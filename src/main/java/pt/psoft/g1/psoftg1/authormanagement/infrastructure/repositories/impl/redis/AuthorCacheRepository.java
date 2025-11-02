package pt.psoft.g1.psoftg1.authormanagement.infrastructure.repositories.impl.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import pt.psoft.g1.psoftg1.authormanagement.api.AuthorLendingView;
import pt.psoft.g1.psoftg1.authormanagement.model.Author;
import pt.psoft.g1.psoftg1.authormanagement.repositories.AuthorRepository;

import java.util.List;
import java.util.Optional;

/**
 * Cache-Aside Pattern Implementation for Author Repository
 *
 * This repository acts as a caching layer between the service and the database.
 * It delegates to Redis for caching and SQL for source of truth.
 *
 * Strategy:
 * - Read: Try cache first, fallback to SQL, then populate cache
 * - Write: Write to SQL first (source of truth), then update cache
 * - Delete: Delete from SQL first, then invalidate cache
 *
 * Active only when:
 * - Profile "sql-redis" is active
 * - Property "repository.cache.enabled" is true (default)
 */
@Slf4j
@Repository("authorCacheRepository")
@Profile("sql-redis")
@Primary
@ConditionalOnProperty(
        name = "repository.cache.enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class AuthorCacheRepository implements AuthorRepository {

    private final AuthorRepository redisRepository;
    private final AuthorRepository sqlRepository;

    public AuthorCacheRepository(
            @Qualifier("redisAuthorRepository") AuthorRepository redisRepository,
            @Qualifier("authorRepositoryImpl") AuthorRepository sqlRepository) {
        this.redisRepository = redisRepository;
        this.sqlRepository = sqlRepository;
        log.info("✅ AuthorCacheRepository initialized with Cache-Aside pattern");
    }

    // ==================== CORREÇÃO CRÍTICA ====================

    @Override
    public List<Author> searchByNameName(String name) {
        if (name == null || name.isBlank()) {
            log.warn("⚠️ searchByNameName called with null/empty name");
            return List.of();
        }

        log.debug("🔍 searchByNameName: '{}'", name);

        // ✅ CORREÇÃO: Usar null como fallback para distinguir erro de cache vazio
        List<Author> cached = safeExecute(
                () -> redisRepository.searchByNameName(name),
                "search cache by exact name",
                null  // ← NULL em vez de List.of()
        );

        // ✅ Se cache retornou resultado (mesmo que vazio), usa
        if (cached != null) {
            if (!cached.isEmpty()) {
                log.info("🎯 CACHE HIT - Exact name: '{}' - {} authors", name, cached.size());
                return cached;
            } else {
                log.debug("📭 Cache returned empty list for: '{}' - trying SQL", name);
            }
        } else {
            log.debug("❌ Cache error/miss for: '{}' - trying SQL", name);
        }

        // ✅ Ir buscar ao SQL (source of truth)
        log.info("📊 CACHE MISS - Fetching from SQL: '{}'", name);
        List<Author> authors = sqlRepository.searchByNameName(name);

        log.info("📊 SQL returned {} authors for: '{}'", authors.size(), name);

        // ✅ Cachear os resultados (se houver)
        if (!authors.isEmpty()) {
            authors.forEach(author -> {
                log.debug("💾 Caching author: {} (ID: {})",
                        author.getName().getName(), author.getAuthorNumber());
                saveToCache(author);
            });
        } else {
            log.warn("⚠️ No authors found in SQL for: '{}'", name);
        }

        return authors;
    }

    @Override
    public List<Author> searchByNameNameStartsWith(String name) {
        if (name == null || name.isBlank()) {
            log.warn("⚠️ searchByNameNameStartsWith called with null/empty name");
            return List.of();
        }

        log.debug("🔍 searchByNameNameStartsWith: '{}'", name);

        // ✅ Mesma correção para starts with
        List<Author> cached = safeExecute(
                () -> redisRepository.searchByNameNameStartsWith(name),
                "search cache by name prefix",
                null
        );

        if (cached != null && !cached.isEmpty()) {
            log.info("🎯 CACHE HIT - Name starts with: '{}' - {} authors", name, cached.size());
            return cached;
        }

        log.info("📊 CACHE MISS - SQL search - Name starts with: '{}'", name);
        List<Author> authors = sqlRepository.searchByNameNameStartsWith(name);

        log.info("📊 SQL returned {} authors for prefix: '{}'", authors.size(), name);

        authors.forEach(this::saveToCache);

        return authors;
    }

    @Override
    public Optional<Author> findByAuthorNumber(Long authorNumber) {
        if (authorNumber == null) {
            log.warn("⚠️ findByAuthorNumber called with null ID");
            return Optional.empty();
        }

        log.debug("🔍 findByAuthorNumber: {}", authorNumber);

        // Try cache first
        Optional<Author> cached = safeExecute(
                () -> redisRepository.findByAuthorNumber(authorNumber),
                "fetch from Redis cache",
                Optional.empty()
        );

        if (cached.isPresent()) {
            log.info("🎯 CACHE HIT - Author #{}", authorNumber);
            return cached;
        }

        // Cache miss - fetch from SQL
        log.info("📊 CACHE MISS - Fetching from SQL - Author #{}", authorNumber);
        Optional<Author> author = sqlRepository.findByAuthorNumber(authorNumber);

        // Populate cache asynchronously
        author.ifPresent(a -> {
            log.debug("💾 Caching author #{}", authorNumber);
            saveToCache(a);
        });

        return author;
    }

    @Override
    public Author save(Author author) {
        if (author == null) {
            throw new IllegalArgumentException("Author cannot be null");
        }

        log.debug("💾 Saving author: {}", author.getAuthorNumber());

        // Write to SQL first (source of truth)
        Author saved = sqlRepository.save(author);
        log.info("✅ Saved to SQL - Author #{}", saved.getAuthorNumber());

        // Update cache (fire and forget)
        saveToCache(saved);

        return saved;
    }

    @Override
    public void delete(Author author) {
        if (author == null) {
            throw new IllegalArgumentException("Author cannot be null");
        }

        log.debug("🗑️ Deleting author: {}", author.getAuthorNumber());

        // Delete from SQL first (source of truth)
        sqlRepository.delete(author);
        log.info("✅ Deleted from SQL - Author #{}", author.getAuthorNumber());

        // Invalidate cache (fire and forget)
        deleteFromCache(author);
    }

    @Override
    public Iterable<Author> findAll() {
        log.debug("📋 Finding all authors (bypassing cache)");
        return sqlRepository.findAll();
    }

    @Override
    public Page<AuthorLendingView> findTopAuthorByLendings(Pageable pageable) {
        log.debug("📊 Finding top authors by lendings (bypassing cache)");
        return sqlRepository.findTopAuthorByLendings(pageable);
    }

    @Override
    public List<Author> findCoAuthorsByAuthorNumber(Long authorNumber) {
        log.debug("👥 Finding co-authors for author #{} (bypassing cache)", authorNumber);
        return sqlRepository.findCoAuthorsByAuthorNumber(authorNumber);
    }

    // ==================== HELPER METHODS ====================

    private void saveToCache(Author author) {
        if (author == null || author.getAuthorNumber() == null) {
            return;
        }

        safeExecute(
                () -> {
                    redisRepository.save(author);
                    log.debug("✅ Cached in Redis - Author #{}: {}",
                            author.getAuthorNumber(), author.getName().getName());
                    return null;
                },
                "save to cache",
                null
        );
    }

    private void deleteFromCache(Author author) {
        if (author == null || author.getAuthorNumber() == null) {
            return;
        }

        safeExecute(
                () -> {
                    redisRepository.delete(author);
                    log.debug("✅ Invalidated Redis cache - Author #{}", author.getAuthorNumber());
                    return null;
                },
                "delete from cache",
                null
        );
    }

    private <T> T safeExecute(CacheOperation<T> operation, String operationName, T fallback) {
        try {
            return operation.execute();
        } catch (Exception e) {
            log.warn("⚠️ Cache operation failed ({}): {} - Using fallback",
                    operationName, e.getMessage());
            return fallback;
        }
    }

    @FunctionalInterface
    private interface CacheOperation<T> {
        T execute();
    }
}