package pt.psoft.g1.psoftg1.shared.infrastructure.repositories.impl.mongodb;

import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import pt.psoft.g1.psoftg1.shared.model.mongodb.PhotoMongoEntity;

@Repository
@Profile("mongodb")
public interface SpringDataPhotoMongoRepository extends MongoRepository<PhotoMongoEntity, String> {

        // Nível 1 (Query Derivada)
        void deleteByPhotoFile(String photoFile);
}