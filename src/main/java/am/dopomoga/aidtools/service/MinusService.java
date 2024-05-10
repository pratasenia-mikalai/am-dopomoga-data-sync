package am.dopomoga.aidtools.service;

import am.dopomoga.aidtools.model.document.GoodDocument;
import am.dopomoga.aidtools.model.document.MinusDocument;
import am.dopomoga.aidtools.model.document.SupportDocument;
import am.dopomoga.aidtools.repository.mongo.MinusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MinusService {

    private final MinusRepository minusRepository;
    private final GoodService goodService;
    private final SupportService supportService;

    public MinusDocument prepareFromRawAirtableModel(MinusDocument raw) {
        MinusDocument existingMinus = minusRepository.findByOriginAirtableId(raw.getOriginAirtableId());

        GoodDocument good = goodService.findByOriginAirtableId(raw.getOriginAirtableGoodId());
        SupportDocument support = supportService.findByOriginAirtableId(raw.getOriginAirtableSupportId());

        raw.setGoodId(good.getId());
        raw.setSupportId(support.getId());

        if (existingMinus == null) {
            return raw.apply(MinusDocument::fillFieldsOnCreate);
        }
        return raw.apply(it -> it.copyFieldsAndFillOnUpdate(existingMinus));
    }

}
