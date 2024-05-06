package am.dopomoga.aidtools.batch;

import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ExecutionContext;

import java.time.LocalDate;

public abstract class StepExecutionContextAccess implements StepExecutionListener {

    private StepExecution stepExecution;

    public void beforeStep(StepExecution stepExecution) {
        this.stepExecution = stepExecution;
    }

    protected String getLastModifiedDate() {
        return this.stepExecution.getJobExecution().getJobParameters().getString(JobParameters.LAST_MODIFIED_DATE, null);
    }

    protected ExecutionContext stepExecutionContext() {
        return this.stepExecution.getExecutionContext();
    }


    protected String getAirtableTableOffset() {
        return this.stepExecution.getExecutionContext().getString(JobParameters.AIRTABLE_TABLE_OFFSET, null);
    }

    protected void setAirtableTableOffset(String offset) {
        this.stepExecution.getExecutionContext().put(JobParameters.AIRTABLE_TABLE_OFFSET, offset);
    }

    protected String getAirtableDatabaseId() {
        return this.stepExecution.getExecutionContext().getString(JobParameters.AIRTABLE_BASE_ID, null);
    }

    protected LocalDate getDatabaseActualStartDate() {
        String rawParam = this.stepExecution.getExecutionContext().getString(JobParameters.ACTUAL_START_DATE, null);
        return rawParam == null ? null : LocalDate.parse(rawParam);
    }

    protected boolean isDatabaseLast() {
        return this.stepExecution.getExecutionContext().containsKey(JobParameters.AIRTABLE_BASE_ID) &&
                !this.stepExecution.getExecutionContext().containsKey(JobParameters.AIRTABLE_NEXT_BASE_ID);
    }

}
