package am.dopomoga.aidtools.airtable.dto.response;

import am.dopomoga.aidtools.airtable.dto.AirtableDatabaseDto;

import java.util.List;

public record AirtableBasesResponse(
        List<AirtableDatabaseDto> bases
) {
}
