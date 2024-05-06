package am.dopomoga.aidtools.model.entity;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum PermissionLevel {

    NONE("none"),
    READ("read"),
    COMMENT("comment"),
    EDIT("edit"),
    CREATE("create");

    @JsonValue
    final String originValue;
}
