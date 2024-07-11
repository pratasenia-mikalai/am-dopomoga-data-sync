package am.dopomoga.aidtools.airtable.dto;

import am.dopomoga.aidtools.model.FunctionalModel;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
public class TableDataSaveDto<T> implements FunctionalModel<TableDataSaveDto<T>> {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String id;
    private T fields;

}
