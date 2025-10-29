package pt.psoft.g1.psoftg1.readermanagement.infrastructure.repositories.impl.mongodb.mongomapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pt.psoft.g1.psoftg1.readermanagement.model.ReaderDetails;
import pt.psoft.g1.psoftg1.readermanagement.model.mongodb.ReaderDetailsMongoEntity;
import pt.psoft.g1.psoftg1.usermanagement.infrastructure.repositories.impl.mongodb.mongomapper.UserMongoEntityMapper; // Presumido

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Este é o mapper que você forneceu, com a adição do UserMongoEntityMapper.
 * Ele lida com os Value Objects (como Path) e o mapeamento principal.
 */
@Mapper(componentModel = "spring", uses = { UserMongoEntityMapper.class }) // Adicionado UserMapper
public interface ReaderDetailsMongoEntityMapper {

    @Mapping(target = "pk", ignore = true)
    ReaderDetails toModel(ReaderDetailsMongoEntity entity);

    @Mapping(target = "readerdetailId", ignore = true)
    @Mapping(target = "photo", ignore = true)
    ReaderDetailsMongoEntity toMongoEntity(ReaderDetails model);

    // --- Mapeadores de Value Object (do seu ficheiro) ---
    default Path stringToPath(String path) {
        if (path == null) {
            return null;
        }
        return Paths.get(path);
    }

    default String pathToString(Path path) {
        if (path == null) {
            return null;
        }
        return path.toString();
    }

    // (O MapStruct tratará dos outros Value Objects como BirthDate, PhoneNumber,
    // se as classes Mongo correspondentes tiverem construtores que aceitam String)
}