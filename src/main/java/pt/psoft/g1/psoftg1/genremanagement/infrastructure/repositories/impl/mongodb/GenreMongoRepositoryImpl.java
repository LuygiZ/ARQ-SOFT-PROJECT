package pt.psoft.g1.psoftg1.genremanagement.infrastructure.repositories.impl.mongodb;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.DateOperators;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;
import pt.psoft.g1.psoftg1.bookmanagement.services.GenreBookCountDTO;
import pt.psoft.g1.psoftg1.genremanagement.infrastructure.repositories.impl.mongodb.mongomapper.GenreMongoEntityMapper;
import pt.psoft.g1.psoftg1.genremanagement.model.Genre;
import pt.psoft.g1.psoftg1.genremanagement.model.mongodb.GenreMongoEntity;
import pt.psoft.g1.psoftg1.genremanagement.repositories.GenreRepository;
import pt.psoft.g1.psoftg1.genremanagement.services.GenreLendingsDTO;
import pt.psoft.g1.psoftg1.genremanagement.services.GenreLendingsPerMonthDTO;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Profile("mongodb")
@Primary
@Repository
@RequiredArgsConstructor
public class GenreMongoRepositoryImpl implements GenreRepository {

    private final SpringDataGenreMongoRepository genreRepo;
    private final GenreMongoEntityMapper genreMapper;
    private final MongoTemplate mongoTemplate;

    // ... (métodos save, delete, findAll, findByString, findTop5GenreByBookCount
    // permanecem iguais, delegando para 'genreRepo') ...

    @Override
    public Iterable<Genre> findAll() {
        return genreRepo.findAll().stream()
                .map(genreMapper::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Genre> findByString(String genreName) {
        return genreRepo.findByGenre(genreName)
                .map(genreMapper::toModel);
    }

    @Override
    public Genre save(Genre genre) {
        GenreMongoEntity entity = genreMapper.toMongoEntity(genre);
        GenreMongoEntity savedEntity = genreRepo.save(entity);
        return genreMapper.toModel(savedEntity);
    }

    @Override
    public void delete(Genre genre) {
        genreRepo.findByGenre(genre.getGenre()).ifPresent(genreRepo::delete);
    }

    @Override
    public Page<GenreBookCountDTO> findTop5GenreByBookCount(Pageable pageable) {
        return genreRepo.findTop5GenreByBookCount(pageable);
    }

    // --- QUERIES DINÂMICAS (MANTIDAS NO 'IMPL') ---

    @Override
    public List<GenreLendingsPerMonthDTO> getLendingsPerMonthLastYearByGenre() {
        // MANTIDO: Esta lógica calcula LocalDate.now(), por isso
        // tem de ficar no 'Impl' e usar MongoTemplate.
        LocalDate now = LocalDate.now();
        LocalDate twelveMonthsAgo = now.minusMonths(12);

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("startDate").gte(twelveMonthsAgo).lte(now)),
                Aggregation.lookup("books", "book", "_id", "bookDetails"),
                Aggregation.unwind("$bookDetails", true),
                Aggregation.lookup("genres", "bookDetails.genre", "_id", "genreDetails"),
                Aggregation.unwind("$genreDetails", true),
                Aggregation.project()
                        .and("$genreDetails.genre").as("genre")
                        .and(DateOperators.dateOf("startDate").toString("%Y")).as("year")
                        .and(DateOperators.dateOf("startDate").toString("%m")).as("month"),
                Aggregation.group("genre", "year", "month").count().as("count"),
                Aggregation.sort(Sort.Direction.ASC, "year", "month", "_id.genre"));

        AggregationResults<Document> results = mongoTemplate.aggregate(aggregation, "lendings", Document.class);
        return processAggregationResults(results.getMappedResults());
    }

    @Override
    public List<GenreLendingsDTO> getAverageLendingsInMonth(LocalDate month,
            pt.psoft.g1.psoftg1.shared.services.Page page) {
        // MANTIDO: Esta lógica calcula 'firstOfMonth', 'lastOfMonth' e 'days',
        // por isso tem de ficar no 'Impl'.
        int days = month.lengthOfMonth();
        LocalDate firstOfMonth = month.withDayOfMonth(1);
        LocalDate lastOfMonth = month.withDayOfMonth(days);

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("startDate").gte(firstOfMonth).lte(lastOfMonth)),
                Aggregation.lookup("books", "book", "_id", "bookDetails"),
                Aggregation.unwind("$bookDetails", true),
                Aggregation.lookup("genres", "bookDetails.genre", "_id", "genreDetails"),
                Aggregation.unwind("$genreDetails", true),
                Aggregation.group("$genreDetails.genre").count().as("totalLendings"),
                Aggregation.project()
                        .and("_id").as("genre")
                        .and("totalLendings").divide(days).as("averageLendings"),
                Aggregation.skip((long) (page.getNumber() - 1) * page.getLimit()),
                Aggregation.limit(page.getLimit()));

        AggregationResults<GenreLendingsDTO> results = mongoTemplate.aggregate(aggregation, "lendings",
                GenreLendingsDTO.class);
        return results.getMappedResults();
    }

    // --- QUERY ESTÁTICA (MOVIDA) ---

    @Override
    public List<GenreLendingsPerMonthDTO> getLendingsAverageDurationPerMonth(LocalDate startDate, LocalDate endDate) {
        // MUDANÇA: Agora delegamos para a interface Spring Data!
        List<Document> results = genreRepo.getLendingsAverageDurationPerMonth(startDate, endDate);

        // Usamos o helper refatorado para processar os resultados
        return processAggregationResults(results);
    }

    /**
     * MUDANÇA: O helper agora aceita List<Document> para ser mais flexível
     * e poder ser usado tanto pelo MongoTemplate como pelo Repositório.
     */
    @NotNull
    private List<GenreLendingsPerMonthDTO> processAggregationResults(List<Document> mappedResults) {
        Map<Integer, Map<Integer, List<GenreLendingsDTO>>> groupedResults = new HashMap<>();

        for (Document result : mappedResults) {
            Document id = (Document) result.get("_id");
            String genre = id.getString("genre");
            int yearValue = Integer.parseInt(id.getString("year"));
            int monthValue = Integer.parseInt(id.getString("month"));
            double count = result.getDouble("count");

            GenreLendingsDTO genreLendingsDTO = new GenreLendingsDTO(genre, count);

            groupedResults
                    .computeIfAbsent(yearValue, k -> new HashMap<>())
                    .computeIfAbsent(monthValue, k -> new ArrayList<>())
                    .add(genreLendingsDTO);
        }

        List<GenreLendingsPerMonthDTO> lendingsPerMonth = new ArrayList<>();
        for (Map.Entry<Integer, Map<Integer, List<GenreLendingsDTO>>> yearEntry : groupedResults.entrySet()) {
            int yearValue = yearEntry.getKey();
            for (Map.Entry<Integer, List<GenreLendingsDTO>> monthEntry : yearEntry.getValue().entrySet()) {
                int monthValue = monthEntry.getKey();
                List<GenreLendingsDTO> values = monthEntry.getValue();
                lendingsPerMonth.add(new GenreLendingsPerMonthDTO(yearValue, monthValue, values));
            }
        }
        return lendingsPerMonth;
    }
}