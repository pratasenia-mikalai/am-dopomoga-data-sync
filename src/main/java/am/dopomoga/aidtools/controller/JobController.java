package am.dopomoga.aidtools.controller;

import am.dopomoga.aidtools.batch.BatchJobParameters;
import am.dopomoga.aidtools.controller.dto.ExportJobRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v0")
public class JobController {

    private final JobLauncher jobLauncher;
    private final Job importJob;
    private final Job exportJob;

    @RequestMapping(method = RequestMethod.POST, value = "/import")
    public ResponseEntity<?> importJob() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        jobLauncher.run(importJob, new JobParameters(new HashMap<>()));
        return ResponseEntity.ok().build();
    }

    @RequestMapping(method = RequestMethod.POST, value = "/export")
    public ResponseEntity<?> exportJob(@RequestBody ExportJobRequest request) throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        JobParameters jobParameters = new JobParametersBuilder()
                .addString(BatchJobParameters.AIRTABLE_BASE_ID, request.airtableBaseId())
                .toJobParameters();
        jobLauncher.run(exportJob, jobParameters);
        return ResponseEntity.ok().build();
    }

}
