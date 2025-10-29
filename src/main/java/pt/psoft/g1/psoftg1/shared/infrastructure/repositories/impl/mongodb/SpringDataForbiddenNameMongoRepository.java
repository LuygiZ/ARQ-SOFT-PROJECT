package pt.psoft.g1.psoftg1.shared.infrastructure.repositories.impl.mongodb;

import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import pt.psoft.g1.psoftg1.shared.model.mongodb.ForbiddenNameMongoEntity;

import java.util.Optional;

@Repository
@Profile("mongodb")
public interface SpringDataForbiddenNameMongoRepository extends MongoRepository<ForbiddenNameMongoEntity, String> {

        // Nível 1 (Query Derivada)
        Optional<ForbiddenNameMongoEntity> findByForbiddenName(String forbiddenName);

        // Nível 1 (Query Derivada)
        int deleteByForbiddenName(String forbiddenName);
}