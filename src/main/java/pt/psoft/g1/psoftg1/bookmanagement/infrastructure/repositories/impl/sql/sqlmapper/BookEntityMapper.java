package pt.psoft.g1.psoftg1.bookmanagement.infrastructure.repositories.impl.sql.sqlmapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pt.psoft.g1.psoftg1.authormanagement.infrastructure.repositories.impl.sql.sqlmapper.AuthorEntityMapper;
import pt.psoft.g1.psoftg1.bookmanagement.model.Book;
import pt.psoft.g1.psoftg1.bookmanagement.model.Description;
import pt.psoft.g1.psoftg1.bookmanagement.model.Isbn;
import pt.psoft.g1.psoftg1.bookmanagement.model.Title;
import pt.psoft.g1.psoftg1.bookmanagement.model.sql.BookSqlEntity;
import pt.psoft.g1.psoftg1.bookmanagement.model.sql.DescriptionSqlEntity;
import pt.psoft.g1.psoftg1.bookmanagement.model.sql.IsbnSqlEntity;
import pt.psoft.g1.psoftg1.bookmanagement.model.sql.TitleSqlEntity;
import pt.psoft.g1.psoftg1.genremanagement.infrastructure.repositories.impl.sql.sqlmapper.GenreEntityMapper;
import pt.psoft.g1.psoftg1.shared.infrastructure.repositories.impl.sql.sqlmapper.PhotoEntityMapper;

@Mapper(componentModel = "spring", uses = {GenreEntityMapper.class, AuthorEntityMapper.class, PhotoEntityMapper.class})
public interface BookEntityMapper
{
    @Mapping(target="title", source="title")
    @Mapping(target="description", source="description")
    @Mapping(target="isbn", source="isbn")
    @Mapping(target="genre", source="genre")
    @Mapping(target="photoURI", source="photo")
    Book toModel(BookSqlEntity entity);

    @Mapping(target="title", source="titleObj")
    @Mapping(target="description", source="descriptionObj")
    @Mapping(target="isbn", source="isbnObj")
    @Mapping(target="genre", source="genreObj")
    BookSqlEntity toEntity(Book model);

    // Title mappings
    default String map(TitleSqlEntity value) {
        return value == null ? null : value.getTitle();
    }

    default TitleSqlEntity map(Title value) {
        return value == null ? null : new TitleSqlEntity(value.getTitle());
    }

    // Description mappings
    default String map(DescriptionSqlEntity entity) {
        return entity == null ? null : entity.getDescription();
    }

    default DescriptionSqlEntity map(Description value) {
        if (value == null) return null;
        DescriptionSqlEntity entity = new DescriptionSqlEntity();
        entity.setDescription(value.getDescription());
        return entity;
    }

    // ISBN mappings
    default String map(IsbnSqlEntity entity) {
        return entity == null ? null : entity.toString();
    }

    default IsbnSqlEntity map(Isbn value) {
        return value == null ? null : new IsbnSqlEntity(value.toString());
    }
}