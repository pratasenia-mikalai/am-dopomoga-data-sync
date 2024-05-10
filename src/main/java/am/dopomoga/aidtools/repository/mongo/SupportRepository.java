package am.dopomoga.aidtools.repository.mongo;

import am.dopomoga.aidtools.model.document.SupportDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SupportRepository extends MongoRepository<SupportDocument, UUID> {

    SupportDocument findByOriginAirtableId(String originAirtableId);

}
