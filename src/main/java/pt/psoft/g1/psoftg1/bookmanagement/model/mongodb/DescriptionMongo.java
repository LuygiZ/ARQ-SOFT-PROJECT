package pt.psoft.g1.psoftg1.bookmanagement.model.mongodb;

import org.springframework.context.annotation.Profile;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Size;
import pt.psoft.g1.psoftg1.bookmanagement.model.Description;
import pt.psoft.g1.psoftg1.shared.model.StringUtilsCustom;

@Profile("mongodb")
@Document(collection = "descriptions")
public class DescriptionMongo {

    @Id
    private String id;

    private final int length = Description.DESC_MAX_LENGTH;

    @Size(max = length)
    @Field("description")
    private String description;

    public DescriptionMongo(String description) {
        setDescription(description);
    }

    protected DescriptionMongo() {
    }

    public void setDescription(@Nullable String description) {
        if (description == null || description.isBlank()) {
            this.description = null;
        } else if (description.length() > length) {
            throw new IllegalArgumentException("Description has a maximum of 4096 characters");
        } else {
            this.description = StringUtilsCustom.sanitizeHtml(description);
        }
    }

    public String toString() {
        return this.description;
    }
}
