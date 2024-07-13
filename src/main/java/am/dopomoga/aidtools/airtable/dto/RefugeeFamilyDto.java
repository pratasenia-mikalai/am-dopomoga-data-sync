package am.dopomoga.aidtools.airtable.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record RefugeeFamilyDto(
        @JsonProperty("Family")
        List<String> familyIds
) {
}
