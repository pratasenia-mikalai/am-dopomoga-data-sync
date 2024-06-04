package am.dopomoga.aidtools.model.document;

import am.dopomoga.aidtools.model.MongoCollectionsConst;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.OffsetDateTime;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@Document(MongoCollectionsConst.PLUS)
public class PlusDocument extends AbstractDocument<PlusDocument> {

    @Indexed(unique = true)
    private String originAirtableId;
    private String originAirtableGoodId;
    private Integer plus;
    private UUID goodId;
    private OffsetDateTime createdInAirtable;
    private OffsetDateTime updatedInAirtable;

}
