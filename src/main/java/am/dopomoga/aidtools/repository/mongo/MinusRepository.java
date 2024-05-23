package am.dopomoga.aidtools.repository.mongo;

import am.dopomoga.aidtools.model.document.MinusDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MinusRepository extends MongoRepository<MinusDocument, UUID> {

    MinusDocument findByOriginAirtableId(String originId);

    List<MinusDocument> findBySupportId(UUID supportId);

}
