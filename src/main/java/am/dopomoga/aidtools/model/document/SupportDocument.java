package am.dopomoga.aidtools.model.document;

import am.dopomoga.aidtools.model.MongoCollectionsConst;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@Document(MongoCollectionsConst.SUPPORT)
public class SupportDocument extends AbstractDocument<SupportDocument> {

    @Indexed(unique = true)
    private String originAirtableId;
    private String status;
    @Indexed
    private LocalDate date;
    private String originAirtableRefugeeId;
    @Indexed
    private UUID refugeeId;
    private Integer originAirtableIdNumber;

}
