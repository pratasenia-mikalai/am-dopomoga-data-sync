package am.dopomoga.aidtools.repository.mongo;

import am.dopomoga.aidtools.model.document.RefugeeDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.UUID;

@Repository
public interface RefugeeRepository extends MongoRepository<RefugeeDocument, UUID> {

    RefugeeDocument findByNameAndDateOfBirth(String name, LocalDate dateOfBirth);

}
