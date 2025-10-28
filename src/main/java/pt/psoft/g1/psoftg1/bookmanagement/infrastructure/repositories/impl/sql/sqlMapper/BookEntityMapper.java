package pt.psoft.g1.psoftg1.bookmanagement.infrastructure.repositories.impl.sql.sqlMapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pt.psoft.g1.psoftg1.authormanagement.infrastructure.repositories.impl.sql.sqlMapper.AuthorEntityMapper;
import pt.psoft.g1.psoftg1.bookmanagement.model.Book;
import pt.psoft.g1.psoftg1.bookmanagement.model.sql.BookSqlEntity;
import pt.psoft.g1.psoftg1.bookmanagement.model.sql.DescriptionSqlEntity;
import pt.psoft.g1.psoftg1.bookmanagement.model.sql.IsbnSqlEntity;
import pt.psoft.g1.psoftg1.bookmanagement.model.sql.TitleSqlEntity;
import pt.psoft.g1.psoftg1.genremanagement.model.sql.GenreSqlEntity;
import pt.psoft.g1.psoftg1.genremanagement.infrastructure.repositories.impl.sql.sqlMapper.GenreEntityMapper;
import pt.psoft.g1.psoftg1.shared.infrastructure.repositories.impl.sql.sqlMapper.PhotoEntityMapper;

@Mapper(componentModel = "spring", uses = {GenreEntityMapper.class, AuthorEntityMapper.class, PhotoEntityMapper.class})
public interface BookEntityMapper
{
    @Mapping(target="photoURI", source="photo")
    Book toModel(BookSqlEntity entity);
    BookSqlEntity toEntity(Book model);

    // Mapping methods for Title (Entity -> String)
    default String map(TitleSqlEntity value) {
        return value == null ? null : value.getTitle();
    }

    // Mapping methods for Title (String -> Entity)
    default TitleSqlEntity mapToTitle(String title) {
        if (title == null) return null;
        TitleSqlEntity entity = new TitleSqlEntity();
        entity.setTitle(title);
        return entity;
    }

    // Mapping methods for Description (Entity -> String)
    default String map(DescriptionSqlEntity entity) {
        return entity == null ? null : entity.getDescription();
    }

    // Mapping methods for Description (String -> Entity)
    default DescriptionSqlEntity mapToDescription(String description) {
        if (description == null) return null;
        DescriptionSqlEntity entity = new DescriptionSqlEntity();
        entity.setDescription(description);
        return entity;
    }

    // Mapping methods for Genre (Entity -> String)
    default String map(GenreSqlEntity entity) {
        return entity == null ? null : entity.getGenre();
    }

    // Mapping methods for Genre (String -> Entity)
    default GenreSqlEntity mapToGenre(String genre) {
        if (genre == null) return null;
        GenreSqlEntity entity = new GenreSqlEntity();
        entity.setGenre(genre);
        return entity;
    }

    // Mapping methods for ISBN (Entity -> String)
    default String map(IsbnSqlEntity entity) {
        return entity == null ? null : entity.toString();
    }

    // Mapping methods for ISBN (String -> Entity)
    default IsbnSqlEntity mapToIsbn(String isbn) {
        return isbn == null ? null : new IsbnSqlEntity(isbn);
    }
}
