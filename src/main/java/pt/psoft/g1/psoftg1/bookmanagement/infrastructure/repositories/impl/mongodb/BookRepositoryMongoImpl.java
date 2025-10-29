package pt.psoft.g1.psoftg1.bookmanagement.infrastructure.repositories.impl.mongodb;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import pt.psoft.g1.psoftg1.bookmanagement.infrastructure.repositories.impl.mongodb.mongomapper.BookMongoEntityMapper;
import pt.psoft.g1.psoftg1.bookmanagement.model.Book;
import pt.psoft.g1.psoftg1.bookmanagement.model.mongodb.BookMongoEntity;
import pt.psoft.g1.psoftg1.bookmanagement.repositories.BookRepository;
import pt.psoft.g1.psoftg1.bookmanagement.services.BookCountDTO;
import pt.psoft.g1.psoftg1.bookmanagement.services.SearchBooksQuery;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementação (Adaptador) da porta do domínio BookRepository.
 * Esta implementação usa MongoDB como tecnologia de persistência.
 *
 * É marcada como @Primary e @Profile("mongodb") para ser a implementação
 * principal quando o perfil "mongodb" estiver ativo.
 */
@Profile("mongodb")
@Primary
@Repository
@RequiredArgsConstructor
public class BookRepositoryMongoImpl implements BookRepository {

    private final SpringDataBookMongoRepository springDataBookRepo;
    private final BookMongoEntityMapper bookMapper;
    private final MongoTemplate mongoTemplate; // Para queries complexas

    @Override
    public Book save(Book book) {
        // 1. Traduzir Domínio -> Persistência
        BookMongoEntity entity = bookMapper.toMongoEntity(book);

        // 2. Salvar na base de dados
        BookMongoEntity savedEntity = springDataBookRepo.save(entity);

        // 3. Traduzir Persistência -> Domínio
        return bookMapper.toModel(savedEntity);
    }

    @Override
    public void delete(Book book) {
        // É mais seguro apagar pelo ID do que pelo objeto
        BookMongoEntity entity = bookMapper.toMongoEntity(book);
        springDataBookRepo.deleteById(entity.getBookId());
    }

    @Override
    public Optional<Book> findByIsbn(String isbn) {
        return springDataBookRepo.findByIsbn_Isbn(isbn)
                .map(bookMapper::toModel); // Traduz o resultado para o Domínio
    }

    @Override
    public List<Book> findByGenre(String genre) {
        return springDataBookRepo.findByGenre_Genre(genre).stream()
                .map(bookMapper::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<Book> findByTitle(String title) {
        return springDataBookRepo.findByTitle_TitleLikeIgnoreCase(title).stream()
                .map(bookMapper::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<Book> findByAuthorName(String authorName) {
        return springDataBookRepo.findByAuthors_Name_NameLikeIgnoreCase(authorName).stream()
                .map(bookMapper::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<Book> findBooksByAuthorNumber(Long authorNumber) {
        return springDataBookRepo.findByAuthors_AuthorNumber(authorNumber).stream()
                .map(bookMapper::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public Page<BookCountDTO> findTop5BooksLent(LocalDate oneYearAgo, Pageable pageable) {
        // AVISO: Esta é uma query de Agregação complexa (JPA JOIN/COUNT).
        // A implementação disto em Mongo requer o uso do 'mongoTemplate.aggregate(...)'
        // para fazer $lookup na coleção de 'lendings', $match pela data, $group, $sort,
        // e $limit.
        // A implementação está fora do escopo deste "scaffolding".
        System.err.println("AVISO: findTop5BooksLent não está implementado para MongoDB.");
        return Page.empty(pageable);
    }

    @Override
    public List<Book> searchBooks(pt.psoft.g1.psoftg1.shared.services.Page page, SearchBooksQuery queryParams) {
        final Query mongoQuery = new Query();
        final List<Criteria> criteria = new ArrayList<>();

        // Pesquisa por Título (funciona bem, é um campo embutido)
        if (StringUtils.hasText(queryParams.getTitle())) {
            criteria.add(Criteria.where("title.title").regex(queryParams.getTitle(), "i")); // 'i' para case-insensitive
        }

        // AVISO: Pesquisa por Genre e AuthorName (campos @DBRef)
        // A pesquisa em campos referenciados (@DBRef) como 'genre' e 'authors'
        // não funciona com 'mongoTemplate.find()'.
        // É necessário usar 'mongoTemplate.aggregate(...)' com $lookup.

        if (StringUtils.hasText(queryParams.getGenre())) {
            System.err.println("AVISO: searchBooks por Genre não suportado nesta implementação simples.");
        }
        if (StringUtils.hasText(queryParams.getAuthorName())) {
            System.err.println("AVISO: searchBooks por AuthorName não suportado nesta implementação simples.");
        }

        // Aplicar critérios
        if (!criteria.isEmpty()) {
            mongoQuery.addCriteria(new Criteria().andOperator(criteria.toArray(new Criteria[0])));
        }

        // Aplicar paginação
        mongoQuery.limit(page.getLimit());
        mongoQuery.skip((long) (page.getNumber() - 1) * page.getLimit());

        // Executar query
        List<BookMongoEntity> entities = mongoTemplate.find(mongoQuery, BookMongoEntity.class);

        return entities.stream()
                .map(bookMapper::toModel)
                .collect(Collectors.toList());
    }
}