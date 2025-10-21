package pt.psoft.g1.psoftg1.bookmanagement.model.mongodb;

import jakarta.validation.constraints.NotNull;

import org.springframework.context.annotation.Profile;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Getter;

import pt.psoft.g1.psoftg1.authormanagement.model.Author;
import pt.psoft.g1.psoftg1.genremanagement.model.Genre;
import pt.psoft.g1.psoftg1.shared.model.EntityWithPhoto;

import java.util.ArrayList;
import java.util.List;

@Profile("mongodb")
@Document(collection = "books")
public class BookMongo extends EntityWithPhoto {

    @Id
    private String id;

    @Version
    @Getter
    private Long version;

    @Field("isbn")
    @Indexed(unique = true)
    private IsbnMongo isbn;

    @Getter
    @Field("title")
    @NotNull
    private TitleMongo title;

    @Getter
    @DBRef // foreign key reference
    @NotNull
    @Field("genre")
    private Genre genre;

    @Getter
    @DBRef // foreign key reference
    @Field("authors")
    private List<Author> authors = new ArrayList<>();

    @Field("description")
    private DescriptionMongo description;

    public BookMongo(String isbn, String title, String description, Genre genre, List<Author> authors,
            String photoURI) {
        setTitle(title);
        setIsbn(isbn);
        setDescription(description);
        setGenre(genre);
        setAuthors(authors);
        setPhotoInternal(photoURI);
    }

    protected BookMongo() {
        // got ORM only
    }

    private void setTitle(String title) {
        this.title = new TitleMongo(title);
    }

    private void setIsbn(String isbn) {
        this.isbn = new IsbnMongo(isbn);
    }

    private void setDescription(String description) {
        this.description = new DescriptionMongo(description);
    }

    private void setGenre(Genre genre) {
        this.genre = genre;
    }

    private void setAuthors(List<Author> authors) {
        this.authors = authors;
    }

    public String getIsbn() {
        return this.isbn.toString();
    }

    public String getDescription() {
        return this.description.toString();
    }
}
