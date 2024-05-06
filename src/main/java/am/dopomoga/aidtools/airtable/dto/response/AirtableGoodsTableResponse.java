package am.dopomoga.aidtools.airtable.dto.response;

import am.dopomoga.aidtools.airtable.dto.GoodDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AirtableGoodsTableResponse extends AbstractAirtableTableResponse<GoodDto> {

}
