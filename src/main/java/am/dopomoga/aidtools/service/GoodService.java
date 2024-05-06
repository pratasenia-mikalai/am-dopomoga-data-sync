package am.dopomoga.aidtools.service;

import am.dopomoga.aidtools.model.document.GoodDocument;
import am.dopomoga.aidtools.repository.mongo.GoodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GoodService {

    private final GoodRepository goodRepository;

    public List<GoodDocument> findByNameId(String nameId) {
        return goodRepository.findByNameId(nameId);
    }

    public GoodDocument prepareFromRawAirtableModel(GoodDocument raw) {
        GoodDocument existingGood = goodRepository.findByNameIdAndType(raw.getNameId(), raw.getType());
        if (existingGood == null) {
            return raw.apply(this::fillFieldsOnCreate);
        }
        return raw.apply(it -> {
            it.setId(existingGood.getId());
            existingGood.getOriginAirtableIds().forEach(it::addOriginAirtableId);
            existingGood.getUnitEstimatedPrices().forEach(it::addUnitEstimatedPrice);
            it.setCreatedDate(existingGood.getCreatedDate());
            it.setLastUpdatedDate(OffsetDateTime.now());
        });
    }

    private void fillFieldsOnCreate(GoodDocument good) {
        good.setId(UUID.randomUUID());
        good.setCreatedDate(OffsetDateTime.now());
        good.setLastUpdatedDate(OffsetDateTime.now());
    }
}
