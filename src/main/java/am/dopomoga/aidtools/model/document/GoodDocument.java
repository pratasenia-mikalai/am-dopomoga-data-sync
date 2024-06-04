package am.dopomoga.aidtools.model.document;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;

import static am.dopomoga.aidtools.model.MongoCollectionsConst.GOOD;

@EqualsAndHashCode(callSuper = true)
@Slf4j
@Document(GOOD)
@Data
public class GoodDocument extends AbstractDocument<GoodDocument> {

    private String name;
    private String type;
    @Indexed
    private String nameId;
    @Indexed
    private Set<String> originAirtableIds;
    private BigDecimal unitWeightKg;
    private TreeMap<String, BigDecimal> unitEstimatedPrices;

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

    @Override
    public void setOriginAirtableId(String originAirtableId) {
        addOriginAirtableId(originAirtableId);
    }
}
