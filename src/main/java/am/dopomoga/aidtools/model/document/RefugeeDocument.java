package am.dopomoga.aidtools.model.document;

import am.dopomoga.aidtools.model.MongoCollectionsConst;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@Document(MongoCollectionsConst.REFUGEE)
@CompoundIndex(def ="{'name': 1, 'dateOfBirth': 1}", unique = true)
public class RefugeeDocument extends AbstractDocument<RefugeeDocument> {

    private String name;
    private String gender;
    @Field(targetType = FieldType.STRING)
    private LocalDate dateOfBirth;
    private String idCard;
    private String citizenship;
    private String phone;
    private String localAddress;
    private String arrivalPlace;
    @Field(targetType = FieldType.STRING)
    private LocalDate arrivalDate;
    private Boolean refugeeStatus;
    @Indexed
    private Set<String> originAirtableIds;
    private Set<UUID> familyIds;
    private List<String> airtableOriginalFamilyIds;
    private Boolean cardHolder;
    private String newAirtableId;

    public void addOriginAirtableId(String airtableId) {
        if (originAirtableIds == null) originAirtableIds = new HashSet<>();
        originAirtableIds.add(airtableId);
    }

    @Override
    public void setOriginAirtableId(String originAirtableId) {
        addOriginAirtableId(originAirtableId);
    }
}
