package pt.psoft.g1.psoftg1.lendingmanagement.infrastructure.repositories.impl.mongodb;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import pt.psoft.g1.psoftg1.lendingmanagement.infrastructure.repositories.impl.mongodb.mongomapper.LendingMongoEntityMapper;
import pt.psoft.g1.psoftg1.lendingmanagement.model.Lending;
import pt.psoft.g1.psoftg1.lendingmanagement.model.mongodb.LendingMongoEntity;
import pt.psoft.g1.psoftg1.lendingmanagement.repositories.LendingRepository;
import pt.psoft.g1.psoftg1.shared.services.Page;

import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Profile("mongodb")
@Primary
@Repository
@RequiredArgsConstructor
public class LendingMongoRepositoryImpl implements LendingRepository {

    private final SpringDataLendingMongoRepository lendingRepo;
    private final LendingMongoEntityMapper lendingMapper;
    private final MongoTemplate mongoTemplate;

    // --- Métodos Delegados (Estáticos) ---

    @Override
    public Optional<Lending> findByLendingNumber(String lendingNumber) {
        return lendingRepo.findByLendingNumber_LendingNumber(lendingNumber)
                .map(lendingMapper::toModel);
    }

    @Override
    public List<Lending> listByReaderNumberAndIsbn(String readerNumber, String isbn) {
        return lendingRepo.findByReaderDetails_ReaderNumber_ReaderNumberAndBook_Isbn_Isbn(readerNumber, isbn)
                .stream()
                .map(lendingMapper::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<Lending> listOutstandingByReaderNumber(String readerNumber) {
        return lendingRepo.findByReaderDetails_ReaderNumber_ReaderNumberAndReturnedDateIsNull(readerNumber)
                .stream()
                .map(lendingMapper::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public Double getAverageDuration() {
        return lendingRepo.getAverageDuration();
    }

    @Override
    public Double getAvgLendingDurationByIsbn(String isbn) {
        return lendingRepo.getAvgLendingDurationByIsbn(isbn);
    }

    @Override
    public Lending save(Lending lending) {
        LendingMongoEntity entity = lendingMapper.toMongoEntity(lending);
        LendingMongoEntity saved = lendingRepo.save(entity);
        return lendingMapper.toModel(saved);
    }

    @Override
    public void delete(Lending lending) {
        lendingRepo.delete(lendingMapper.toMongoEntity(lending));
    }

    // --- Métodos Dinâmicos (MongoTemplate) ---

    @Override
    public int getCountFromCurrentYear() {
        // Lógica dinâmica (calcula 'current year')
        LocalDate startOfYear = Year.now().atDay(1);
        Query query = new Query(Criteria.where("startDate").gte(startOfYear));
        long count = mongoTemplate.count(query, LendingMongoEntity.class);
        return (int) count;
    }

    @Override
    public List<Lending> getOverdue(Page page) {
        // Lógica dinâmica (calcula 'LocalDate.now()')
        LocalDate now = LocalDate.now();
        Query query = new Query(Criteria.where("returnedDate").isNull()
                .and("limitDate").lt(now));

        query.limit(page.getLimit());
        query.skip((long) (page.getNumber() - 1) * page.getLimit());

        return mongoTemplate.find(query, LendingMongoEntity.class)
                .stream()
                .map(lendingMapper::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<Lending> searchLendings(Page page, String readerNumber, String isbn, Boolean returned,
            LocalDate startDate, LocalDate endDate) {
        // Lógica dinâmica (filtros opcionais + $lookups)
        List<AggregationOperation> pipeline = new ArrayList<>();

        // $lookup para 'books' (para filtrar por ISBN)
        pipeline.add(Aggregation.lookup("books", "book", "_id", "bookDetails"));
        pipeline.add(Aggregation.unwind("$bookDetails", true));

        // $lookup para 'readerdetails' (para filtrar por ReaderNumber)
        pipeline.add(Aggregation.lookup("readerdetails", "readerDetails", "_id", "readerDetailsInfo"));
        pipeline.add(Aggregation.unwind("$readerDetailsInfo", true));

        // $match (construção dinâmica de filtros)
        List<Criteria> criteria = new ArrayList<>();
        if (StringUtils.hasText(readerNumber)) {
            criteria.add(Criteria.where("readerDetailsInfo.readerNumber.readerNumber").is(readerNumber));
        }
        if (StringUtils.hasText(isbn)) {
            criteria.add(Criteria.where("bookDetails.isbn.isbn").is(isbn));
        }
        if (returned != null) {
            criteria.add(Criteria.where("returnedDate").is(returned ? (Object) true : null));
        }
        if (startDate != null) {
            criteria.add(Criteria.where("startDate").gte(startDate));
        }
        if (endDate != null) {
            criteria.add(Criteria.where("startDate").lte(endDate));
        }

        if (!criteria.isEmpty()) {
            pipeline.add(Aggregation.match(new Criteria().andOperator(criteria.toArray(new Criteria[0]))));
        }

        // Paginação
        pipeline.add(Aggregation.skip((long) (page.getNumber() - 1) * page.getLimit()));
        pipeline.add(Aggregation.limit(page.getLimit()));

        // $project para voltar ao formato original (opcional, mas limpo)
        pipeline.add(Aggregation.project()
                .and("lendingId").as("lendingId")
                .and("lendingNumber").as("lendingNumber")
                .and("book").as("book")
                .and("readerDetails").as("readerDetails")
                .and("startDate").as("startDate")
                .and("limitDate").as("limitDate")
                .and("returnedDate").as("returnedDate")
                .and("version").as("version")
                .and("commentary").as("commentary")
                .and("fineValuePerDayInCents").as("fineValuePerDayInCents"));

        Aggregation aggregation = Aggregation.newAggregation(pipeline);

        AggregationResults<LendingMongoEntity> results = mongoTemplate.aggregate(
                aggregation, "lendings", LendingMongoEntity.class);

        return results.getMappedResults().stream()
                .map(lendingMapper::toModel)
                .collect(Collectors.toList());
    }
}