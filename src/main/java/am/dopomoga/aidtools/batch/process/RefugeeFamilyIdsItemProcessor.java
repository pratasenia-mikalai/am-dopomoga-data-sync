package am.dopomoga.aidtools.batch.process;

import am.dopomoga.aidtools.model.document.RefugeeDocument;
import am.dopomoga.aidtools.service.RefugeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RefugeeFamilyIdsItemProcessor implements ItemProcessor<RefugeeDocument, RefugeeDocument> {

    private final RefugeeService service;

    @Override
    public RefugeeDocument process(RefugeeDocument item) throws Exception {
        List<RefugeeDocument> family = service.findFamilyByAirtableOriginalFamilyIds(item.getAirtableOriginalFamilyIds());
        List<UUID> familyIds = family.stream().map(RefugeeDocument::getId).toList();
        item.setFamilyIds(familyIds);
        item.getFamilyIds().add(item.getId());
        return item;
    }
}
