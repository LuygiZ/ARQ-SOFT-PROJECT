package pt.psoft.g1.psoftg1.bookmanagement.infrastructure.repositories.impl.sql;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import pt.psoft.g1.psoftg1.authormanagement.infrastructure.repositories.impl.sql.AuthorRepositoryImpl;
import pt.psoft.g1.psoftg1.authormanagement.model.Author;
import pt.psoft.g1.psoftg1.authormanagement.model.sql.AuthorSqlEntity;
import pt.psoft.g1.psoftg1.bookmanagement.infrastructure.repositories.impl.sql.sqlmapper.BookEntityMapper;
import pt.psoft.g1.psoftg1.bookmanagement.model.Book;
import pt.psoft.g1.psoftg1.bookmanagement.model.sql.BookSqlEntity;
import pt.psoft.g1.psoftg1.bookmanagement.repositories.BookRepository;
import pt.psoft.g1.psoftg1.bookmanagement.services.BookCountDTO;
import pt.psoft.g1.psoftg1.bookmanagement.services.SearchBooksQuery;
import pt.psoft.g1.psoftg1.genremanagement.infrastructure.repositories.impl.sql.GenreRepositoryImpl;
import pt.psoft.g1.psoftg1.genremanagement.model.Genre;
import pt.psoft.g1.psoftg1.genremanagement.model.sql.GenreSqlEntity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Profile("sql-redis")
@Primary
@Repository
@RequiredArgsConstructor
public class BookRepositoryImpl implements BookRepository
{
    private final SpringDataBookRepository bookRepo;
    private final BookEntityMapper bookEntityMapper;
    private final GenreRepositoryImpl genreRepo;
    private final AuthorRepositoryImpl authorRepo;
    private final EntityManager em;

    @Override
    public List<Book> findByGenre(@Param("genre") String genre)
    {
        List<Book> books = new ArrayList<>();
        for (BookSqlEntity b: bookRepo.findByGenre(genre))
        {
            books.add(bookEntityMapper.toModel(b));
        }

        return books;
    }

    @Override
    public List<Book> findByTitle(@Param("title") String title)
    {
        List<Book> books = new ArrayList<>();
        for (BookSqlEntity b: bookRepo.findByTitle(title))
        {
            books.add(bookEntityMapper.toModel(b));
        }

        return books;
    }

    @Override
    public List<Book> findByAuthorName(@Param("authorName") String authorName)
    {
        List<Book> books = new ArrayList<>();
        for (BookSqlEntity b: bookRepo.findByAuthorName(authorName))
        {
            books.add(bookEntityMapper.toModel(b));
        }

        return books;
    }

    @Override
    public Optional<Book> findByIsbn(@Param("isbn") String isbn)
    {
        Optional<BookSqlEntity> entityOpt = bookRepo.findByIsbn(isbn);
        if(entityOpt.isPresent())
        {
            return Optional.of(bookEntityMapper.toModel(entityOpt.get()));
        }
        else
        {
            return Optional.empty();
        }
    }

    @Override
    public Page<BookCountDTO> findTop5BooksLent(@Param("oneYearAgo") LocalDate oneYearAgo, Pageable pageable)
    {
        //TODO: Corrigir este
        return bookRepo.findTop5BooksLent(oneYearAgo, pageable);
    }

    @Override
    public List<Book> findBooksByAuthorNumber(Long authorNumber)
    {
        List<Book> books = new ArrayList<>();
        for (BookSqlEntity b: bookRepo.findBooksByAuthorNumber(authorNumber))
        {
            books.add(bookEntityMapper.toModel(b));
        }

        return books;
    }

    @Override
    public List<Book> searchBooks(pt.psoft.g1.psoftg1.shared.services.Page page, SearchBooksQuery query)
    {
        String title = query.getTitle();
        String genre = query.getGenre();
        String authorName = query.getAuthorName();

        final CriteriaBuilder cb = em.getCriteriaBuilder();
        final CriteriaQuery<BookSqlEntity> cq = cb.createQuery(BookSqlEntity.class);
        final Root<BookSqlEntity> root = cq.from(BookSqlEntity.class);
        final Join<BookSqlEntity, Genre> genreJoin = root.join("genre");
        final Join<BookSqlEntity, Author> authorJoin = root.join("authors");
        cq.select(root);

        final List<Predicate> where = new ArrayList<>();

        if (StringUtils.hasText(title))
            where.add(cb.like(root.get("title").get("title"), title + "%"));

        if (StringUtils.hasText(genre))
            where.add(cb.like(genreJoin.get("genre"), genre + "%"));

        if (StringUtils.hasText(authorName))
            where.add(cb.like(authorJoin.get("name").get("name"), authorName + "%"));

        cq.where(where.toArray(new Predicate[0]));
        cq.orderBy(cb.asc(root.get("title"))); // Order by title, alphabetically

        final TypedQuery<BookSqlEntity> q = em.createQuery(cq);
        q.setFirstResult((page.getNumber() - 1) * page.getLimit());
        q.setMaxResults(page.getLimit());

        List <Book> books = new ArrayList<>();

        for (BookSqlEntity bookEntity : q.getResultList()) {
            books.add(bookEntityMapper.toModel(bookEntity));
        }

        return books;
    }

    @Override
    @Transactional
    public Book save(Book book) {
        if (book == null) {
            throw new IllegalArgumentException("Book cannot be null");
        }

        BookSqlEntity entity = bookEntityMapper.toEntity(book);

        // === Handle Genre ===
        Genre genreModel = genreRepo.findByString(book.getGenre().getGenre())
                .orElseThrow(() -> new IllegalStateException(
                        "Genre not found: " + book.getGenre().getGenre()));

        GenreSqlEntity genreEntity = em.getReference(GenreSqlEntity.class, genreModel.getPk());
        entity.setGenre(genreEntity);

        // === Handle Authors ===
        // ✅ CORREÇÃO: Buscar authors POR NOME (já que authorNumber pode não estar setado no Book)
        List<AuthorSqlEntity> authorEntities = book.getAuthors().stream()
                .map(author -> {
                    String authorName = author.getName().getName();

                    // Buscar author pelo nome no repositório
                    List<Author> foundAuthors = authorRepo.searchByNameName(authorName);

                    if (foundAuthors.isEmpty()) {
                        throw new IllegalStateException(
                                "Author not found: " + authorName +
                                        ". Make sure authors are created before books.");
                    }

                    // Pegar o primeiro (assumindo que nome é único ou próximo disso)
                    Author matchedAuthor = foundAuthors.get(0);

                    // ✅ CRÍTICO: Verificar se authorNumber foi gerado
                    if (matchedAuthor.getAuthorNumber() == null) {
                        throw new IllegalStateException(
                                "Author '" + authorName + "' exists but has no authorNumber. " +
                                        "Database may not have committed yet.");
                    }

                    return em.getReference(AuthorSqlEntity.class, matchedAuthor.getAuthorNumber());
                })
                .toList();

        entity.setAuthors(authorEntities);

        // === Save ===
        BookSqlEntity saved = bookRepo.save(entity);
        System.out.println("Saved entity ISBN: " + saved.getIsbn());

        return bookEntityMapper.toModel(saved);
    }

    @Override
    public void delete(Book book)
    {
        // TODO: implement delete logic
    }

    // Adiciona este método público
    public Optional<BookSqlEntity> findSqlEntityByIsbn(String isbn) {
        return bookRepo.findByIsbn(isbn);
    }
}
