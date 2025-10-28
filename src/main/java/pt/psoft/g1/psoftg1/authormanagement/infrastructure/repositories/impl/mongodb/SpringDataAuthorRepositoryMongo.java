package pt.psoft.g1.psoftg1.authormanagement.infrastructure.repositories.impl.mongodb;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import pt.psoft.g1.psoftg1.authormanagement.api.AuthorLendingView;
import pt.psoft.g1.psoftg1.authormanagement.model.mongodb.AuthorMongoEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpringDataAuthorRepositoryMongo extends MongoRepository<AuthorMongoEntity, String> {

        @Query("{ 'authorNumber': ?0 }")
        Optional<AuthorMongoEntity> findByAuthorNumber(String authorNumber);

        @Aggregation(pipeline = {
                        "{ $lookup: { from: 'books', localField: '_id', foreignField: 'authors.authorId', as: 'books' } }",
                        "{ $unwind: '$books' }",
                        "{ $lookup: { from: 'lendings', localField: 'books._id', foreignField: 'book.bookId', as: 'lendings' } }",
                        "{ $unwind: '$lendings' }",
                        "{ $group: { _id: '$name', count: { $sum: 1 } } }",
                        "{ $sort: { count: -1 } }",
                        "{ $limit: 10 }",
                        "{ $project: { _id: 0, name: '$_id', lendingCount: '$count' } }"
        })
        Page<AuthorLendingView> findTopAuthorByLendings(Pageable pageable);

        @Aggregation(pipeline = {
                        "{ $lookup: { from: 'books', localField: '_id', foreignField: 'authors.authorId', as: 'books' } }",
                        "{ $unwind: '$books' }",
                        "{ $unwind: '$books.authors' }",
                        "{ $match: { 'books.authors.authorNumber': { $ne: ?0 } } }",
                        "{ $group: { _id: '$books.authors.authorNumber', coAuthor: { $first: '$books.authors' } } }",
                        "{ $replaceRoot: { newRoot: '$coAuthor' } }"
        })
        List<AuthorMongoEntity> findCoAuthorsByAuthorNumber(String authorNumber);

        @Query("{ 'name.name': ?0 }")
        List<AuthorMongoEntity> searchByNameName(String name);

        @Query("{ 'name.name': { $regex: '^?0', $options: 'i' } }")
        List<AuthorMongoEntity> searchByNameNameStartsWith(String name);

        @Query("{}")
        List<AuthorMongoEntity> findAll();
}