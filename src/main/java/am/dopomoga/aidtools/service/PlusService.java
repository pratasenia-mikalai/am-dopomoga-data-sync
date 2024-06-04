package am.dopomoga.aidtools.service;

import am.dopomoga.aidtools.model.document.GoodDocument;
import am.dopomoga.aidtools.model.document.PlusDocument;
import am.dopomoga.aidtools.repository.mongo.PlusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlusService {

    private final PlusRepository plusRepository;
    private final GoodService goodService;

    public PlusDocument prepareFromRawAirtableModel(PlusDocument raw) {
        PlusDocument existingPlus = plusRepository.findByOriginAirtableId(raw.getOriginAirtableId());

        GoodDocument good = goodService.findByOriginAirtableId(raw.getOriginAirtableGoodId());
        raw.setGoodId(good.getId());

        if (existingPlus == null) {
            return raw.apply(PlusDocument::fillFieldsOnCreate);
        }
        return raw.apply(it -> it.copyFieldsAndFillOnUpdate(existingPlus));
    }

}
