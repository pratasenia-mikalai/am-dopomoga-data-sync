package am.dopomoga.aidtools.batch.configuartion;

import am.dopomoga.aidtools.airtable.dto.AirtableBlankableItem;
import am.dopomoga.aidtools.airtable.dto.TableDataDto;
import am.dopomoga.aidtools.airtable.dto.response.AirtableTableListFunction;
import am.dopomoga.aidtools.airtable.restclient.AirtableTablesClient;
import am.dopomoga.aidtools.batch.process.AirtableImportItemProcessor;
import am.dopomoga.aidtools.batch.process.RefugeeFamilyIdsItemProcessor;
import am.dopomoga.aidtools.batch.reader.AirtableRestItemReader;
import am.dopomoga.aidtools.model.document.AbstractDocument;
import am.dopomoga.aidtools.model.document.RefugeeDocument;
import am.dopomoga.aidtools.model.mapper.ModelMapper;
import am.dopomoga.aidtools.service.*;
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
import org.springframework.batch.item.data.builder.MongoItemReaderBuilder;
import org.springframework.batch.item.data.builder.MongoItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.time.LocalDate;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.UnaryOperator;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class SpringBatchConfiguration extends DefaultBatchConfiguration {

    private final AirtableTablesClient airtableTablesClient;
    private final AirtableDatabaseService airtableDatabaseService;
    private final MongoTemplate mongoTemplate;

    @Bean
    public Job importJob(JobRepository jobRepository,
                         ModelMapper mapper,
                         GoodService goodService,
                         RefugeeService refugeeService,
                         SupportService supportService,
                         MinusService minusService,
                         PlusService plusService,
                         RefugeeFamilyIdsItemProcessor refugeeFamilyIdsItemProcessor) {

        Step goodsImport = airtableTableDataImportStep("GoodsImport", airtableTablesClient::getGoods,
                importItemProcessorDatabaseDateAware(mapper::map, goodService::prepareFromRawAirtableModel),
                jobRepository);

        Step refugeesImport = airtableTableDataImportStep("RefugeesImport", airtableTablesClient::getRefugees,
                importItemProcessor(mapper::map, refugeeService::prepareFromRawAirtableModel),
                jobRepository);

        Step refugeesFamilyIds = fillRefugeesFamilyIdsStep(refugeeFamilyIdsItemProcessor, jobRepository);

        Step supportImport = airtableTableDataImportStep("SupportImport", airtableTablesClient::getSupport,
                importItemProcessor(mapper::map, supportService::prepareFromRawAirtableModel),
                jobRepository);

        Step minusImport = airtableTableDataImportStep("MinusImport", airtableTablesClient::getMinus,
                importItemProcessor(mapper::map, minusService::prepareFromRawAirtableModel),
                jobRepository);

        Step plusImport = airtableTableDataImportStep("PlusImport", airtableTablesClient::getPlus,
                importItemProcessor(mapper::map, plusService::prepareFromRawAirtableModel),
                jobRepository);

        return new JobBuilder("AirtableDataImportJob", jobRepository)
                .start(goodsImport)
                .next(refugeesImport)
                .next(refugeesFamilyIds)
                .next(supportImport)
                .next(minusImport)
                .next(plusImport)

                .build();
    }

    private Step fillRefugeesFamilyIdsStep(RefugeeFamilyIdsItemProcessor processor, JobRepository jobRepository) {
        return new StepBuilder("RefugeesFamilyIds", jobRepository)
                .<RefugeeDocument, RefugeeDocument>chunk(10, getTransactionManager())
                .reader(
                        new MongoItemReaderBuilder<RefugeeDocument>()
                                .name("RefugeesFamilyIdsStepReader")
                                .pageSize(10)
                                .template(this.mongoTemplate)
                                .targetType(RefugeeDocument.class)
                                .query(Query.query(new Criteria()))
                                .sorts(Map.of("id", Sort.Direction.ASC))
                                .build()
                )
                .processor(processor)
                .writer(
                        new MongoItemWriterBuilder<RefugeeDocument>()
                                .template(this.mongoTemplate)
                                .build()
                )
                .readerIsTransactionalQueue()
                .allowStartIfComplete(true)
                .build();
    }

    private <I extends AirtableBlankableItem, O extends AbstractDocument<O>> Step airtableTableDataImportStep(
            String name,
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

    private <I extends AirtableBlankableItem, O extends AbstractDocument<O>> ItemProcessor<TableDataDto<I>, O> importItemProcessor(
            Function<I, O> simpleDataMapper,
            UnaryOperator<O> beforeSave
    ) {
        return new AirtableImportItemProcessor<>(simpleDataMapper, null, beforeSave);
    }

    private <I extends AirtableBlankableItem, O extends AbstractDocument<O>> ItemProcessor<TableDataDto<I>, O> importItemProcessorDatabaseDateAware(
            BiFunction<I, LocalDate, O> dataMapperWithDatabaseDate,
            UnaryOperator<O> beforeSave
    ) {
        return new AirtableImportItemProcessor<>(null, dataMapperWithDatabaseDate, beforeSave);
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
