package am.dopomoga.aidtools.service;

import am.dopomoga.aidtools.model.document.RefugeeDocument;
import am.dopomoga.aidtools.model.document.SupportDocument;
import am.dopomoga.aidtools.repository.mongo.SupportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SupportService {

    private final SupportRepository supportRepository;
    private final RefugeeService refugeeService;

    public SupportDocument findByOriginAirtableId(String originAirtableId) {
        return supportRepository.findByOriginAirtableId(originAirtableId);
    }

    public List<SupportDocument> getSupportsByRefugee(UUID refugeeId) {
        return supportRepository.findByRefugeeId(refugeeId);
    }

    public SupportDocument prepareFromRawAirtableModel(SupportDocument raw) {
        SupportDocument existingSupport = supportRepository.findByOriginAirtableId(raw.getOriginAirtableId());

        RefugeeDocument refugee = refugeeService.findByOriginAirtableId(raw.getOriginAirtableRefugeeId());

        raw.setRefugeeId(refugee.getId());

        if (existingSupport == null) {
            return raw.apply(SupportDocument::fillFieldsOnCreate);
        }
        return raw.apply(it -> it.copyFieldsAndFillOnUpdate(existingSupport));
    }

}
