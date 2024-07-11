package am.dopomoga.aidtools.batch.writer;

import am.dopomoga.aidtools.airtable.dto.TableDataDto;
import am.dopomoga.aidtools.airtable.dto.TableDataSaveDto;
import am.dopomoga.aidtools.airtable.dto.request.AirtableTableSaveRequest;
import am.dopomoga.aidtools.airtable.dto.request.AirtableTableSaveFunction;
import am.dopomoga.aidtools.airtable.dto.response.AbstractAirtableTableResponse;
import am.dopomoga.aidtools.batch.BatchJobParameters;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

@Slf4j// TODO logging
@RequiredArgsConstructor
public class AirtableRestItemWriter<O, R> implements ItemWriter<TableDataSaveDto<O>>, StepExecutionListener {

    private static AtomicLong LAST_REQUEST_TIME = new AtomicLong(System.currentTimeMillis());
    private JobParameters parameters;

    private final long requestIntervalMillis;
    private final AirtableTableSaveFunction<O, R> restWriteFunction;
    /**
     * Nullable
     */
    private final Consumer<TableDataDto<R>> responseItemPostProcessor;

    @Override
    public void beforeStep(StepExecution stepExecution) {
        this.parameters = stepExecution.getJobExecution().getJobParameters();
    }

    @Override
    public void write(Chunk<? extends TableDataSaveDto<O>> chunk) throws Exception {
        var apiSaveRequest = new AirtableTableSaveRequest<O>();
        apiSaveRequest.setRecords((List<TableDataSaveDto<O>>) chunk.getItems());

        long timeToWait = LAST_REQUEST_TIME.get() + requestIntervalMillis - System.currentTimeMillis();
        if (timeToWait > 0) {
            Thread.sleep(timeToWait);
        }
        LAST_REQUEST_TIME.set(System.currentTimeMillis());
        AbstractAirtableTableResponse<R> response = restWriteFunction.send(parameters.getString(BatchJobParameters.AIRTABLE_BASE_ID), apiSaveRequest);

        if (responseItemPostProcessor != null) {
            response.getRecords().forEach(responseItemPostProcessor);
        }
    }
}
