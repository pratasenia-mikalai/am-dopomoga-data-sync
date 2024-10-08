package am.dopomoga.aidtools.model.mapper;

import am.dopomoga.aidtools.airtable.dto.*;
import am.dopomoga.aidtools.airtable.dto.PlusDto;
import am.dopomoga.aidtools.controller.dto.AirtableDatabaseApiModel;
import am.dopomoga.aidtools.model.document.*;
import am.dopomoga.aidtools.model.entity.AirtableDatabase;
import am.dopomoga.aidtools.model.entity.PermissionLevel;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

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
            it.setUnit(goodDto.unit());
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
                    it.setIdCard(refugeeDto.idCard());
                    it.setCitizenship(refugeeDto.citizenship());
                    it.setPhone(refugeeDto.phone());
                    it.setLocalAddress(refugeeDto.localAddress());
                    it.setArrivalPlace(refugeeDto.arrivalPlace());
                    it.setArrivalDate(refugeeDto.arrivalDate());
                    it.setRefugeeStatus(refugeeDto.refugeeStatus());
                    it.setAirtableOriginalFamilyIds(refugeeDto.familyIds());
                    it.setCardHolder(refugeeDto.cardHolder());
                }
        );
    }

    public SupportDocument map(final SupportDto supportDto) {
        return new SupportDocument().apply(it -> {
            it.setDate(supportDto.date());
            it.setOriginAirtableRefugeeId(firstOrNull(supportDto.refugeeIds()));
            it.setStatus(supportDto.status());
            it.setOriginAirtableIdNumber(supportDto.idNumber());
        });
    }

    public MinusDocument map(final MinusDto minusDto) {
        return new MinusDocument().apply(it -> {
                    it.setOriginAirtableGoodId(firstOrNull(minusDto.goodsId()));
                    it.setOriginAirtableSupportId(firstOrNull(minusDto.supportId()));
                    it.setMinus(minusDto.minus());
                    it.setOriginAirtableIdNumber(minusDto.idNumber());
                }
        );
    }

    public PlusDocument map(final PlusDto plusDto) {
        return new PlusDocument().apply(it -> {
                    it.setOriginAirtableGoodId(firstOrNull(plusDto.goodsId()));
                    it.setPlus(plusDto.plus());
                    it.setCreatedInAirtable(plusDto.created());
                    it.setUpdatedInAirtable(plusDto.lastModified());
                }
        );
    }

    public GoodDto map(final GoodDocument goodDocument) {
        return goodDocument.let(it -> new GoodDto(
                it.getName(),
                it.getType(),
                it.getUnit(),
                it.getUnitWeightKg(),
                lastOrNull(it.getUnitEstimatedPrices()),
                null
        ));
    }

    public RefugeeDto mapWithoutFamily(final RefugeeDocument refugeeDocument) {
        return refugeeDocument.let(it -> new RefugeeDto(
                it.getName(),
                it.getGender(),
                it.getDateOfBirth(),
                it.getIdCard(),
                it.getCitizenship(),
                it.getPhone(),
                it.getLocalAddress(),
                it.getArrivalPlace(),
                it.getArrivalDate(),
                it.getRefugeeStatus(),
                Collections.emptyList(),
                it.getCardHolder()
        ));
    }

    private <E> E firstOrNull(List<E> list) {
        return list == null || list.isEmpty() ? null : list.get(0);
    }

    private <E> E lastOrNull(TreeMap<String, E> map) {
        return map != null && map.lastEntry() != null ? map.lastEntry().getValue() : null;
    }

}
