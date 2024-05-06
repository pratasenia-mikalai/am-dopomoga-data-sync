package am.dopomoga.aidtools.service;

import am.dopomoga.aidtools.airtable.restclient.AirtableBasesClient;
import am.dopomoga.aidtools.controller.dto.AirtableDatabaseApiModel;
import am.dopomoga.aidtools.model.entity.AirtableDatabase;
import am.dopomoga.aidtools.model.mapper.ModelMapper;
import am.dopomoga.aidtools.repository.postgres.AirtableDatabaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@RequiredArgsConstructor
@Service
public class AirtableDatabaseService {

    private final AirtableBasesClient airtableBasesClient;
    private final AirtableDatabaseRepository airtableBaseRepository;
    private final ModelMapper mapper;

    public AirtableDatabaseApiModel getFirstNonProcessedBase() {
        return nullableDatabaseRecordOf(airtableBaseRepository::getFirstNonProcessedBase);
    }

    @Transactional
    public void setDatabaseFullProcessed(String databaseId) {
        airtableBaseRepository.setDatabaseFullProcessedById(databaseId);
    }

    public AirtableDatabaseApiModel getDatabase(String databaseId) {
        return nullableDatabaseRecordOf(() -> airtableBaseRepository.getReferenceById(databaseId));
    }

    public List<AirtableDatabaseApiModel> getDatabaseListFromLocal() {
        return airtableBaseRepository.findAll().stream().map(mapper::map).toList();
    }

    public List<AirtableDatabaseApiModel> getDatabaseListFromAirtable() {
        return airtableBasesClient.getBasesList()
                .bases().stream()
                .map(mapper::map)
                .toList();
    }

    @Transactional
    public List<AirtableDatabaseApiModel> saveDatabasesToLocal(List<AirtableDatabaseApiModel> bases) {
        List<AirtableDatabase> baseEntities = bases.stream()
                .map(mapper::map)
                .toList();
        return airtableBaseRepository.saveAll(baseEntities).stream()
                .map(mapper::map)
                .toList();
    }

    private AirtableDatabaseApiModel nullableDatabaseRecordOf(Supplier<AirtableDatabase> supplier) {
        return Optional.of(supplier.get())
                .map(mapper::map)
                .orElse(null);
    }

}
