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

import pt.psoft.g1.psoftg1.authormanagement.model.mongodb.AuthorEntity;
import pt.psoft.g1.psoftg1.genremanagement.model.mongodb.GenreEntity;
import pt.psoft.g1.psoftg1.shared.model.EntityWithPhoto;

import java.util.ArrayList;
import java.util.List;

@Profile("mongodb")
@Document(collection = "books")
public class BookEntity extends EntityWithPhoto {

    @Id
    private String bookId;

    @Version
    @Getter
    private Long version;

    @Field("isbn")
    @Indexed(unique = true)
    private IsbnEntity isbn;

    @Getter
    @Field("title")
    @NotNull
    private TitleEntity title;

    @Getter
    @DBRef // foreign key reference
    @NotNull
    @Field("genre")
    private GenreEntity genre;

    @Getter
    @DBRef // foreign key reference
    @Field("authors")
    private List<AuthorEntity> authors = new ArrayList<>();

    @Field("description")
    private DescriptionEntity description;

    public BookEntity(
            IsbnEntity isbn,
            TitleEntity title,
            DescriptionEntity description,
            GenreEntity genre,
            List<AuthorEntity> authors,
            String photoURI) {

        setTitle(title);
        setIsbn(isbn);
        setDescription(description);
        setGenre(genre);
        setAuthors(authors);
        setPhotoInternal(photoURI);
    }

    protected BookEntity() {
        // got ORM only
    }

    private void setTitle(TitleEntity title) {
        this.title = title;
    }

    private void setIsbn(IsbnEntity isbn) {
        this.isbn = isbn;
    }

    private void setDescription(DescriptionEntity description) {
        this.description = description;
    }

    private void setGenre(GenreEntity genre) {
        this.genre = genre;
    }

    private void setAuthors(List<AuthorEntity> authors) {
        this.authors = authors;
    }

    public String getIsbn() {
        return this.isbn.toString();
    }

    public String getDescription() {
        return this.description.toString();
    }
}
