package pt.psoft.g1.psoftg1.shared.infrastructure.repositories.impl.mongodb.mongomapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import pt.psoft.g1.psoftg1.shared.model.Photo;
import pt.psoft.g1.psoftg1.shared.model.mongodb.PhotoMongoEntity;

import java.nio.file.Path;
import java.nio.file.Paths;

@Mapper(componentModel = "spring")
public interface PhotoMongoEntityMapper {

    Photo toModel(PhotoMongoEntity entity);

    @Mapping(target = "photoId", ignore = true)
    PhotoMongoEntity toMongoEntity(Photo model);

    default Path stringToPath(String value) {
        if (value == null) {
            return null;
        }
        return Paths.get(value);
    }

    default String pathToString(Path path) {
        if (path == null) {
            return null;
        }
        return path.toString();
    }
}
