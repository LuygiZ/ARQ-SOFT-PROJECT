package pt.psoft.g1.psoftg1.lendingmanagement.infrastructure.repositories.impl.mongodb;

import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pt.psoft.g1.psoftg1.lendingmanagement.model.mongodb.LendingMongoEntity;

import java.util.List;
import java.util.Optional;

@Repository
@Profile("mongodb")
public interface SpringDataLendingMongoRepository extends MongoRepository<LendingMongoEntity, String> {

        // Nível 1 (Query Derivada)
        Optional<LendingMongoEntity> findByLendingNumber_LendingNumber(String lendingNumber);

        // Nível 1 (Query Derivada)
        List<LendingMongoEntity> findByReaderDetails_ReaderNumber_ReaderNumberAndBook_Isbn_Isbn(
                        String readerNumber, String isbn);

        // Nível 1 (Query Derivada)
        List<LendingMongoEntity> findByReaderDetails_ReaderNumber_ReaderNumberAndReturnedDateIsNull(
                        @Param("readerNumber") String readerNumber);

        // Nível 3 (Agregação Estática) - Traduz 'AVG(DATEDIFF(day, l.start_date,
        // l.returned_date))'
        @Aggregation(pipeline = {
                        "{$match: {returnedDate: {$ne: null}}}",
                        "{$project: {durationInDays: {$divide: [{$subtract: ['$returnedDate', '$startDate']}, 86400000]}}}",
                        "{$group: {_id: null, avgDuration: {$avg: '$durationInDays'}}}"
        })
        Double getAverageDuration();

        // Nível 3 (Agregação Estática) - O mesmo, mas com filtro de ISBN
        @Aggregation(pipeline = {
                        // 1. $lookup: Precisa de "JOIN" com 'books' para filtrar por ISBN
                        "{$lookup: {from: 'books', localField: 'book', foreignField: '_id', as: 'bookDetails'}}",
                        "{$unwind: '$bookDetails'}",

                        // 2. $match: Filtra pelo ISBN e data de devolução
                        "{$match: {'bookDetails.isbn.isbn': ?0, returnedDate: {$ne: null}}}",

                        // 3. $project: Calcula a duração
                        "{$project: {durationInDays: {$divide: [{$subtract: ['$returnedDate', '$startDate']}, 86400000]}}}",

                        // 4. $group: Calcula a média
                        "{$group: {_id: null, avgDuration: {$avg: '$durationInDays'}}}"
        })
        Double getAvgLendingDurationByIsbn(String isbn);

}