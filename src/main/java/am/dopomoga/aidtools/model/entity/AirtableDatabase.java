package am.dopomoga.aidtools.model.entity;

import am.dopomoga.aidtools.model.FunctionalModel;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Data;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Data
@Entity(name = "airtable_database")
public class AirtableDatabase implements FunctionalModel<AirtableDatabase> {

    @Id
    private String id;
    private String name;
    private PermissionLevel permissionLevel;
    private LocalDate actualStartDate;
    private String nextBaseId;
    private boolean fullProcessed;
    private OffsetDateTime createdDate;
    private OffsetDateTime lastUpdatedDate;

    @PrePersist
    public void autoCreatedDate() {
        this.createdDate = OffsetDateTime.now();
    }

    @PreUpdate
    public void autoUpdatedDate() {
        this.lastUpdatedDate = OffsetDateTime.now();
    }

}
