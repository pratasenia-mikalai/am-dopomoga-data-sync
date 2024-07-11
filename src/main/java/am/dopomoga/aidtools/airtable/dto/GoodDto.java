package am.dopomoga.aidtools.airtable.dto;

import am.dopomoga.aidtools.model.FunctionalModel;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record GoodDto(
        @JsonProperty("Name")
        String name,
        @JsonProperty("Type")
        String type,
        @JsonProperty("Unit")
        String unit,
        @JsonProperty("Weight 1 unit, kg")
        BigDecimal unitWeightKg,
        @JsonProperty("Est. Price")
        BigDecimal unitEstimatedPrice,
        @JsonProperty("ID_name")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String nameId
) implements FunctionalModel<GoodDto>, AirtableBlankableItem {

    @Override
    public boolean isBlank() {
        return this.name == null || this.name.isBlank();
    }
}
