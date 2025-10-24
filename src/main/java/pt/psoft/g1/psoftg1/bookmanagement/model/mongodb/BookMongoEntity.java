package pt.psoft.g1.psoftg1.bookmanagement.model.mongodb;

import jakarta.validation.constraints.NotNull;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Getter;
import lombok.Setter;
import pt.psoft.g1.psoftg1.authormanagement.model.mongodb.AuthorMongoEntity;
import pt.psoft.g1.psoftg1.genremanagement.model.mongodb.GenreMongoEntity;
import pt.psoft.g1.psoftg1.shared.model.EntityWithPhoto;

import java.util.ArrayList;
import java.util.List;

@Profile("mongodb")
@Primary
@Document(collection = "books")
@Getter
@Setter
public class BookMongoEntity extends EntityWithPhoto {

    @Id
    private String bookId;

    @Version

    private Long version;

    @Field("isbn")
    @Indexed(unique = true)
    private IsbnMongoEntity isbn;

    @Field("title")
    @NotNull
    private TitleMongoEntity title;

    @DBRef // foreign key reference
    @NotNull
    @Field("genre")
    private GenreMongoEntity genre;

    @DBRef // foreign key reference
    @Field("authors")
    private List<AuthorMongoEntity> authors = new ArrayList<>();

    @Field("description")
    private DescriptionMongoEntity description;

    public BookMongoEntity(
            IsbnMongoEntity isbn,
            TitleMongoEntity title,
            DescriptionMongoEntity description,
            GenreMongoEntity genre,
            List<AuthorMongoEntity> authors,
            String photoURI) {

        this.title = title;
        this.isbn = isbn;
        this.description = description;
        this.genre = genre;
        this.authors = authors;
        setPhotoInternal(photoURI);
    }

    protected BookMongoEntity() {
        // got ORM only
    }

}
