package pt.psoft.g1.psoftg1.shared.model.mongodb;

import org.springframework.context.annotation.Profile;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Getter;
import lombok.Setter;

@Profile("mongodb")
@Document(collection = "photos")
@Getter
@Setter
public class PhotoMongoEntity {

    @Id
    private String photoId;

    private String photoFile;

    public PhotoMongoEntity(String photoFile) {
        this.photoFile = photoFile;
    }

    protected PhotoMongoEntity() {
        // para ORM
    }
}