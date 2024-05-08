package am.dopomoga.aidtools.model.mapper;

import am.dopomoga.aidtools.airtable.dto.AirtableDatabaseDto;
import am.dopomoga.aidtools.airtable.dto.AirtablePermissionLevel;
import am.dopomoga.aidtools.airtable.dto.GoodDto;
import am.dopomoga.aidtools.airtable.dto.RefugeeDto;
import am.dopomoga.aidtools.controller.dto.AirtableDatabaseApiModel;
import am.dopomoga.aidtools.model.document.GoodDocument;
import am.dopomoga.aidtools.model.document.RefugeeDocument;
import am.dopomoga.aidtools.model.entity.AirtableDatabase;
import am.dopomoga.aidtools.model.entity.PermissionLevel;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class ModelMapper {

    public PermissionLevel map(AirtablePermissionLevel airtablePermissionLevel) {
        return PermissionLevel.valueOf(airtablePermissionLevel.name());
    }

    public AirtableDatabaseApiModel map(final AirtableDatabaseDto dto) {
        return dto.let(it ->
                new AirtableDatabaseApiModel(
                        it.id(),
                        it.name(),
                        map(it.permissionLevel()),
                        null,
                        null,
                        false,
                        null,
                        null
                )
        );
    }

    public AirtableDatabaseApiModel map(final AirtableDatabase entity) {
        return entity.let(it ->
                new AirtableDatabaseApiModel(
                        it.getId(),
                        it.getName(),
                        it.getPermissionLevel(),
                        it.getActualStartDate(),
                        it.getNextBaseId(),
                        it.isFullProcessed(),
                        it.getCreatedDate(),
                        it.getLastUpdatedDate()
                )
        );
    }

    public AirtableDatabase map(final AirtableDatabaseApiModel record) {
        return new AirtableDatabase().apply(it -> {
            it.setId(record.id());
            it.setName(record.name());
            it.setPermissionLevel(record.permissionLevel());
            it.setActualStartDate(record.actualStartDate());
            it.setNextBaseId(record.nextDatabaseId());
            it.setCreatedDate(record.createdDate());
            it.setLastUpdatedDate(record.lastUpdatedDate());
        });
    }

    public GoodDocument map(final GoodDto goodDto, LocalDate actualStartDate) {
        return new GoodDocument().apply(it -> {
            it.setName(goodDto.name());
            it.setType(goodDto.type());
            it.setNameId(goodDto.nameId());
            it.setUnitWeightKg(goodDto.unitWeightKg());
            it.addUnitEstimatedPrice(actualStartDate.toString(), goodDto.unitEstimatedPrice());
        });
    }

    public RefugeeDocument map(final RefugeeDto refugeeDto) {
        return new RefugeeDocument().apply(it -> {
                    it.setName(refugeeDto.name());
                    it.setGender(refugeeDto.gender());
                    it.setDateOfBirth(refugeeDto.dateOfBirth());
                    it.setArrivalDate(refugeeDto.arrivalDate());
                    it.setRefugeeStatus(refugeeDto.refugeeStatus());
                    it.setAirtableOriginalFamilyIds(refugeeDto.familyIds());
                }
        );
    }

    private <E> E firstOrNull(List<E> list) {
        return list == null || list.isEmpty() ? null : list.get(0);
    }

}
