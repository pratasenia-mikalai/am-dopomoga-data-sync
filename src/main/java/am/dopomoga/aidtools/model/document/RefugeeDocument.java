package am.dopomoga.aidtools.model.document;

import am.dopomoga.aidtools.model.MongoCollectionsConst;
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
    @Field(targetType = FieldType.STRING)
    private LocalDate arrivalDate;
    private Boolean refugeeStatus;
    @Indexed
    private Set<String> originAirtableIds;
    private List<UUID> familyIds;
    private List<String> airtableOriginalFamilyIds;

    public void addOriginAirtableId(String airtableId) {
        if (originAirtableIds == null) originAirtableIds = new HashSet<>();
        originAirtableIds.add(airtableId);
    }

}
