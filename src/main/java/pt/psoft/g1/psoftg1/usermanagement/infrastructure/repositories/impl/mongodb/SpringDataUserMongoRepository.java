package pt.psoft.g1.psoftg1.usermanagement.infrastructure.repositories.impl.mongodb;

import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import pt.psoft.g1.psoftg1.usermanagement.model.mongodb.UserMongoEntity;

import java.util.List;
import java.util.Optional;

/**
 * Interface Spring Data.
 * * - Estende MongoRepository<UserMongoEntity, String> (A classe Base e o
 * seu @Id String)
 * - Contém queries Nível 1 e Nível 2.
 */
@Repository
@Profile("mongodb")
public interface SpringDataUserMongoRepository extends MongoRepository<UserMongoEntity, String> {

    /**
     * Nível 1 (Query Derivada)
     * Encontra pelo ID do Domínio (o 'Long'), não pelo '@Id' (String) do Mongo.
     * Isto é crucial para implementar a "Porta" UserRepository.
     */
    Optional<UserMongoEntity> findByUserId(Long userId);

    /**
     * Nível 1 (Query Derivada)
     */
    Optional<UserMongoEntity> findByUsername(String username);

    /**
     * Nível 1 (Query Derivada)
     * Acede ao campo 'name' (String) dentro do sub-documento 'name'
     * (NameMongoEntity).
     */
    List<UserMongoEntity> findByName_Name(String name);

    /**
     * Nível 2 (Query @Query)
     * Traduz o 'LIKE %...%' do SQL para um regex case-insensitive ('i').
     */
    @Query("{ 'name.name': { $regex: ?0, $options: 'i' } }")
    List<UserMongoEntity> findByNameNameContains(String namePart);
}