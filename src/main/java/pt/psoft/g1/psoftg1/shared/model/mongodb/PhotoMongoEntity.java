package pt.psoft.g1.psoftg1.shared.model.mongodb;

import org.springframework.data.annotation.Id;
import jakarta.validation.constraints.NotNull;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import lombok.Getter;
import lombok.Setter;

@Document(collection = "photos")
@Profile("mongodb")
@Setter
@Getter
@Primary
public class PhotoMongoEntity {

    @Id
    private String photoId;

    @Field("photoFile")
    @NotNull
    private String photoFile;

    protected PhotoMongoEntity() {
    }

    public PhotoMongoEntity(String photoFile) {
        setPhotoFile(photoFile);
    }
}