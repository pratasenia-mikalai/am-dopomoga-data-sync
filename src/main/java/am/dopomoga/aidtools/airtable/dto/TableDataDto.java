package am.dopomoga.aidtools.airtable.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Data
public class TableDataDto<T extends AirtableBlankableItem> {

    private String id;
    private OffsetDateTime createdTime;
    private T fields;
    @JsonIgnore
    private LocalDate actualStartDateFromDatabase;

}
