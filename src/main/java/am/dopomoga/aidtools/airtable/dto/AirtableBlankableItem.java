package am.dopomoga.aidtools.airtable.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

public sealed interface AirtableBlankableItem permits GoodDto, RefugeeDto, SupportDto, MinusDto, PlusDto {

    @JsonIgnore
    boolean isBlank();

}
