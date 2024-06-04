package am.dopomoga.aidtools.airtable.dto;

public sealed interface AirtableBlankableItem permits GoodDto, RefugeeDto, SupportDto, MinusDto, PlusDto {

    boolean isBlank();

}
