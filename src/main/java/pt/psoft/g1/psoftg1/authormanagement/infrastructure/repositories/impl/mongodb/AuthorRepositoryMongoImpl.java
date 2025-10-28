package pt.psoft.g1.psoftg1.authormanagement.infrastructure.repositories.impl.mongodb;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pt.psoft.g1.psoftg1.authormanagement.api.AuthorLendingView;
import pt.psoft.g1.psoftg1.authormanagement.model.Author;
import pt.psoft.g1.psoftg1.authormanagement.model.mongodb.AuthorMongoEntity;
import pt.psoft.g1.psoftg1.authormanagement.repositories.AuthorRepository;
import pt.psoft.g1.psoftg1.authormanagement.infrastructure.repositories.impl.mongodb.mongomapper.AuthorMongoEntityMapper;

import java.util.*;

@Profile("mongodb")
@Qualifier("mongoDbRepo")
@RequiredArgsConstructor
public class AuthorRepositoryMongoImpl implements AuthorRepository {

    private final SpringDataAuthorRepositoryMongo authoRepo;
    private final AuthorMongoEntityMapper authorEntityMapper;

    @Override
    public Optional<Author> findByAuthorNumber(Long authorNumber) {
        Optional<AuthorMongoEntity> entityOpt = authoRepo.findByAuthorNumber(authorNumber.toString());
        if (entityOpt.isPresent()) {
            return Optional.of(authorEntityMapper.toModel(entityOpt.get()));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public List<Author> searchByNameNameStartsWith(String name) {
        List<Author> authors = new ArrayList<>();
        for (AuthorMongoEntity a : authoRepo.searchByNameNameStartsWith(name)) {
            authors.add(authorEntityMapper.toModel(a));
        }

        return authors;
    }

    @Override
    public List<Author> searchByNameName(String name) {
        List<Author> authors = new ArrayList<>();
        for (AuthorMongoEntity a : authoRepo.searchByNameName(name)) {
            authors.add(authorEntityMapper.toModel(a));
        }

        return authors;
    }

    @Override
    public Author save(Author author) {
        return authorEntityMapper.toModel(authoRepo.save(authorEntityMapper.toMongoEntity(author)));
    }

    @Override
    public Iterable<Author> findAll() {
        List<Author> authors = new ArrayList<>();
        for (AuthorMongoEntity a : authoRepo.findAll()) {
            authors.add(authorEntityMapper.toModel(a));
        }

        return authors;
    }

    @Override
    public Page<AuthorLendingView> findTopAuthorByLendings(Pageable pageableRules) {
        return authoRepo.findTopAuthorByLendings(pageableRules);
    }

    @Override
    public void delete(Author author) {
        authoRepo.delete(authorEntityMapper.toMongoEntity(author));
    }

    @Override
    public List<Author> findCoAuthorsByAuthorNumber(Long authorNumber) {
        List<Author> authors = new ArrayList<>();
        for (AuthorMongoEntity a : authoRepo.findCoAuthorsByAuthorNumber(authorNumber.toString())) {
            authors.add(authorEntityMapper.toModel(a));
        }

        return authors;
    }
}