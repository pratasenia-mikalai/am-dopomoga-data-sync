package am.dopomoga.aidtools.batch.configuartion;

import am.dopomoga.aidtools.airtable.dto.TableDataDto;
import am.dopomoga.aidtools.airtable.dto.response.AirtableTableListFunction;
import am.dopomoga.aidtools.airtable.restclient.AirtableTablesClient;
import am.dopomoga.aidtools.batch.process.GoodImportItemProcessor;
import am.dopomoga.aidtools.batch.process.RefugeeImportItemProcessor;
import am.dopomoga.aidtools.batch.reader.AirtableRestItemReader;
import am.dopomoga.aidtools.service.AirtableDatabaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.data.builder.MongoItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class SpringBatchConfiguration extends DefaultBatchConfiguration {

    private final AirtableTablesClient airtableTablesClient;
    private final AirtableDatabaseService airtableDatabaseService;
    private final MongoTemplate mongoTemplate;

    @Bean
    public Job testJob(JobRepository jobRepository,
                       GoodImportItemProcessor goodImportItemProcessor,
                       RefugeeImportItemProcessor refugeeImportItemProcessor) {
        Step goodsImport = airtableTableDataStep("GoodsImport", airtableTablesClient::getGoods,
                goodImportItemProcessor, jobRepository);
        Step refugeesImport = airtableTableDataStep("RefugeesImport", airtableTablesClient::getRefugees,
                refugeeImportItemProcessor, jobRepository);

        return new JobBuilder("AirtableDataImportJob", jobRepository)
                .start(goodsImport)
                .next(refugeesImport)

                .build();
    }

    private <I, O> Step airtableTableDataStep(String name,
                                              AirtableTableListFunction<I> dataSupplyingMethod,
                                              ItemProcessor<TableDataDto<I>, O> processor,
                                              JobRepository jobRepository) {
        return new StepBuilder(name, jobRepository)
                .<TableDataDto<I>, O>chunk(10, getTransactionManager())
                .reader(new AirtableRestItemReader<>(name, dataSupplyingMethod, this.airtableDatabaseService))
                .processor(processor)
                .writer(
                        new MongoItemWriterBuilder<O>()
                        .template(this.mongoTemplate)
                       .build()
                )
                .readerIsTransactionalQueue()
                .allowStartIfComplete(true)
                .build();
    }

    @Bean
    public JobLauncher jobLauncher(JobRepository jobRepository) throws Exception {
        TaskExecutorJobLauncher jobLauncher = new TaskExecutorJobLauncher();
        jobLauncher.setJobRepository(jobRepository);
        jobLauncher.setTaskExecutor(new SimpleAsyncTaskExecutor());
        jobLauncher.afterPropertiesSet();
        return jobLauncher;
    }

    @Override
    public String getTablePrefix() {
        return "BATCH.BATCH_";
    }

}
