package pt.psoft.g1.psoftg1.shared.model.SQL;

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
public class NameEntity {
    @Column(name = "NAME", length = 150)
    @NotNull
    @NotBlank
    @Getter
    @Setter
    private String name;

    protected NameEntity()
    {

    }

    public NameEntity(String name)
    {
        setName(name);
    }
}
