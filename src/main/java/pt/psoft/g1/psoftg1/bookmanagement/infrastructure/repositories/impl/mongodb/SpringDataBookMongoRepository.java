package pt.psoft.g1.psoftg1.bookmanagement.infrastructure.repositories.impl.mongodb;

import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import pt.psoft.g1.psoftg1.bookmanagement.model.mongodb.BookMongoEntity;

import java.util.List;
import java.util.Optional;

/**
 * Interface de repositório Spring Data para a entidade BookMongoEntity.
 * Isto é um detalhe de implementação da camada de infraestrutura (Adaptador).
 *
 * Estende MongoRepository para obter métodos CRUD (save, findById, delete,
 * etc.)
 * e métodos de query derivados do nome.
 */
@Repository
@Profile("mongodb")
public interface SpringDataBookMongoRepository extends MongoRepository<BookMongoEntity, String> {

        /**
         * Encontra um livro pelo seu ISBN.
         * O Spring Data Mongo vai procurar no documento embutido "isbn" pelo campo
         * "isbn".
         */
        Optional<BookMongoEntity> findByIsbn_Isbn(String isbn);

        /**
         * Encontra livros pelo nome do género.
         * O Spring Data Mongo vai procurar na DBRef "genre" pelo campo "genre".
         * (Assume que GenreMongoEntity tem um campo 'genre' indexado)
         */
        List<BookMongoEntity> findByGenre_Genre(String genre);

        /**
         * Encontra livros cujo título corresponda (parcialmente, case-insensitive).
         * O Spring Data Mongo vai procurar no documento embutido "title" pelo campo
         * "title".
         */
        List<BookMongoEntity> findByTitle_TitleLikeIgnoreCase(String title);

        /**
         * Encontra livros associados a um número de autor específico.
         * O Spring Data Mongo vai procurar na lista de DBRefs "authors"
         * por uma correspondência no campo "authorNumber".
         */
        List<BookMongoEntity> findByAuthors_AuthorNumber(Long authorNumber);

        /**
         * Encontra livros pelo nome do autor.
         * O Spring Data Mongo vai procurar na lista de DBRefs "authors",
         * depois no documento embutido "name" e no campo "name".
         */
        List<BookMongoEntity> findByAuthors_Name_NameLikeIgnoreCase(String authorName);

}