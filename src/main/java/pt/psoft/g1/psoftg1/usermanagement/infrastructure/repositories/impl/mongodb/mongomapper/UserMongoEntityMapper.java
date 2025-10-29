package pt.psoft.g1.psoftg1.usermanagement.infrastructure.repositories.impl.mongodb.mongomapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.SubclassMapping;
import pt.psoft.g1.psoftg1.shared.infrastructure.repositories.impl.mongodb.mongomapper.NameMongoEntityMapper;
import pt.psoft.g1.psoftg1.usermanagement.model.Librarian;
import pt.psoft.g1.psoftg1.usermanagement.model.Reader;
import pt.psoft.g1.psoftg1.usermanagement.model.User;
import pt.psoft.g1.psoftg1.usermanagement.model.mongodb.LibrarianMongoEntity;
import pt.psoft.g1.psoftg1.usermanagement.model.mongodb.ReaderMongoEntity;
import pt.psoft.g1.psoftg1.usermanagement.model.mongodb.UserMongoEntity;

@Mapper(componentModel = "spring", uses = { NameMongoEntityMapper.class })
public interface UserMongoEntityMapper {

    User toModel(UserMongoEntity entity);

    Librarian toModel(LibrarianMongoEntity entity);

    Reader toModel(ReaderMongoEntity entity);

    // --- Mapeamento PARA A PERSISTÊNCIA (Domain -> Mongo Entity) ---

    // Este é o método principal. @SubclassMapping diz-lhe para usar
    // os outros métodos se o 'model' for um Reader ou Librarian.
    @SubclassMapping(source = Librarian.class, target = LibrarianMongoEntity.class)
    @SubclassMapping(source = Reader.class, target = ReaderMongoEntity.class)
    @Mapping(source = "id", target = "userId")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", source = "name")
    UserMongoEntity toMongoEntity(User model);

    @Mapping(source = "id", target = "userId")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", source = "name")
    LibrarianMongoEntity toMongoEntity(Librarian model);

    @Mapping(source = "id", target = "userId")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", source = "name")
    ReaderMongoEntity toMongoEntity(Reader model);
}