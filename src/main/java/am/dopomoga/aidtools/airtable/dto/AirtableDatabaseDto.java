package am.dopomoga.aidtools.airtable.dto;

import am.dopomoga.aidtools.model.FunctionalModel;

public record AirtableDatabaseDto(
        String id,
        String name,
        AirtablePermissionLevel permissionLevel
) implements FunctionalModel<AirtableDatabaseDto> {
}
