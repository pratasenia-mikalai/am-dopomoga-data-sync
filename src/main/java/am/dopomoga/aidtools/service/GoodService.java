package am.dopomoga.aidtools.service;

import am.dopomoga.aidtools.model.document.GoodDocument;
import am.dopomoga.aidtools.repository.mongo.GoodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GoodService {

    private final GoodRepository goodRepository;

    public List<GoodDocument> findByNameId(String nameId) {
        return goodRepository.findByNameId(nameId);
    }

    public GoodDocument findByOriginAirtableId(String originAirtableId) {
        return goodRepository.findByOriginAirtableIds(originAirtableId);
    }

    public GoodDocument prepareFromRawAirtableModel(GoodDocument raw) {
        GoodDocument existingGood = goodRepository.findByNameIdAndType(raw.getNameId(), raw.getType());
        if (existingGood == null) {
            return raw.apply(GoodDocument::fillFieldsOnCreate);
        }
        return raw.apply(it -> {
            it.copyFieldsAndFillOnUpdate(existingGood);
            existingGood.getOriginAirtableIds().forEach(it::addOriginAirtableId);
            existingGood.getUnitEstimatedPrices().forEach(it::addUnitEstimatedPrice);
        });
    }


}
