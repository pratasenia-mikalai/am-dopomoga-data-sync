package am.dopomoga.aidtools.airtable.dto;

import am.dopomoga.aidtools.model.FunctionalModel;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record MinusDto(
        @JsonProperty("ID")
        Integer idNumber,
        @JsonProperty("Goods")
        List<String> goodsId,
        @JsonProperty("_minus")
        Integer minus,
        @JsonProperty("Support")
        List<String> supportId
) implements FunctionalModel<MinusDto>, AirtableBlankableItem {
        @Override
        public boolean isBlank() {
                return this.goodsId == null || this.goodsId.isEmpty() ||
                        this.supportId == null || this.supportId.isEmpty();
        }
}
