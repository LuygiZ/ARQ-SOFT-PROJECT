package pt.psoft.g1.psoftg1.readermanagement.infrastructure.repositories.impl.mongodb;

import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import pt.psoft.g1.psoftg1.readermanagement.model.mongodb.ReaderDetailsMongoEntity;
import pt.psoft.g1.psoftg1.readermanagement.services.ReaderBookCountDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
@Profile("mongodb")
public interface SpringDataReaderRepositoryMongo extends MongoRepository<ReaderDetailsMongoEntity, String> {

        // Nível 1: Queries Derivadas Simples
        Optional<ReaderDetailsMongoEntity> findByReaderNumber_ReaderNumber(String readerNumber);

        List<ReaderDetailsMongoEntity> findByPhoneNumber_PhoneNumber(String phoneNumber);

        // Nível 3: @Aggregation (para $lookup no User)
        // Assumimos que ReaderDetailsMongoEntity tem { @DBRef private UserMongoEntity
        // reader; }
        @Aggregation(pipeline = {
                        "{$lookup: {from: 'users', localField: 'reader', foreignField: '_id', as: 'readerInfo'}}",
                        "{$unwind: '$readerInfo'}",
                        "{$match: {'readerInfo.username': ?0}}"
        })
        Optional<ReaderDetailsMongoEntity> findByUsername(String username);

        // Nível 3: @Aggregation (para $lookup no User)
        // Assumimos que UserMongoEntity tem um campo 'userId' (Long)
        @Aggregation(pipeline = {
                        "{$lookup: {from: 'users', localField: 'reader', foreignField: '_id', as: 'readerInfo'}}",
                        "{$unwind: '$readerInfo'}",
                        "{$match: {'readerInfo.userId': ?0}}"
        })
        Optional<ReaderDetailsMongoEntity> findByUserId(Long userId);

        // Nível 3: @Aggregation (findTopReaders - Tradução do SQL)
        @Aggregation(pipeline = {
                        "{$lookup: {from: 'lendings', localField: '_id', foreignField: 'readerDetails', as: 'lendings'}}",
                        "{$project: {reader: '$$ROOT', lendingCount: {$size: '$lendings'}}}",
                        "{$sort: {lendingCount: -1}}",
                        "{$replaceRoot: {newRoot: '$reader'}}"
                        // Paginação é tratada pelo Spring Data
        })
        Page<ReaderDetailsMongoEntity> findTopReaders(Pageable pageable);

        // Nível 3: @Aggregation (findTopByGenre - Tradução do SQL)
        @Aggregation(pipeline = {
                        "{$lookup: {from: 'lendings', localField: '_id', foreignField: 'readerDetails', as: 'lendings'}}",
                        "{$unwind: '$lendings'}",
                        // Filtro de data
                        "{$match: {'lendings.startDate': {$gte: ?1, $lte: ?2}}}",
                        // JOIN Book
                        "{$lookup: {from: 'books', localField: 'lendings.book', foreignField: '_id', as: 'bookDetails'}}",
                        "{$unwind: '$bookDetails'}",
                        // JOIN Genre
                        "{$lookup: {from: 'genres', localField: 'bookDetails.genre', foreignField: '_id', as: 'genreDetails'}}",
                        "{$unwind: '$genreDetails'}",
                        // Filtro de Género
                        "{$match: {'genreDetails.genre': ?0}}",
                        // Group By Reader
                        "{$group: {_id: '$_id', reader: {$first: '$$ROOT'}, count: {$sum: 1}}}",
                        "{$sort: {count: -1}}",
                        // Projectar para o DTO
                        "{$project: {reader: '$reader', count: '$count', _id: 0}}"
        })
        Page<ReaderBookCountDTO> findTopByGenre(Pageable pageable, String genre, LocalDate startDate,
                        LocalDate endDate);
}