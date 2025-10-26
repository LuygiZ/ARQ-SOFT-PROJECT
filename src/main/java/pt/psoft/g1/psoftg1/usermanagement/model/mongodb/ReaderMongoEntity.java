package pt.psoft.g1.psoftg1.usermanagement.model.mongodb;

import java.time.LocalDateTime;
import java.util.Set;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.mapping.Document;

import pt.psoft.g1.psoftg1.shared.model.Name;
import pt.psoft.g1.psoftg1.usermanagement.model.Role;

@Document(collection = "readers")
@Profile("mongodb")
@Primary
public class ReaderMongoEntity extends UserMongoEntity {
    protected ReaderMongoEntity() {
        // for ORM only
    }

    public ReaderMongoEntity(String username,
            String password,
            LocalDateTime createdAt,
            LocalDateTime modifiedAt,
            String createdBy,
            String modifiedBy,
            boolean enabled,
            Name name,
            Set<Role> authorities) {
        super(username, password, createdAt, modifiedAt, createdBy, modifiedBy, enabled, name, authorities);
    }

}
