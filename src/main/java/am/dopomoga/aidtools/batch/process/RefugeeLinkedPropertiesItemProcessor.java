package am.dopomoga.aidtools.batch.process;

import am.dopomoga.aidtools.model.document.RefugeeDocument;
import am.dopomoga.aidtools.model.document.SupportDocument;
import am.dopomoga.aidtools.service.RefugeeService;
import am.dopomoga.aidtools.service.SupportService;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;

import java.util.*;

@RequiredArgsConstructor
public class RefugeeLinkedPropertiesItemProcessor implements ItemProcessor<RefugeeDocument, RefugeeDocument> {

    private final RefugeeService refugeeService;
    private final SupportService supportService;

    @Override
    public RefugeeDocument process(RefugeeDocument item) throws Exception {
        List<RefugeeDocument> family = refugeeService.findFamilyByAirtableOriginalFamilyIds(item.getAirtableOriginalFamilyIds());

        Set<UUID> familyIds = new HashSet<>();
        family.stream().map(RefugeeDocument::getId).forEach(familyIds::add);
        familyIds.add(item.getId());
        item.setFamilyIds(familyIds);

        List<SupportDocument> support = supportService.getSupportsByRefugee(item.getId());
        if (!support.isEmpty()) {
            item.setCardHolder(true);
        }
        return item;
    }
}
