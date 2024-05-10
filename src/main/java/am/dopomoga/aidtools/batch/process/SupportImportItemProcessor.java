package am.dopomoga.aidtools.batch.process;

import am.dopomoga.aidtools.airtable.dto.SupportDto;
import am.dopomoga.aidtools.airtable.dto.TableDataDto;
import am.dopomoga.aidtools.model.document.SupportDocument;
import am.dopomoga.aidtools.model.mapper.ModelMapper;
import am.dopomoga.aidtools.service.SupportService;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SupportImportItemProcessor implements ItemProcessor<TableDataDto<SupportDto>, SupportDocument> {

    private final ModelMapper mapper;
    private final SupportService supportService;

    @Override
    public SupportDocument process(TableDataDto<SupportDto> item) throws Exception {
        if (item.getFields().date() == null || item.getFields().refugeeIds() == null ||
                item.getFields().refugeeIds().isEmpty()) {
            return null;
        }

        return item.getFields()
                .let(mapper::map)
                .apply(it -> it.setOriginAirtableId(item.getId()))
                .let(supportService::prepareFromRawAirtableModel);
    }
}
