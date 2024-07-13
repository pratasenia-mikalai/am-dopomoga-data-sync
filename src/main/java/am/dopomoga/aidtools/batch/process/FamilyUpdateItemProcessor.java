package am.dopomoga.aidtools.batch.process;

import am.dopomoga.aidtools.airtable.dto.RefugeeFamilyDto;
import am.dopomoga.aidtools.airtable.dto.TableDataSaveDto;
import am.dopomoga.aidtools.model.document.RefugeeDocument;
import am.dopomoga.aidtools.service.RefugeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;

import java.util.List;

@RequiredArgsConstructor
public class FamilyUpdateItemProcessor implements ItemProcessor<RefugeeDocument, TableDataSaveDto<RefugeeFamilyDto>>{

    private final RefugeeService service;

    @Override
    public TableDataSaveDto<RefugeeFamilyDto> process(RefugeeDocument item) throws Exception {
        List<String> newFamilyIds = service.findFamilyNewAirtableIds(item);
        return new TableDataSaveDto<RefugeeFamilyDto>().apply(it -> {
                    it.setId(item.getNewAirtableId());
                    it.setFields(new RefugeeFamilyDto(newFamilyIds));
                }
        );
    }

}
