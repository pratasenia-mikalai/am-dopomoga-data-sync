package am.dopomoga.aidtools.airtable.dto.response;

import am.dopomoga.aidtools.airtable.dto.RefugeesDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AirtableRefugeesTableResponse extends AbstractAirtableTableResponse<RefugeesDto> {

}
