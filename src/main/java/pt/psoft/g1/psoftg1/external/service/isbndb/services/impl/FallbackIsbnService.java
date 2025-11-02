package pt.psoft.g1.psoftg1.external.service.isbndb.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pt.psoft.g1.psoftg1.bookmanagement.model.Isbn;
import pt.psoft.g1.psoftg1.external.service.isbndb.services.IsbnService;

/**
 * Fallback ISBN lookup service that tries multiple providers
 * Priority order can be configured via application properties
 */
@Component
@RequiredArgsConstructor
public class FallbackIsbnService implements IsbnService {

    private final OpenLibraryIsbnService openLibraryProvider;
    private final GoogleBooksIsbnService googleBooksProvider;

    @Override
    public Isbn searchByTitle(String title) {
        try {
            Isbn result = openLibraryProvider.searchByTitle(title);
            if (result != null && result.getIsbn() != null) {
                return result;
            }
        } catch (Exception e) {
            System.out.println("[FallbackIsbnProvider] OpenLibrary falhou: " + e.getMessage());
        }

        // fallback para Google Books
        try {
            return googleBooksProvider.searchByTitle(title);
        } catch (Exception e) {
            System.out.println("[FallbackIsbnProvider] GoogleBooks falhou: " + e.getMessage());
            throw new RuntimeException("Falha em ambos os provedores.");
        }
    }
}