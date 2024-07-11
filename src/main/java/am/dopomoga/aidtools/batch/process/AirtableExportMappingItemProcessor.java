package am.dopomoga.aidtools.batch.process;

import am.dopomoga.aidtools.airtable.dto.TableDataSaveDto;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;

import java.util.function.Function;

@RequiredArgsConstructor
public class AirtableExportMappingItemProcessor<I, O> implements ItemProcessor<I, TableDataSaveDto<O>> {

    private final Function<I, O> mappingFunction;

    @Override
    public TableDataSaveDto<O> process(I item) throws Exception {
        O fields = mappingFunction.apply(item);
        return new TableDataSaveDto<O>().apply(it -> it.setFields(fields));
    }

}
