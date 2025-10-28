package pt.psoft.g1.psoftg1.readermanagement.infrastructure.repositories.impl.mongodb.mongomapper;

import org.mapstruct.Mapper;

import pt.psoft.g1.psoftg1.readermanagement.model.ReaderDetails;
import pt.psoft.g1.psoftg1.readermanagement.model.mongodb.ReaderDetailsMongoEntity;

import java.nio.file.Path;
import java.nio.file.Paths;
@Mapper(componentModel = "spring")
public interface ReaderDetailsMongoEntityMapper {
    ReaderDetails toModel(ReaderDetailsMongoEntity entity);

    ReaderDetailsMongoEntity toMongoEntity(ReaderDetails model);

    default Path stringToPath(String path) {
        if (path == null) {
            return null;
        }
        // Use Paths.get() (Java 7+) or Path.of() (Java 11+)
        return Paths.get(path);
    }

    default String pathToString(Path path) {
        if (path == null) {
            return null;
        }
        return path.toString();
    }
}