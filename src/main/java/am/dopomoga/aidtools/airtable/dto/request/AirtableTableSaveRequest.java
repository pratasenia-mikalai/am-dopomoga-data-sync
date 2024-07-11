package am.dopomoga.aidtools.airtable.dto.request;

import am.dopomoga.aidtools.airtable.dto.TableDataSaveDto;
import lombok.Data;

import java.util.List;

@Data
public class AirtableTableSaveRequest<T> {

    private List<TableDataSaveDto<T>> records;

}
