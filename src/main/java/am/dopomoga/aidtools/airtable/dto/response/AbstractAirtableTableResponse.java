package am.dopomoga.aidtools.airtable.dto.response;

import am.dopomoga.aidtools.airtable.dto.TableDataDto;
import lombok.Data;

import java.util.List;

@Data
public abstract class AbstractAirtableTableResponse<T> {

    private List<TableDataDto<T>> records;
    private String offset;

}
