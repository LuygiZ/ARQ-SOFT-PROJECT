package pt.psoft.g1.psoftg1.usermanagement.model.sql;

import jakarta.persistence.Entity;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import pt.psoft.g1.psoftg1.usermanagement.model.Role;

@Profile("sql")
@Primary
@Entity
public class ReaderSqlEntity extends UserSqlEntity {

    protected ReaderSqlEntity() {
        // for ORM only
    }

    public ReaderSqlEntity(String username, String password, Role role)
    {
        super(username, password, role);
    }
}
