package am.dopomoga.aidtools.batch.process;

import am.dopomoga.aidtools.airtable.dto.MinusDto;
import am.dopomoga.aidtools.airtable.dto.TableDataDto;
import am.dopomoga.aidtools.model.document.MinusDocument;
import am.dopomoga.aidtools.model.mapper.ModelMapper;
import am.dopomoga.aidtools.service.MinusService;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MinusImportItemProcessor implements ItemProcessor<TableDataDto<MinusDto>, MinusDocument> {

    private final ModelMapper mapper;
    private final MinusService minusService;

    @Override
    public MinusDocument process(TableDataDto<MinusDto> item) throws Exception {
        if (item.getFields().goodsId() == null || item.getFields().goodsId().isEmpty() ||
                item.getFields().supportId() == null || item.getFields().supportId().isEmpty()) {
            return null;
        }

        return item.getFields()
                .let(mapper::map)
                .apply(it -> it.setOriginAirtableId(item.getId()))
                .let(minusService::prepareFromRawAirtableModel);
    }
}
