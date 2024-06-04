package am.dopomoga.aidtools.repository.mongo;

import am.dopomoga.aidtools.model.document.PlusDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PlusRepository extends MongoRepository<PlusDocument, UUID> {

    PlusDocument findByOriginAirtableId(String originId);

}
