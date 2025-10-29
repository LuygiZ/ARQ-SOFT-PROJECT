package pt.psoft.g1.psoftg1.genremanagement.infrastructure.repositories.impl.mongodb;

import org.bson.Document;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import pt.psoft.g1.psoftg1.bookmanagement.services.GenreBookCountDTO;
import pt.psoft.g1.psoftg1.genremanagement.model.mongodb.GenreMongoEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Interface de repositório Spring Data (Mongo) para a entidade Genre.
 * Usa o padrão híbrido:
 * - Nível 1 (queries derivadas) para métodos simples.
 * - Nível 3 (@Aggregation) para queries estáticas complexas.
 */
@Repository
@Profile("mongodb")
public interface SpringDataGenreMongoRepository extends MongoRepository<GenreMongoEntity, String> {

        Optional<GenreMongoEntity> findByGenre(String genreName);

        @Aggregation(pipeline = {
                        "{$lookup: {from: 'books', localField: '_id', foreignField: 'genre', as: 'books'}}",
                        "{$group: {_id: '$genre', count: {$sum: {$size: '$books'}} }}",
                        "{$sort: {count: -1}}",
                        "{$project: {genre: '$_id', count: 1, _id: 0}}"
        })
        Page<GenreBookCountDTO> findTop5GenreByBookCount(Pageable pageable);

        @Aggregation(pipeline = {

                        "{$match: {startDate: {$gte: ?0, $lte: ?1}, returnedDate: {$ne: null}}}",
                        "{$lookup: {from: 'books', localField: 'book', foreignField: '_id', as: 'bookDetails'}}",
                        "{$unwind: {path: '$bookDetails', preserveNullAndEmptyArrays: true}}",
                        "{$lookup: {from: 'genres', localField: 'bookDetails.genre', foreignField: '_id', as: 'genreDetails'}}",
                        "{$unwind: {path: '$genreDetails', preserveNullAndEmptyArrays: true}}",
                        "{$project: {genre: '$genreDetails.genre', year: {$toString: {format: '%Y', date: '$startDate'}}, month: {$toString: {format: '%m', date: '$startDate'}}, durationInDays: {$divide: [{$subtract: ['$returnedDate', '$startDate']}, 86400000]}}}",
                        "{$group: {_id: {genre: '$genre', year: '$year', month: '$month'}, count: {$avg: '$durationInDays'}}}",
                        "{$sort: {'_id.year': 1, '_id.month': 1, '_id.genre': 1}}",
                        "{$project: {_id: '$_id', count: '$count'}}"
        })
        List<Document> getLendingsAverageDurationPerMonth(LocalDate startDate, LocalDate endDate);
}