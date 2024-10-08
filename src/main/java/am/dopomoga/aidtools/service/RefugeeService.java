package am.dopomoga.aidtools.service;

import am.dopomoga.aidtools.airtable.dto.RefugeeDto;
import am.dopomoga.aidtools.airtable.dto.TableDataDto;
import am.dopomoga.aidtools.model.document.RefugeeDocument;
import am.dopomoga.aidtools.repository.mongo.RefugeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RefugeeService {

    private final RefugeeRepository refugeeRepository;

    public RefugeeDocument prepareFromRawAirtableModel(RefugeeDocument raw) {
        RefugeeDocument existingRefugee = refugeeRepository.findByNameAndDateOfBirth(raw.getName(), raw.getDateOfBirth());

        if (existingRefugee == null) {
            return raw.apply(RefugeeDocument::fillFieldsOnCreate);
        }
        return raw.apply(it -> {
            it.copyFieldsAndFillOnUpdate(existingRefugee);
            existingRefugee.getOriginAirtableIds().forEach(it::addOriginAirtableId);
        });
    }

    public List<RefugeeDocument> findFamilyByAirtableOriginalFamilyIds(List<String> airtableOriginalFamilyIds) {
        return refugeeRepository.findByOriginAirtableIdsIn(airtableOriginalFamilyIds);
    }

    public RefugeeDocument findByOriginAirtableId(String originAirtableId) {
        return refugeeRepository.findByOriginAirtableIds(originAirtableId);
    }

    public void saveNewAirtableId(TableDataDto<RefugeeDto> dto) {
        RefugeeDocument refugeeDocument = refugeeRepository.findByNameAndDateOfBirth(dto.getFields().name(), dto.getFields().dateOfBirth());
        if (refugeeDocument == null) return;

        refugeeDocument.setNewAirtableId(dto.getId());
        refugeeRepository.save(refugeeDocument);
    }

    public List<String> findFamilyNewAirtableIds(RefugeeDocument refugeeDocument) {
        return refugeeRepository.findAllById(refugeeDocument.getFamilyIds()).stream()
                .map(RefugeeDocument::getNewAirtableId).toList();
    }

}
