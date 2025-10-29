package pt.psoft.g1.psoftg1.readermanagement.infrastructure.repositories.impl.mongodb;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.bson.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import pt.psoft.g1.psoftg1.readermanagement.infrastructure.repositories.impl.mongodb.mongomapper.ReaderDetailsMongoEntityMapper;
import pt.psoft.g1.psoftg1.readermanagement.model.ReaderDetails;
import pt.psoft.g1.psoftg1.readermanagement.model.mongodb.ReaderDetailsMongoEntity;
import pt.psoft.g1.psoftg1.readermanagement.repositories.ReaderRepository;
import pt.psoft.g1.psoftg1.readermanagement.services.ReaderBookCountDTO;
import pt.psoft.g1.psoftg1.readermanagement.services.SearchReadersQuery;

import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Profile("mongodb")
@Qualifier("mongoDbRepo")
@Repository // Usar @Repository em vez de @Component
@Primary
@RequiredArgsConstructor
public class ReaderDetailsMongoRepositoryImpl implements ReaderRepository {

    private final SpringDataReaderRepositoryMongo readerRepo;
    private final ReaderDetailsMongoEntityMapper readerMapper;
    private final MongoTemplate mongoTemplate; // Precisamos disto para queries dinâmicas

    // --- Métodos Delegados (Estáticos) ---

    @Override
    public Optional<ReaderDetails> findByReaderNumber(String readerNumber) {
        return readerRepo.findByReaderNumber_ReaderNumber(readerNumber)
                .map(readerMapper::toModel);
    }

    @Override
    public List<ReaderDetails> findByPhoneNumber(String phoneNumber) {
        return readerRepo.findByPhoneNumber_PhoneNumber(phoneNumber).stream()
                .map(readerMapper::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ReaderDetails> findByUsername(String username) {
        // Delega para a query @Aggregation
        return readerRepo.findByUsername(username)
                .map(readerMapper::toModel);
    }

    @Override
    public Optional<ReaderDetails> findByUserId(Long userId) {
        // Delega para a query @Aggregation
        return readerRepo.findByUserId(userId)
                .map(readerMapper::toModel);
    }

    @Override
    public ReaderDetails save(ReaderDetails readerDetails) {
        ReaderDetailsMongoEntity entity = readerMapper.toMongoEntity(readerDetails);
        ReaderDetailsMongoEntity saved = readerRepo.save(entity);
        return readerMapper.toModel(saved);
    }

    @Override
    public Iterable<ReaderDetails> findAll() {
        return StreamSupport.stream(readerRepo.findAll().spliterator(), false)
                .map(readerMapper::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public Page<ReaderDetails> findTopReaders(Pageable pageable) {
        // Delega para a query @Aggregation
        return readerRepo.findTopReaders(pageable)
                .map(readerMapper::toModel);
    }

    @Override
    public Page<ReaderBookCountDTO> findTopByGenre(Pageable pageable, String genre, LocalDate startDate,
            LocalDate endDate) {
        // Delega para a query @Aggregation
        return readerRepo.findTopByGenre(pageable, genre, startDate, endDate);
    }

    @Override
    public void delete(ReaderDetails readerDetails) {
        readerRepo.delete(readerMapper.toMongoEntity(readerDetails));
    }

    // --- Métodos Dinâmicos (MongoTemplate) ---

    @Override
    public int getCountFromCurrentYear() {
        // Lógica dinâmica (calcula 'current year')
        LocalDate startOfYear = Year.now().atDay(1);

        // Esta query precisa de um $lookup no 'users' para ver o 'createdAt'
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.lookup("users", "reader", "_id", "readerInfo"),
                Aggregation.unwind("$readerInfo"),
                Aggregation.match(Criteria.where("readerInfo.createdAt").gte(startOfYear)));

        AggregationResults<Document> results = mongoTemplate.aggregate(aggregation, "readerdetails", Document.class);
        return results.getMappedResults().size();
    }

    @Override
    public List<ReaderDetails> searchReaderDetails(pt.psoft.g1.psoftg1.shared.services.Page page,
            SearchReadersQuery query) {
        // Lógica dinâmica (filtros opcionais + $lookups)
        List<AggregationOperation> pipeline = new ArrayList<>();

        // $lookup para 'users' (para filtrar por nome e email/username)
        pipeline.add(Aggregation.lookup("users", "reader", "_id", "readerInfo"));
        pipeline.add(Aggregation.unwind("$readerInfo"));

        // $match (construção dinâmica de filtros)
        List<Criteria> criteria = new ArrayList<>();
        if (StringUtils.hasText(query.getName())) {
            criteria.add(Criteria.where("readerInfo.name.name").regex(query.getName(), "i"));
        }
        if (StringUtils.hasText(query.getEmail())) {
            criteria.add(Criteria.where("readerInfo.username").is(query.getEmail()));
        }
        if (StringUtils.hasText(query.getPhoneNumber())) {
            criteria.add(Criteria.where("phoneNumber.phoneNumber").is(query.getPhoneNumber()));
        }

        if (!criteria.isEmpty()) {
            pipeline.add(Aggregation.match(new Criteria().orOperator(criteria.toArray(new Criteria[0]))));
        }

        // Paginação
        pipeline.add(Aggregation.skip((long) (page.getNumber() - 1) * page.getLimit()));
        pipeline.add(Aggregation.limit(page.getLimit()));

        // Projectar de volta para o ReaderDetails
        pipeline.add(Aggregation.project()
                .and("_id").as("readerDetailsId")
                .and("reader").as("reader")
                .and("readerNumber").as("readerNumber")
                .and("birthDate").as("birthDate")
                .and("phoneNumber").as("phoneNumber")
        // ... (etc. para todos os campos de ReaderDetailsMongoEntity)
        );

        Aggregation aggregation = Aggregation.newAggregation(pipeline);

        AggregationResults<ReaderDetailsMongoEntity> results = mongoTemplate.aggregate(
                aggregation, "readerdetails", ReaderDetailsMongoEntity.class);

        return results.getMappedResults().stream()
                .map(readerMapper::toModel)
                .collect(Collectors.toList());
    }
}