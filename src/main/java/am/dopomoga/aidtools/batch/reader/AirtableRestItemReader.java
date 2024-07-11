package am.dopomoga.aidtools.batch.reader;

import am.dopomoga.aidtools.airtable.AirtableRequestUtils;
import am.dopomoga.aidtools.airtable.dto.TableDataDto;
import am.dopomoga.aidtools.airtable.dto.response.AbstractAirtableTableResponse;
import am.dopomoga.aidtools.airtable.dto.response.AirtableTableListFunction;
import am.dopomoga.aidtools.batch.BatchJobParameters;
import am.dopomoga.aidtools.batch.StepExecutionContextAccess;
import am.dopomoga.aidtools.controller.dto.AirtableDatabaseApiModel;
import am.dopomoga.aidtools.service.AirtableDatabaseService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.*;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
public class AirtableRestItemReader<T> extends StepExecutionContextAccess implements ItemReader<TableDataDto<T>> {

    private final String stepName;
    private final AirtableTableListFunction<T> dataSupplyingMethod;
    private final AirtableDatabaseService databaseService;
    private final long apiRetryDelaySeconds;

    private Iterator<TableDataDto<T>> dataIterator;
    private String nextBatchOffset = null;
    private boolean lastRestBatch = false;
    private int databaseRecordCounter = 0;

    @Override
    public TableDataDto<T> read() throws UnexpectedInputException, ParseException, NonTransientResourceException, InterruptedException {
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

                response = getDataResponse(databaseId, offset, lastModifiedDate);

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

    private AbstractAirtableTableResponse<T> getDataResponse(String databaseId, String offset, String lastModifiedDate) throws InterruptedException {
        do {
            try {
                return this.dataSupplyingMethod.get(databaseId, offset, AirtableRequestUtils.lastModifiedDateParam(lastModifiedDate));
            } catch (FeignException.TooManyRequests ex) {
                TimeUnit.SECONDS.sleep(this.apiRetryDelaySeconds);
            }
        } while (true);
    }

    private void loadNextAirtableDatabaseStepContext() {
        this.lastRestBatch = false;
        logRecordsNumber();

        ExecutionContext stepExecutionContext = stepExecutionContext();
        stepExecutionContext.remove(BatchJobParameters.AIRTABLE_TABLE_OFFSET);

        AirtableDatabaseApiModel nextBase;
        if (!stepExecutionContext.containsKey(BatchJobParameters.AIRTABLE_BASE_ID)) {
            nextBase = databaseService.getFirstNonProcessedBase();
        } else {
            String nextBaseId = stepExecutionContext.getString(BatchJobParameters.AIRTABLE_NEXT_BASE_ID);
            nextBase = databaseService.getDatabase(nextBaseId);
        }
        if (nextBase == null) {
            throw new NoSuchElementException();
        }

        stepExecutionContext.put(BatchJobParameters.AIRTABLE_BASE_ID, nextBase.id());
        stepExecutionContext.put(BatchJobParameters.ACTUAL_START_DATE, nextBase.actualStartDate().toString());
        if (nextBase.nextDatabaseId() != null) {
            stepExecutionContext.put(BatchJobParameters.AIRTABLE_NEXT_BASE_ID, nextBase.nextDatabaseId());
        } else {
            stepExecutionContext.remove(BatchJobParameters.AIRTABLE_NEXT_BASE_ID);
        }
        log.info("Reading from database \"{}\"", nextBase.name());
    }

    private void logRecordsNumber() {
        log.info("{}. Read records: {}", this.stepName, this.databaseRecordCounter);
        this.databaseRecordCounter = 0;
    }

}
