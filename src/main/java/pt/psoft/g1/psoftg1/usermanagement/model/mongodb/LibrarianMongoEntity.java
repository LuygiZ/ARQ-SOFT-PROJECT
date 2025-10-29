package pt.psoft.g1.psoftg1.usermanagement.model.mongodb;

import java.time.LocalDateTime;
import java.util.Set;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;

import pt.psoft.g1.psoftg1.shared.model.mongodb.NameMongoEntity;
import pt.psoft.g1.psoftg1.usermanagement.model.Role;

@Document(collection = "users") // <-- MUDANÇA AQUI (era "librarians")
@Profile("mongodb")
@Getter
@Setter
@Primary
public class LibrarianMongoEntity extends UserMongoEntity {
    protected LibrarianMongoEntity() {
        // for ORM only
    }

    public LibrarianMongoEntity(
            String username,
            String password,
            LocalDateTime createdAt,
            LocalDateTime modifiedAt,
            String createdBy,
            String modifiedBy,
            boolean enabled,
            NameMongoEntity name,
            Set<Role> authorities) {
        super(username, password, createdAt, modifiedAt, createdBy, modifiedBy, enabled, name, authorities);
    }

}
