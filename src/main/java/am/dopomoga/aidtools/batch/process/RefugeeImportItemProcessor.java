package am.dopomoga.aidtools.batch.process;

import am.dopomoga.aidtools.airtable.dto.RefugeeDto;
import am.dopomoga.aidtools.airtable.dto.TableDataDto;
import am.dopomoga.aidtools.model.document.RefugeeDocument;
import am.dopomoga.aidtools.model.mapper.ModelMapper;
import am.dopomoga.aidtools.service.RefugeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RefugeeImportItemProcessor implements ItemProcessor<TableDataDto<RefugeeDto>, RefugeeDocument> {

    private final ModelMapper mapper;
    private final RefugeeService service;

    @Override
    public RefugeeDocument process(TableDataDto<RefugeeDto> item) throws Exception {
        if (item.getFields().name() == null || item.getFields().name().isBlank()) {
            return null;
        }

        return item.getFields()
                .let(mapper::map)
                .apply(it -> it.addOriginAirtableId(item.getId()))
                .let(service::prepareFromRawAirtableModel);
    }

}
