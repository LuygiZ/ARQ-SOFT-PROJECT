package pt.psoft.g1.psoftg1.authormanagement.model.mongodb;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Profile("mongodb")
@Primary
public class BioMongoEntity {

    @Id
    private String bioId;

    private final int BIO_MAX_LENGTH = 4096;

    @NotNull
    @Size(min = 1, max = BIO_MAX_LENGTH)
    @Field("bio")
    private String bio;

    public BioMongoEntity(String bio) {
        this.bio = bio;
    }

    protected BioMongoEntity() {
        // for ORM only
    }

}
