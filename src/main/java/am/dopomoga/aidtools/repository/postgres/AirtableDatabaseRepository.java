package am.dopomoga.aidtools.repository.postgres;

import am.dopomoga.aidtools.model.entity.AirtableDatabase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AirtableDatabaseRepository extends JpaRepository<AirtableDatabase, String> {

    @Modifying
    @Query("update airtable_database ab set ab.fullProcessed = true where ab.id = :id")
    void setDatabaseFullProcessedById(@Param("id") String id);

    @Query("select ab from airtable_database ab where ab.fullProcessed = false and ab.id not in " +
            "(select b.nextBaseId from airtable_database b where b.fullProcessed = false and b.nextBaseId is not null)")
    AirtableDatabase getFirstNonProcessedBase();

}
