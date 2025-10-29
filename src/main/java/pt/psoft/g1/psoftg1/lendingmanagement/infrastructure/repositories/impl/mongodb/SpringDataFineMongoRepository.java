package pt.psoft.g1.psoftg1.lendingmanagement.infrastructure.repositories.impl.mongodb;

import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import pt.psoft.g1.psoftg1.lendingmanagement.model.mongodb.FineMongoEntity;

import java.util.Optional;

@Repository
@Profile("mongodb")
public interface SpringDataFineMongoRepository extends MongoRepository<FineMongoEntity, String> {

    /**
     * Nível 1 (Query Derivada). O Spring Data vai procurar:
     * 1. No campo 'lending' (o @DBRef para LendingMongoEntity)
     * 2. No campo 'lendingNumber' (o objeto embutido)
     * 3. No campo 'lendingNumber' (o String dentro do objeto)
     */
    Optional<FineMongoEntity> findByLending_LendingNumber_LendingNumber(String lendingNumber);

}