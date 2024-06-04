package am.dopomoga.aidtools.airtable.dto;

import am.dopomoga.aidtools.model.FunctionalModel;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;
import java.util.List;

public record PlusDto(
        @JsonProperty("ID")
        Integer idNumber,
        @JsonProperty("Goods")
        List<String> goodsId,
        @JsonProperty("_plus")
        Integer plus,
        @JsonProperty("Created")
        OffsetDateTime created,
        @JsonProperty("Last Modified")
        OffsetDateTime lastModified
) implements FunctionalModel<PlusDto>, AirtableBlankableItem {
    @Override
    public boolean isBlank() {
        return this.goodsId == null || this.goodsId.isEmpty() ||
                this.plus == null || this.plus < 1;
    }
}