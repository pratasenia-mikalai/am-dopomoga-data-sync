package am.dopomoga.aidtools.repository.mongo;

import am.dopomoga.aidtools.model.document.GoodDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GoodRepository extends MongoRepository<GoodDocument, UUID> {


    List<GoodDocument> findByNameId(String nameId);

    GoodDocument findByNameIdAndType(String nameId, String type);

}
