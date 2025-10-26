package pt.psoft.g1.psoftg1.bookmanagement.model.mongodb;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import pt.psoft.g1.psoftg1.bookmanagement.model.Description;

@Profile("mongodb")
@Primary
@Getter
@Setter
public class DescriptionMongoEntity {

    @Id
    private String DescriptionId;

    @Size(max = Description.DESC_MAX_LENGTH)
    @Field("description")
    private String description;

    public DescriptionMongoEntity(String description) {
        this.description = description;
    }

    protected DescriptionMongoEntity() {
    }
}
