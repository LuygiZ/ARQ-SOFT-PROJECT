package pt.psoft.g1.psoftg1.bookmanagement.model.sql;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import pt.psoft.g1.psoftg1.authormanagement.model.sql.AuthorEntity;
import pt.psoft.g1.psoftg1.genremanagement.model.sql.GenreEntity;
import pt.psoft.g1.psoftg1.shared.model.EntityWithPhoto;

import java.util.ArrayList;
import java.util.List;

@Profile("SQL") //Specifies which environment (or Spring profile) a bean should be loaded in.
@Primary //Marks a bean as the default when multiple candidates of the same type exist.
@Entity
@Table(name = "Book", uniqueConstraints = {
        @UniqueConstraint(name = "uc_book_isbn", columnNames = { "ISBN" })
})
public class BookEntity extends EntityWithPhoto {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long pk;

    @Version
    @Getter
    private Long version;

    @Embedded
    private IsbnEntity isbn;

    @Getter
    @Embedded
    @NotNull
    private TitleEntity title;

    @Getter
    @ManyToOne
    @NotNull
    private GenreEntity genre;

    @Getter
    @ManyToMany
    private List<AuthorEntity> authors = new ArrayList<>();

    @Embedded
    private DescriptionEntity description;

    public BookEntity(IsbnEntity isbn, TitleEntity title, DescriptionEntity description, GenreEntity genre, List<AuthorEntity> authors, String photoURI)
    {
        setTitle(title);
        setIsbn(isbn);
        setDescription(description);
        setAuthors(authors);
        setGenre(genre);
        setPhotoInternal(photoURI);
    }

    protected BookEntity() {}

    // Setters
    private void setTitle(TitleEntity title) { this.title = title; }
    private void setIsbn(IsbnEntity isbn) { this.isbn = isbn; }
    private void setDescription(DescriptionEntity description) { this.description = description; }
    private void setGenre(GenreEntity genre) { this.genre = genre; }
    private void setAuthors(List<AuthorEntity> authors) { this.authors = authors; }

    // Getters
    public String getDescription(){ return this.description.toString(); }
    public String getIsbn(){ return this.isbn.toString(); }
}
