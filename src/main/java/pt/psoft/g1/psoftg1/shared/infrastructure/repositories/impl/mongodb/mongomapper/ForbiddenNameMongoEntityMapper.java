package pt.psoft.g1.psoftg1.shared.infrastructure.repositories.impl.mongodb.mongomapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pt.psoft.g1.psoftg1.shared.model.ForbiddenName;
import pt.psoft.g1.psoftg1.shared.model.mongodb.ForbiddenNameMongoEntity;

@Mapper(componentModel = "spring")
public interface ForbiddenNameMongoEntityMapper {

    // (String -> ForbiddenName)
    default ForbiddenName toModel(ForbiddenNameMongoEntity entity) {
        return entity == null ? null : new ForbiddenName(entity.getForbiddenName());
    }

    // (ForbiddenName -> String)
    default String fromModel(ForbiddenName model) {
        return model == null ? null : model.getForbiddenName();
    }

    // (ForbiddenName -> ForbiddenNameMongoEntity)
    @Mapping(target = "forbiddenNameId", ignore = true)
    ForbiddenNameMongoEntity toMongoEntity(ForbiddenName model);

    // Helper para mapear o Value Object
    default String map(ForbiddenName value) {
        return value == null ? null : value.getForbiddenName();
    }
}