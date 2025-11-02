package pt.psoft.g1.psoftg1.external.service.isbndb.factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pt.psoft.g1.psoftg1.external.service.isbndb.services.impl.FallbackIsbnService;
import pt.psoft.g1.psoftg1.external.service.isbndb.services.impl.GoogleBooksIsbnService;
import pt.psoft.g1.psoftg1.external.service.isbndb.services.IsbnService;
import pt.psoft.g1.psoftg1.external.service.isbndb.services.impl.OpenLibraryIsbnService;

/**
 * Factory for creating ISBN lookup service instances
 * Strategy pattern implementation
 */
@Component
public class IsbnServiceFactory {

    private final GoogleBooksIsbnService googleBooksProvider;

    private final OpenLibraryIsbnService openLibraryProvider;

    private final FallbackIsbnService fallbackIsbnProvider;

    @Value("${isbn.provider}")
    private String activeProvider;

    @Autowired
    public IsbnServiceFactory(GoogleBooksIsbnService googleBooksProvider, OpenLibraryIsbnService openLibraryProvider, FallbackIsbnService fallbackIsbnProvider) {
        this.googleBooksProvider = googleBooksProvider;
        this.openLibraryProvider = openLibraryProvider;
        this.fallbackIsbnProvider = fallbackIsbnProvider;
    }

    public IsbnService getProvider() {
        return switch (activeProvider.toLowerCase()) {
            case "googlebooks" -> googleBooksProvider;
            case "openlibrary" -> openLibraryProvider;
            case "gb_and_ol", "fallback" -> fallbackIsbnProvider;
            default -> fallbackIsbnProvider; // fallback
        };
    }
}