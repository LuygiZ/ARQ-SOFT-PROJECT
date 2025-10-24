package pt.psoft.g1.psoftg1.authormanagement.model.mongodb;

import lombok.Getter;
import lombok.Setter;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import pt.psoft.g1.psoftg1.shared.model.EntityWithPhoto;

import pt.psoft.g1.psoftg1.shared.model.mongodb.NameMongoEntity;

@Document(collection = "authors")
@Getter
@Setter
@Profile("mongodb")
@Primary
public class AuthorMongoEntity extends EntityWithPhoto {

    @Id
    private Long authorNumber;

    @Version
    private long version;

    @Field("name")
    private NameMongoEntity name;

    @Field("bio")
    private BioMongoEntity bio;

    public AuthorMongoEntity(NameMongoEntity name, BioMongoEntity bio, String photoURI) {
        this.name = name;
        this.bio = bio;
        setPhotoInternal(photoURI);
    }

    protected AuthorMongoEntity() {
        // got ORM only
    }

}
