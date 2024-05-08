package am.dopomoga.aidtools.batch.reader;

import am.dopomoga.aidtools.airtable.AirtableRequestUtils;
import am.dopomoga.aidtools.airtable.dto.TableDataDto;
import am.dopomoga.aidtools.airtable.dto.response.AbstractAirtableTableResponse;
import am.dopomoga.aidtools.airtable.dto.response.AirtableTableListFunction;
import am.dopomoga.aidtools.batch.JobParameters;
import am.dopomoga.aidtools.batch.StepExecutionContextAccess;
import am.dopomoga.aidtools.controller.dto.AirtableDatabaseApiModel;
import am.dopomoga.aidtools.service.AirtableDatabaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.*;

import java.util.Iterator;
import java.util.NoSuchElementException;

@Slf4j
@RequiredArgsConstructor
public class AirtableRestItemReader<T> extends StepExecutionContextAccess implements ItemReader<TableDataDto<T>>, StepExecutionListener {

    private final String stepName;
    private final AirtableTableListFunction<T> dataSupplyingMethod;
    private final AirtableDatabaseService databaseService;

    private Iterator<TableDataDto<T>> dataIterator;
    private String nextBatchOffset = null;
    private boolean lastRestBatch = false;
    private int databaseRecordCounter = 0;

    @Override
    public TableDataDto<T> read() throws UnexpectedInputException, ParseException, NonTransientResourceException {
        if (this.dataIterator == null || !dataIterator.hasNext()) {
            if (this.lastRestBatch && isDatabaseLast()) {
                logRecordsNumber();
                return null;
            }

            if (getAirtableDatabaseId() == null || this.lastRestBatch) {
                loadNextAirtableDatabaseStepContext();
            }
            // save offset to context when all data from previous batch are processed
            if (this.nextBatchOffset != null && !this.nextBatchOffset.isEmpty()) {
                setAirtableTableOffset(this.nextBatchOffset);
            }

            AbstractAirtableTableResponse<T> response = null;
            boolean recordsExist = false;
            do {
                String databaseId = getAirtableDatabaseId();
                String offset = getAirtableTableOffset();
                String lastModifiedDate = getLastModifiedDate();

                response = this.dataSupplyingMethod.get(databaseId, offset,
                        AirtableRequestUtils.lastModifiedDateParam(lastModifiedDate));

                recordsExist = response.getRecords() != null && !response.getRecords().isEmpty();
                if (!recordsExist) {
                    loadNextAirtableDatabaseStepContext();
                }
            } while (!recordsExist && !isDatabaseLast());

            if (!recordsExist) {
                return null;
            }

            this.nextBatchOffset = response.getOffset();

            if (this.nextBatchOffset == null || this.nextBatchOffset.isEmpty()) {
                this.lastRestBatch = true;
            }
            this.dataIterator = response.getRecords().iterator();
        }

        if (dataIterator.hasNext()) {
            this.databaseRecordCounter++;
            return dataIterator.next()
                    .apply(it -> it.setActualStartDateFromDatabase(getDatabaseActualStartDate()));
        }
        return null;
    }

    private void loadNextAirtableDatabaseStepContext() {
        this.lastRestBatch = false;
        logRecordsNumber();

        ExecutionContext jobExecutionContext = stepExecutionContext();
        jobExecutionContext.remove(JobParameters.AIRTABLE_TABLE_OFFSET);

        AirtableDatabaseApiModel nextBase;
        if (!jobExecutionContext.containsKey(JobParameters.AIRTABLE_BASE_ID)) {
            nextBase = databaseService.getFirstNonProcessedBase();
        } else {
            String nextBaseId = jobExecutionContext.getString(JobParameters.AIRTABLE_NEXT_BASE_ID);
            nextBase = databaseService.getDatabase(nextBaseId);
        }
        if (nextBase == null) {
            throw new NoSuchElementException();
        }

        jobExecutionContext.put(JobParameters.AIRTABLE_BASE_ID, nextBase.id());
        jobExecutionContext.put(JobParameters.ACTUAL_START_DATE, nextBase.actualStartDate().toString());
        if (nextBase.nextDatabaseId() != null) {
            jobExecutionContext.put(JobParameters.AIRTABLE_NEXT_BASE_ID, nextBase.nextDatabaseId());
        } else {
            jobExecutionContext.remove(JobParameters.AIRTABLE_NEXT_BASE_ID);
        }
        log.info("Reading from database \"{}\"", nextBase.name());
    }

    private void logRecordsNumber() {
        log.info("{}. Read records: {}", this.stepName, this.databaseRecordCounter);
        this.databaseRecordCounter = 0;
    }

}
