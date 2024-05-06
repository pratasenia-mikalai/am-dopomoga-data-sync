package am.dopomoga.aidtools.airtable.dto;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum AirtablePermissionLevel {

    NONE("name"),
    READ("read"),
    COMMENT("comment"),
    EDIT("edit"),
    CREATE("create");

    @JsonValue
    final String originValue;
}
