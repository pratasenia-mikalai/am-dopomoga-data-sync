package am.dopomoga.aidtools.airtable.dto;

import am.dopomoga.aidtools.model.FunctionalModel;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.List;

public record RefugeeDto(
        @JsonProperty("Name")
        String name,
        @JsonProperty("Пол")
        String gender,
        @JsonProperty("DOB")
        LocalDate dateOfBirth,
        @JsonProperty("Arrival date")
        LocalDate arrivalDate,
        @JsonProperty("Refuge status")
        Boolean refugeeStatus,
        @JsonProperty("Family")
        List<String> familyIds
) implements FunctionalModel<RefugeeDto> {
}
