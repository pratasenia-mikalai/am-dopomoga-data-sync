package am.dopomoga.aidtools.controller.dto;

import am.dopomoga.aidtools.model.FunctionalModel;
import am.dopomoga.aidtools.model.entity.PermissionLevel;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public record AirtableDatabaseApiModel(
        String id,
        String name,
        PermissionLevel permissionLevel,
        LocalDate actualStartDate,
        String nextDatabaseId,
        boolean fullProcessed,
        OffsetDateTime createdDate,
        OffsetDateTime lastUpdatedDate
) implements FunctionalModel<AirtableDatabaseApiModel> {
}
