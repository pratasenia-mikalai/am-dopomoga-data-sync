package am.dopomoga.aidtools.airtable.dto;

import am.dopomoga.aidtools.model.FunctionalModel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Data
public class TableDataDto<T> implements FunctionalModel<TableDataDto<T>> {

    private String id;
    private OffsetDateTime createdTime;
    private T fields;
    @JsonIgnore
    private LocalDate actualStartDateFromDatabase;

}
