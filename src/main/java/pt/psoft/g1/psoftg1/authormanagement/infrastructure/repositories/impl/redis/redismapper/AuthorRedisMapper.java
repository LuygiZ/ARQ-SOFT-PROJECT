package pt.psoft.g1.psoftg1.authormanagement.infrastructure.repositories.impl.redis.redismapper;

import org.springframework.stereotype.Component;
import pt.psoft.g1.psoftg1.authormanagement.model.Author;
import pt.psoft.g1.psoftg1.authormanagement.model.Bio;
import pt.psoft.g1.psoftg1.shared.model.Name;

import java.util.HashMap;
import java.util.Map;

@Component
public class AuthorRedisMapper {

    /**
     * Converte Author para Redis Hash
     */
    public Map<String, String> toRedisHash(Author author) {
        Map<String, String> hash = new HashMap<>();

        // ✅ CRÍTICO: Guardar authorNumber!
        if (author.getAuthorNumber() != null) {
            hash.put("authorNumber", author.getAuthorNumber().toString());
        }

        if (author.getName() != null) {
            hash.put("name", author.getName().getName());
        }

        if (author.getBio() != null) {
            hash.put("bio", author.getBio().toString());
        }

        if (author.getPhoto() != null) {
            hash.put("photo", author.getPhoto().getPhotoFile());
        }

        return hash;
    }

    /**
     * Converte Redis Hash para Author
     */
    public Author fromRedisHash(Map<Object, Object> hash) {
        if (hash == null || hash.isEmpty()) {
            return null;
        }

        // ✅ CRÍTICO: Recuperar authorNumber!
        Long authorNumber = null;
        if (hash.containsKey("authorNumber")) {
            authorNumber = Long.parseLong((String) hash.get("authorNumber"));
        }

        String name = (String) hash.get("name");
        String bio = (String) hash.get("bio");
        String photo = (String) hash.get("photo");

        // ✅ Criar Author com authorNumber
        Author author = new Author(name, bio, photo);

        // ✅ IMPORTANTE: Setar o authorNumber recuperado do cache
        if (authorNumber != null) {
            // Assumindo que existe um setter ou método para setar authorNumber
            author.setAuthorNumber(authorNumber);
            // OU usar reflection se não houver setter
        }

        return author;
    }
}