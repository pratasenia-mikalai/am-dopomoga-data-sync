package am.dopomoga.aidtools.airtable.dto;

import am.dopomoga.aidtools.model.FunctionalModel;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.List;

public record SupportDto(
        @JsonProperty("ID")
        Integer idNumber,
        @JsonProperty("Status")
        String status,
        @JsonProperty("Date")
        LocalDate date,
        @JsonProperty("Who")
        List<String> refugeeIds
) implements FunctionalModel<SupportDto> {
}
