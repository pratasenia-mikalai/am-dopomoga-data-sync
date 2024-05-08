package am.dopomoga.aidtools.model.document;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public abstract class AbstractDocument {

    @Id
    private UUID id;
    private OffsetDateTime createdDate;
    private OffsetDateTime lastUpdatedDate;

    public void fillFieldsOnCreate() {
        this.id = UUID.randomUUID();
        this.createdDate = OffsetDateTime.now();
        this.lastUpdatedDate = OffsetDateTime.now();
    }

    public void copyFieldsAndFillOnUpdate(AbstractDocument abstractDocument) {
        this.id = abstractDocument.getId();
        this.createdDate = abstractDocument.getCreatedDate();
        this.lastUpdatedDate = OffsetDateTime.now();
    }

}
