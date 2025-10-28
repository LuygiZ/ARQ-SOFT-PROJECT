package pt.psoft.g1.psoftg1.shared.infrastructure.repositories.impl.mongodb.mongomapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import pt.psoft.g1.psoftg1.shared.model.Name;
import pt.psoft.g1.psoftg1.shared.model.mongodb.NameMongoEntity;

@Mapper(componentModel = "spring")
public interface NameMongoEntityMapper {

    Name toModel(NameMongoEntity entity);

    @Mapping(target = "nameId", ignore = true)
    NameMongoEntity toMongoEntity(Name model);

    default String map(NameMongoEntity value) {
        return value == null ? null : value.getName();
    }

    default String map(Name value) {
        return value == null ? null : value.getName();
    }

    default Name map(String value) {
        return value == null ? null : new Name(value);
    }
}
