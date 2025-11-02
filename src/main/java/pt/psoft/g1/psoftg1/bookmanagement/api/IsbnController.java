package pt.psoft.g1.psoftg1.bookmanagement.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.psoft.g1.psoftg1.bookmanagement.model.Isbn;
import pt.psoft.g1.psoftg1.external.service.isbndb.factory.IsbnServiceFactory;
import pt.psoft.g1.psoftg1.external.service.isbndb.services.IsbnService;

/**
 * Public REST API for ISBN Lookup
 * No authentication required
 */
@Tag(name = "ISBN Lookup", description = "Search for book ISBNs using external APIs")
@RestController
@RequestMapping("/api/public/isbn")
@RequiredArgsConstructor
public class IsbnController {

    private final IsbnServiceFactory isbnServiceFactory;

    /**
     * Search for a book's ISBN by title
     *
     * Example: GET /api/public/isbn/search?title=The Hobbit
     *
     * @param title Book title to search for
     * @return Isbn object with ISBN string (or null if not found)
     */
    @Operation(summary = "Search ISBN by book title",
            description = "Searches for a book's ISBN using external APIs (Google Books, Open Library)")
    @GetMapping("/search")
    public ResponseEntity<Isbn> searchIsbn(@RequestParam String title) {

        System.out.println("📚 Searching ISBN for: " + title);

        try {
            IsbnService service = isbnServiceFactory.getProvider();
            Isbn result = service.searchByTitle(title);

            if (result != null && result.getIsbn() != null) {
                System.out.println("✅ Found ISBN: " + result.getIsbn());
            } else {
                System.out.println("❌ No ISBN found for: " + title);
            }

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            System.err.println("💥 Error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.ok(new Isbn(null));
        }
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("ISBN Service is UP");
    }
}