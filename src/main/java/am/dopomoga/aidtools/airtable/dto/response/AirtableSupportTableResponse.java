package am.dopomoga.aidtools.airtable.dto.response;

import am.dopomoga.aidtools.airtable.dto.SupportDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AirtableSupportTableResponse extends AbstractAirtableTableResponse<SupportDto> {

}
