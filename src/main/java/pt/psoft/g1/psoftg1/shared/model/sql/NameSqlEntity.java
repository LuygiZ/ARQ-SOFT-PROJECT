package pt.psoft.g1.psoftg1.shared.model.sql;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

@Profile("sql")
@Primary
@Embeddable
@PropertySource({"classpath:config/library.properties"})
@Getter
public class NameSqlEntity {
    @Column(name = "NAME", length = 150)
    @NotNull
    @NotBlank
    @Getter
    @Setter
    private String name;

    protected NameSqlEntity()
    {

    }

    public NameSqlEntity(String name)
    {
        setName(name);
    }
}
