package pt.psoft.g1.psoftg1.usermanagement.infrastructure.repositories.impl.mongodb;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import pt.psoft.g1.psoftg1.shared.services.Page;
import pt.psoft.g1.psoftg1.usermanagement.infrastructure.repositories.impl.mongodb.mongomapper.UserMongoEntityMapper;
import pt.psoft.g1.psoftg1.usermanagement.model.User;
import pt.psoft.g1.psoftg1.usermanagement.model.mongodb.UserMongoEntity;
import pt.psoft.g1.psoftg1.usermanagement.repositories.UserRepository;
import pt.psoft.g1.psoftg1.usermanagement.services.SearchUsersQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Profile("mongodb")
@Primary
@Repository
@RequiredArgsConstructor
public class UserMongoRepositoryImpl implements UserRepository {

    private final SpringDataUserMongoRepository userRepo;
    private final UserMongoEntityMapper userMapper;
    private final MongoTemplate mongoTemplate;

    @Override
    public <S extends User> List<S> saveAll(Iterable<S> entities) {
        List<UserMongoEntity> mongoEntities = StreamSupport.stream(entities.spliterator(), false)
                .map(userMapper::toMongoEntity) // O Mapper trata da herança
                .collect(Collectors.toList());

        List<UserMongoEntity> savedEntities = userRepo.saveAll(mongoEntities);

        // O Mapper trata da herança no retorno
        return (List<S>) savedEntities.stream()
                .map(userMapper::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public <S extends User> S save(S entity) {
        // Graças ao @SubclassMapping, não precisamos de 'if/instanceof'
        UserMongoEntity mongoEntity = userMapper.toMongoEntity(entity);
        UserMongoEntity savedEntity = userRepo.save(mongoEntity);
        // O Mapper trata da herança no retorno
        return (S) userMapper.toModel(savedEntity);
    }

    @Override
    public Optional<User> findById(Long objectId) {
        // Usamos a nossa query Nível 1 pelo 'userId' (Long)
        return userRepo.findByUserId(objectId)
                .map(userMapper::toModel);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepo.findByUsername(username)
                .map(userMapper::toModel);
    }

    @Override
    public List<User> findByNameName(String name) {
        return userRepo.findByName_Name(name).stream()
                .map(userMapper::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> findByNameNameContains(String name) {
        return userRepo.findByNameNameContains(name).stream()
                .map(userMapper::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(User user) {
        userRepo.delete(userMapper.toMongoEntity(user));
    }

    // --- Query Dinâmica (MongoTemplate) ---

    @Override
    public List<User> searchUsers(Page page, SearchUsersQuery query) {
        // Esta é a tradução da lógica do 'CriteriaBuilder' (SQL)

        final List<Criteria> where = new ArrayList<>();
        if (StringUtils.hasText(query.getUsername())) {
            where.add(Criteria.where("username").is(query.getUsername()));
        }
        if (StringUtils.hasText(query.getFullName())) {
            // Traduz o 'LIKE' do SQL (regex case-insensitive)
            where.add(Criteria.where("name.name").regex(query.getFullName(), "i"));
        }

        final Query mongoQuery = new Query();

        // O SQL usava 'OR'
        if (!where.isEmpty()) {
            mongoQuery.addCriteria(new Criteria().orOperator(where.toArray(new Criteria[0])));
        }

        // O SQL ordenava por 'createdAt'
        mongoQuery.with(Sort.by(Sort.Direction.DESC, "createdAt"));

        // Paginação
        mongoQuery.skip((long) (page.getNumber() - 1) * page.getLimit());
        mongoQuery.limit(page.getLimit());

        // O "users" no final é o nome da coleção
        List<UserMongoEntity> entities = mongoTemplate.find(mongoQuery, UserMongoEntity.class, "users");

        // O Mapper trata da herança (converte UserMongoEntity, ReaderMongoEntity, etc.)
        return entities.stream()
                .map(userMapper::toModel)
                .collect(Collectors.toList());
    }
}