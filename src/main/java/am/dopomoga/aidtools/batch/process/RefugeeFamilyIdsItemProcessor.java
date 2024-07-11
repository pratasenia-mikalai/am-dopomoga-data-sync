package am.dopomoga.aidtools.batch.process;

import am.dopomoga.aidtools.model.document.RefugeeDocument;
import am.dopomoga.aidtools.service.RefugeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;

import java.util.*;

@RequiredArgsConstructor
public class RefugeeFamilyIdsItemProcessor implements ItemProcessor<RefugeeDocument, RefugeeDocument> {

    private final RefugeeService service;

    @Override
    public RefugeeDocument process(RefugeeDocument item) throws Exception {
        List<RefugeeDocument> family = service.findFamilyByAirtableOriginalFamilyIds(item.getAirtableOriginalFamilyIds());

        Set<UUID> familyIds = new HashSet<>();
        family.stream().map(RefugeeDocument::getId).forEach(familyIds::add);
        familyIds.add(item.getId());

        item.setFamilyIds(familyIds);
        return item;
    }
}
