package pt.psoft.g1.psoftg1.authormanagement.model.mongodb;

import lombok.Getter;
import lombok.Setter;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import pt.psoft.g1.psoftg1.shared.model.mongodb.EntityWithPhotoMongoEntity;
import pt.psoft.g1.psoftg1.shared.model.mongodb.NameMongoEntity;
import pt.psoft.g1.psoftg1.shared.model.mongodb.PhotoMongoEntity;

@Document(collection = "authors")
@Getter
@Setter
@Profile("mongodb")
@Primary
public class AuthorMongoEntity extends EntityWithPhotoMongoEntity {

    @Id
    private Long authorNumber;

    @Version
    private long version;

    @Field("name")
    private NameMongoEntity name;

    @Field("bio")
    private BioMongoEntity bio;

    @Field("photo_uri")
    private PhotoMongoEntity photo;

    public AuthorMongoEntity(NameMongoEntity name, BioMongoEntity bio, PhotoMongoEntity photoURI) {
        this.name = name;
        this.bio = bio;
        this.photo = photoURI;
    }

    protected AuthorMongoEntity() {
        // got ORM only
    }

}
