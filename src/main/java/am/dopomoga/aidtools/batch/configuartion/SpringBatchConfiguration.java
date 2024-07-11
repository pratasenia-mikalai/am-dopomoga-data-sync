package am.dopomoga.aidtools.batch.configuartion;

import am.dopomoga.aidtools.airtable.dto.AirtableBlankableItem;
import am.dopomoga.aidtools.airtable.dto.TableDataDto;
import am.dopomoga.aidtools.airtable.dto.TableDataSaveDto;
import am.dopomoga.aidtools.airtable.dto.request.AirtableTableSaveFunction;
import am.dopomoga.aidtools.airtable.dto.response.AbstractAirtableTableResponse;
import am.dopomoga.aidtools.airtable.dto.response.AirtableTableListFunction;
import am.dopomoga.aidtools.airtable.restclient.AirtableTablesReadClient;
import am.dopomoga.aidtools.airtable.restclient.AirtableTablesWriteClient;
import am.dopomoga.aidtools.batch.process.AirtableExportMappingItemProcessor;
import am.dopomoga.aidtools.batch.process.AirtableImportItemProcessor;
import am.dopomoga.aidtools.batch.process.RefugeeFamilyIdsItemProcessor;
import am.dopomoga.aidtools.batch.reader.AirtableRestItemReader;
import am.dopomoga.aidtools.batch.writer.AirtableRestItemWriter;
import am.dopomoga.aidtools.model.document.AbstractDocument;
import am.dopomoga.aidtools.model.document.GoodDocument;
import am.dopomoga.aidtools.model.document.RefugeeDocument;
import am.dopomoga.aidtools.model.mapper.ModelMapper;
import am.dopomoga.aidtools.service.*;
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
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.data.builder.MongoItemReaderBuilder;
import org.springframework.batch.item.data.builder.MongoItemWriterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;

@Configuration
@EnableBatchProcessing
public class SpringBatchConfiguration extends DefaultBatchConfiguration {

    @Autowired
    private AirtableTablesReadClient airtableTablesReadClient;
    @Autowired
    private AirtableTablesWriteClient airtableTablesWriteClient;
    @Autowired
    private AirtableDatabaseService airtableDatabaseService;
    @Autowired
    private MongoTemplate mongoTemplate;

    @Value("${airtable.api-retry-delay-seconds}")
    private Long airtableApiRetryDelaySeconds;
    @Value("${airtable.api-request-time-interval-millis}")
    private Long airtableApiRequestIntervalMillis;

    @Bean
    public Job importJob(JobRepository jobRepository,
                         ModelMapper mapper,
                         GoodService goodService,
                         RefugeeService refugeeService,
                         SupportService supportService,
                         MinusService minusService,
                         PlusService plusService) {

        Step goodsImport = airtableTableDataImportStep("GoodsImport", airtableTablesReadClient::getGoods,
                importItemProcessorDatabaseDateAware(mapper::map, goodService::prepareFromRawAirtableModel),
                jobRepository);

        Step refugeesImport = airtableTableDataImportStep("RefugeesImport", airtableTablesReadClient::getRefugees,
                importItemProcessor(mapper::map, refugeeService::prepareFromRawAirtableModel),
                jobRepository);

        Step refugeesFamilyIds = fillRefugeesFamilyIdsStep(refugeeService, jobRepository);

        Step supportImport = airtableTableDataImportStep("SupportImport", airtableTablesReadClient::getSupport,
                importItemProcessor(mapper::map, supportService::prepareFromRawAirtableModel),
                jobRepository);

        Step minusImport = airtableTableDataImportStep("MinusImport", airtableTablesReadClient::getMinus,
                importItemProcessor(mapper::map, minusService::prepareFromRawAirtableModel),
                jobRepository);

        Step plusImport = airtableTableDataImportStep("PlusImport", airtableTablesReadClient::getPlus,
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

    @Bean
    public Job exportJob(JobRepository jobRepository,
                         ModelMapper mapper,
                         GoodService goodService,
                         RefugeeService refugeeService) {

        Step goodsExport = airtableTableDataExportStep("GoodExport", GoodDocument.class, mapper::map,
                airtableTablesWriteClient::saveGoods, null,
                jobRepository);


        Step refugeesExport = airtableTableDataExportStep("RefugeesExport", RefugeeDocument.class, mapper::mapWithoutFamily,
                airtableTablesWriteClient::saveRefugees, null, // TODO save new IDs
                jobRepository);


        return new JobBuilder("AirtableDataExportJob", jobRepository)
                .start(goodsExport)
                .next(refugeesExport)

                .build();
    }

    private Step fillRefugeesFamilyIdsStep(RefugeeService refugeeService, JobRepository jobRepository) {
        return new StepBuilder("RefugeesFamilyIds", jobRepository)
                .<RefugeeDocument, RefugeeDocument>chunk(10, getTransactionManager())
                .reader(mongoItemReader(RefugeeDocument.class))
                .processor(new RefugeeFamilyIdsItemProcessor(refugeeService))
                .writer(
                        new MongoItemWriterBuilder<RefugeeDocument>()
                                .template(this.mongoTemplate)
                                .build()
                )
                .readerIsTransactionalQueue()
                .allowStartIfComplete(true)
                .build();
    }

    private <I, O> Step airtableTableDataImportStep(
            String name,
            AirtableTableListFunction<I> dataSupplyingMethod,
            ItemProcessor<TableDataDto<I>, O> processor,
            JobRepository jobRepository) {
        return new StepBuilder(name, jobRepository)
                .<TableDataDto<I>, O>chunk(10, getTransactionManager())
                .reader(new AirtableRestItemReader<>(name, dataSupplyingMethod, this.airtableDatabaseService, this.airtableApiRetryDelaySeconds))
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

    private <I, O, R> Step airtableTableDataExportStep(
            String name,
            Class<I> mongoDocumentClass,
            Function<I, O> mappingFunction,
            AirtableTableSaveFunction<O, R> dataSaveMethod,
            Consumer<AbstractAirtableTableResponse<R>> responseItemPostProcessor,
            JobRepository jobRepository
    ) {
        return new StepBuilder(name, jobRepository)
                .<I, TableDataSaveDto<O>>chunk(10, getTransactionManager())
                .reader(mongoItemReader(mongoDocumentClass))
                .processor(new AirtableExportMappingItemProcessor<>(mappingFunction))
                .writer(
                    new AirtableRestItemWriter<>(this.airtableApiRequestIntervalMillis, dataSaveMethod, responseItemPostProcessor)
                )
                .readerIsTransactionalQueue()
                .allowStartIfComplete(true)
                .build();
    }

    private <T> ItemReader<T> mongoItemReader(Class<T> documentClass) {
        return new MongoItemReaderBuilder<T>()
                .name("RefugeesFamilyIdsStepReader")
                .pageSize(10)
                .template(this.mongoTemplate)
                .targetType(documentClass)
                .query(Query.query(new Criteria()))
                .sorts(Map.of("id", Sort.Direction.ASC))
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
