package am.dopomoga.aidtools.model.document;

import am.dopomoga.aidtools.model.MongoCollectionsConst;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@Document(MongoCollectionsConst.MINUS)
public class MinusDocument extends AbstractDocument<MinusDocument> {

    @Indexed(unique = true)
    private String originAirtableId;
    private String originAirtableGoodId;
    private String originAirtableSupportId;
    private Integer minus;
    private UUID goodId;
    @Indexed
    private UUID supportId;
    private Integer originAirtableIdNumber;

}
