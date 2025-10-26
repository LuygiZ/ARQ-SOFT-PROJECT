package pt.psoft.g1.psoftg1.shared.model.mongodb;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

@Profile("mongodb")
@Primary
@Setter
@Getter
@PropertySource({ "classpath:config/library.properties" })
public class NameMongoEntity {

    @Id
    private String nameId;

    @NotNull
    @NotBlank
    @Field("name")
    private String name;

    public NameMongoEntity(String name) {
        this.name = name;
    }

    protected NameMongoEntity() {
        // for ORM only
    }
}
