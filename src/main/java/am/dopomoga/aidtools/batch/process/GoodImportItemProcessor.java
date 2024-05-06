package am.dopomoga.aidtools.batch.process;

import am.dopomoga.aidtools.airtable.dto.GoodDto;
import am.dopomoga.aidtools.airtable.dto.TableDataDto;
import am.dopomoga.aidtools.batch.StepExecutionContextAccess;
import am.dopomoga.aidtools.model.document.GoodDocument;
import am.dopomoga.aidtools.model.mapper.ModelMapper;
import am.dopomoga.aidtools.service.GoodService;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GoodImportItemProcessor extends StepExecutionContextAccess implements ItemProcessor<TableDataDto<GoodDto>, GoodDocument> {

    private final ModelMapper mapper;
    private final GoodService service;

    @Override
    public GoodDocument process(final TableDataDto<GoodDto> item) throws Exception {
        if (item.getFields().name() == null || item.getFields().name().isBlank()) {
            return null;
        }

        return item.getFields()
                .let(it -> mapper.map(it, item.getActualStartDateFromDatabase()))
                .apply(it -> it.addOriginAirtableId(item.getId()))
                .let(service::prepareFromRawAirtableModel);
    }
}
