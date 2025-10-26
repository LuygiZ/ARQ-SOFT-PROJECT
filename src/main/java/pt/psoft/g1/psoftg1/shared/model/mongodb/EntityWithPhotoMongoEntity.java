package pt.psoft.g1.psoftg1.shared.model.mongodb;

import jakarta.annotation.Nullable;

import lombok.Getter;
import lombok.Setter;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Field;

@Profile("mongodb")
@Setter
@Getter
@Primary
public abstract class EntityWithPhotoMongoEntity {

    @Id
    private String entityWithPhotoId;

    @Nullable
    @DBRef
    @Field("photo")
    protected PhotoMongoEntity photo;

    public EntityWithPhotoMongoEntity(PhotoMongoEntity photo) {
        this.photo = photo;
    }

    protected EntityWithPhotoMongoEntity() {
        // for ORM only
    }
}
