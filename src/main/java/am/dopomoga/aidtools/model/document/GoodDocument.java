package am.dopomoga.aidtools.model.document;

import am.dopomoga.aidtools.model.FunctionalModel;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;

import static am.dopomoga.aidtools.model.MongoCollectionsConst.GOOD;

@Slf4j
@Document(GOOD)
@Data
@CompoundIndex(def ="{'nameId': 1, 'type': 1}", unique = true)
public class GoodDocument implements FunctionalModel<GoodDocument> {

    @Id
    private UUID id;
    private String name;
    private String type;
    private String nameId;
    @Indexed
    private Set<String> originAirtableIds;
    private BigDecimal unitWeightKg;
    private TreeMap<String, BigDecimal> unitEstimatedPrices;
    private OffsetDateTime createdDate;
    private OffsetDateTime lastUpdatedDate;

    public void addOriginAirtableId(String airtableId) {
        if (originAirtableIds == null) originAirtableIds = new HashSet<>();
        originAirtableIds.add(airtableId);
    }

    public void addUnitEstimatedPrice(String actualStartDate, BigDecimal estimatedPrice) {
        if (unitEstimatedPrices == null) unitEstimatedPrices = new TreeMap<>();
        try {
            DateTimeFormatter.ISO_DATE.parse(actualStartDate);
            unitEstimatedPrices.put(actualStartDate, estimatedPrice);
        } catch (DateTimeParseException exception) {
            log.error("Wrong date format: {}", actualStartDate);
        }

    }

}
