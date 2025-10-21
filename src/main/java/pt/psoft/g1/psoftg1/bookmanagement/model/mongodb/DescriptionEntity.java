package pt.psoft.g1.psoftg1.bookmanagement.model.mongodb;

import org.springframework.context.annotation.Profile;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import jakarta.validation.constraints.Size;
import pt.psoft.g1.psoftg1.bookmanagement.model.Description;

@Profile("mongodb")
@Document(collection = "descriptions")
public class DescriptionEntity {

    @Id
    private String id;

    private final int length = Description.DESC_MAX_LENGTH;

    @Size(max = length)
    @Field("description")
    private String description;

    public DescriptionEntity(Description description) {
        if (description != null) {
            this.description = description.getDescription();
        } else {
            this.description = null;
        }
    }

    protected DescriptionEntity() {
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String toString() {
        return this.description;
    }
}
