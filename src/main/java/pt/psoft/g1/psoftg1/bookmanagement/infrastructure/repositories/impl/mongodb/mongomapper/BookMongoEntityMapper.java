package pt.psoft.g1.psoftg1.bookmanagement.infrastructure.repositories.impl.mongodb.mongomapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pt.psoft.g1.psoftg1.authormanagement.infrastructure.repositories.impl.mongodb.mongomapper.AuthorMongoEntityMapper; // Presumido
import pt.psoft.g1.psoftg1.bookmanagement.model.Book;
import pt.psoft.g1.psoftg1.bookmanagement.model.mongodb.BookMongoEntity;
import pt.psoft.g1.psoftg1.bookmanagement.model.mongodb.DescriptionMongoEntity;
import pt.psoft.g1.psoftg1.bookmanagement.model.mongodb.IsbnMongoEntity;
import pt.psoft.g1.psoftg1.bookmanagement.model.mongodb.TitleMongoEntity;
import pt.psoft.g1.psoftg1.genremanagement.infrastructure.repositories.impl.mongodb.mongomapper.GenreMongoEntityMapper;
import pt.psoft.g1.psoftg1.shared.infrastructure.repositories.impl.mongodb.mongomapper.PhotoMongoEntityMapper;
import pt.psoft.g1.psoftg1.shared.model.Photo;
import java.nio.file.Path;

@Mapper(componentModel = "spring", uses = {
        GenreMongoEntityMapper.class,
        AuthorMongoEntityMapper.class,
        PhotoMongoEntityMapper.class
})
public interface BookMongoEntityMapper {

    @Mapping(source = "photo", target = "photo")
    Book toModel(BookMongoEntity entity);

    @Mapping(target = "bookId", ignore = true)
    @Mapping(target = "genre", ignore = true)
    @Mapping(target = "authors", ignore = true)
    @Mapping(target = "photoURI", source = "photo")
    @Mapping(source = "isbn", target = "isbn")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "description", target = "description")
    BookMongoEntity toMongoEntity(Book model);

    default IsbnMongoEntity stringToIsbnMongoEntity(String isbn) {
        return (isbn == null) ? null : new IsbnMongoEntity(isbn);
    }

    default String isbnMongoEntityToString(IsbnMongoEntity entity) {
        return (entity == null) ? null : entity.getIsbn();
    }

    default TitleMongoEntity stringToTitleMongoEntity(String title) {
        return (title == null) ? null : new TitleMongoEntity(title);
    }

    default String titleMongoEntityToString(TitleMongoEntity entity) {
        return (entity == null) ? null : entity.getTitle();
    }

    default DescriptionMongoEntity stringToDescriptionMongoEntity(String description) {
        return (description == null) ? null : new DescriptionMongoEntity(description);
    }

    default String descriptionMongoEntityToString(DescriptionMongoEntity entity) {
        return (entity == null) ? null : entity.getDescription();
    }

    default String photoToPhotoURI(Photo photo) {
        return (photo == null) ? null : photo.getPhotoFile();
    }

    default Photo photoURIToPhoto(String uri) {
        return (uri == null) ? null : new Photo(Path.of(uri));
    }
}