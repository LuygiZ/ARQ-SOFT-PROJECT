package pt.psoft.g1.psoftg1.authormanagement.infrastructure.repositories.impl.mongodb.mongomapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import pt.psoft.g1.psoftg1.authormanagement.model.Bio;
import pt.psoft.g1.psoftg1.authormanagement.model.mongodb.BioMongoEntity;

@Mapper(componentModel = "spring")
public interface BioMongoEntityMapper {

    @Mapping(source = "bio", target = "bio")
    Bio toModel(BioMongoEntity entity);

    @Mapping(source = "value", target = "bio")

    @Mapping(target = "bioId", ignore = true)
    BioMongoEntity toMongoEntity(Bio model);

    default Bio map(String value) {
        return value == null ? null : new Bio(value);
    }
}
