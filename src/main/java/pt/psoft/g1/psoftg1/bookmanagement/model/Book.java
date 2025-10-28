package pt.psoft.g1.psoftg1.bookmanagement.model;

import org.hibernate.StaleObjectStateException;
import pt.psoft.g1.psoftg1.authormanagement.model.Author;
import pt.psoft.g1.psoftg1.bookmanagement.services.UpdateBookRequest;
import pt.psoft.g1.psoftg1.exceptions.ConflictException;
import pt.psoft.g1.psoftg1.genremanagement.model.Genre;
import pt.psoft.g1.psoftg1.shared.model.EntityWithPhoto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Representa um livro no sistema de gestão de biblioteca.
 */
public class Book extends EntityWithPhoto {

    // Attributes
    private long pk;
    private Long version;
    private Isbn isbn;
    private Title title;
    private Genre genre;
    private List<Author> authors = new ArrayList<>();
    private Description description;

    // Constructors

    /**
     * Construtor para criar um novo livro com todos os dados necessários.
     */
    public Book(String isbn, String title, String description, Genre genre, List<Author> authors, String photoURI) {
        setTitle(title);
        setIsbn(isbn);

        if (description != null)
            setDescription(description);

        if (genre == null)
            throw new IllegalArgumentException("Genre cannot be null");
        setGenre(genre);

        if (authors == null)
            throw new IllegalArgumentException("Author list is null");
        if (authors.isEmpty())
            throw new IllegalArgumentException("Author list is empty");
        setAuthors(authors);

        setPhotoInternal(photoURI);
    }

    /**
     * Construtor protegido para uso exclusivo do ORM.
     */
    protected Book() {
        // for ORM only
    }

    // Setters (privados para encapsulamento)

    private void setTitle(String title) {
        this.title = new Title(title);
    }

    private void setIsbn(String isbn) {
        this.isbn = new Isbn(isbn);
    }

    private void setDescription(String description) {
        this.description = new Description(description);
    }

    private void setGenre(Genre genre) {
        this.genre = genre;
    }

    private void setAuthors(List<Author> authors) {
        this.authors = authors;
    }

    // Getters

    public String getIsbn() {
        return this.isbn.toString();
    }

    public String getTitle() {
        return this.title.toString();
    }

    public String getDescription() {
        return this.description.toString();
    }

    public String getGenre() {
        return this.genre.toString();
    }

    public List<Author> getAuthors() {
        return this.authors;
    }

    public Long getVersion() {
        return this.version;
    }

    // Regras de negócio

    /**
     * Remove a foto do livro com verificação de versão otimista.
     */
    public void removePhoto(long desiredVersion) {
        if (desiredVersion != this.version) {
            throw new ConflictException("Provided version does not match latest version of this object");
        }

        setPhotoInternal((String)null);
    }

    /**
     * Aplica alterações parciais ao livro com verificação de versão otimista.
     */
    public void applyPatch(final Long desiredVersion, UpdateBookRequest request) {
        if (!Objects.equals(this.version, desiredVersion))
            throw new StaleObjectStateException("Object was already modified by another user", this.pk);

        String title = request.getTitle();
        String description = request.getDescription();
        Genre genre = request.getGenreObj();
        List<Author> authors = request.getAuthorObjList();
        String photoURI = request.getPhotoURI();

        if (title != null) {
            setTitle(title);
        }

        if (description != null) {
            setDescription(description);
        }

        if (genre != null) {
            setGenre(genre);
        }

        if (authors != null) {
            setAuthors(authors);
        }

        if (photoURI != null)
            setPhotoInternal(photoURI);
    }

}