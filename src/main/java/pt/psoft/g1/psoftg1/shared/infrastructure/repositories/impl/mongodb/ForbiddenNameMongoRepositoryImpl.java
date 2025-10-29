package pt.psoft.g1.psoftg1.shared.infrastructure.repositories.impl.mongodb;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
// Importe estes dois:
import org.springframework.data.mongodb.core.aggregation.StringOperators;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;
import pt.psoft.g1.psoftg1.shared.infrastructure.repositories.impl.mongodb.mongomapper.ForbiddenNameMongoEntityMapper;
import pt.psoft.g1.psoftg1.shared.model.ForbiddenName;
import pt.psoft.g1.psoftg1.shared.model.mongodb.ForbiddenNameMongoEntity;
import pt.psoft.g1.psoftg1.shared.repositories.ForbiddenNameRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Profile("mongodb")
@Primary
@Repository
@RequiredArgsConstructor
public class ForbiddenNameMongoRepositoryImpl implements ForbiddenNameRepository {

    private final SpringDataForbiddenNameMongoRepository forbiddenNameRepo;
    private final ForbiddenNameMongoEntityMapper forbiddenNameMapper;
    private final MongoTemplate mongoTemplate;

    // ... (métodos findAll, save, findByForbiddenName, deleteForbiddenName ficam
    // iguais) ...
    @Override
    public Iterable<ForbiddenName> findAll() {
        return StreamSupport.stream(forbiddenNameRepo.findAll().spliterator(), false)
                .map(forbiddenNameMapper::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public ForbiddenName save(ForbiddenName forbiddenName) {
        ForbiddenNameMongoEntity entity = forbiddenNameMapper.toMongoEntity(forbiddenName);
        ForbiddenNameMongoEntity saved = forbiddenNameRepo.save(entity);
        return forbiddenNameMapper.toModel(saved);
    }

    @Override
    public Optional<ForbiddenName> findByForbiddenName(String forbiddenName) {
        return forbiddenNameRepo.findByForbiddenName(forbiddenName)
                .map(forbiddenNameMapper::toModel);
    }

    @Override
    public int deleteForbiddenName(String forbiddenName) {
        return forbiddenNameRepo.deleteByForbiddenName(forbiddenName);
    }

    /**
     * CORRIGIDO: Esta é a tradução Mongo de 'WHERE :pat LIKE %forbiddenName%'
     */
    @Override
    public List<ForbiddenName> findByForbiddenNameIsContained(String pat) {

        // A lógica Mongo é:
        // $match: { $expr: { $gte: [ { $indexOfCP: [ pat, "$forbiddenName" ] }, 0 ] } }
        //
        // Tradução: "Encontrar documentos onde o índice de '$forbiddenName' (o campo)
        // DENTRO de 'pat' (a nossa variável) seja maior ou igual a 0 (ou seja, foi
        // encontrado)"

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(
                        Criteria.where("$expr").gte(
                                // String a pesquisar (pat) .indexOf (campo a procurar "$forbiddenName")
                                StringOperators.IndexOfCP.valueOf(pat).indexOf("$forbiddenName"))));

        AggregationResults<ForbiddenNameMongoEntity> results = mongoTemplate.aggregate(
                aggregation, "forbiddenNames", ForbiddenNameMongoEntity.class);

        return results.getMappedResults().stream()
                .map(forbiddenNameMapper::toModel)
                .collect(Collectors.toList());
    }
}