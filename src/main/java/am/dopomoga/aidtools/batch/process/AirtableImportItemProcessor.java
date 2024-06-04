package am.dopomoga.aidtools.batch.process;

import am.dopomoga.aidtools.airtable.dto.AirtableBlankableItem;
import am.dopomoga.aidtools.airtable.dto.TableDataDto;
import am.dopomoga.aidtools.model.document.AbstractDocument;
import org.springframework.batch.item.ItemProcessor;

import java.time.LocalDate;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public class AirtableImportItemProcessor<I extends AirtableBlankableItem, O extends AbstractDocument<O>> implements ItemProcessor<TableDataDto<I>, O> {

    private final Function<I, O> simpleDataMapper;
    private final BiFunction<I, LocalDate, O> dataMapperWithDatabaseDate;
    private final UnaryOperator<O> beforeSave;

    public AirtableImportItemProcessor(Function<I, O> simpleDataMapper, BiFunction<I, LocalDate, O> dataMapperWithDatabaseDate, UnaryOperator<O> beforeSave) {
        this.simpleDataMapper = simpleDataMapper;
        this.dataMapperWithDatabaseDate = dataMapperWithDatabaseDate;
        this.beforeSave = beforeSave;
    }

    @Override
    public O process(TableDataDto<I> item) {
        if (item.getFields().isBlank()) {
            return null;
        }

        O mappedItem = dataMapperWithDatabaseDate != null
                ? dataMapperWithDatabaseDate.apply(item.getFields(), item.getActualStartDateFromDatabase())
                : simpleDataMapper.apply(item.getFields());

        return mappedItem
                .apply(it -> it.setOriginAirtableId(item.getId()))
                .let(beforeSave);
    }
}
