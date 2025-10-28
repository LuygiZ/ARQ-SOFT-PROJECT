package pt.psoft.g1.psoftg1.authormanagement.infrastructure.repositories.impl.mongodb.mongomapper;

import java.nio.file.Paths;

import org.mapstruct.Mapper;

import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import pt.psoft.g1.psoftg1.authormanagement.model.Author;
import pt.psoft.g1.psoftg1.authormanagement.model.mongodb.AuthorMongoEntity;
import pt.psoft.g1.psoftg1.authormanagement.services.CreateAuthorRequest;

import pt.psoft.g1.psoftg1.shared.infrastructure.repositories.impl.mongodb.mongomapper.NameMongoEntityMapper;
import pt.psoft.g1.psoftg1.shared.model.Photo;

@Mapper(componentModel = "spring", uses = { BioMongoEntityMapper.class, NameMongoEntityMapper.class })

public interface AuthorMongoEntityMapper {

    AuthorMongoEntityMapper INSTANCE = Mappers.getMapper(AuthorMongoEntityMapper.class);

    @Mappings({
            @Mapping(target = "authorNumber", source = "authorNumber"),
            @Mapping(source = "photo", target = "photoURI", qualifiedByName = "photoToUri")
    })
    AuthorMongoEntity toMongoEntity(Author author);

    Author toModel(AuthorMongoEntity authorMongoDB);

    @Mapping(target = "photo", source = "photoURI", qualifiedByName = "uriToPhoto")
    Author create(CreateAuthorRequest request);

    @Named("uriToPhoto")
    default Photo uriToPhoto(String photoURI) {
        if (photoURI == null) {
            return null;
        }

        return new Photo(Paths.get(photoURI));
    }

    @Named("photoToUri")
    default String photoToUri(Photo photo) {
        if (photo == null) {
            return null;
        }
        return photo.getPhotoFile();
    }

}
