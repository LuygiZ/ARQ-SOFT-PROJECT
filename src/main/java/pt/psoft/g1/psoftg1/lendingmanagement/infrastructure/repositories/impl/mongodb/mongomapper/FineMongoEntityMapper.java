package pt.psoft.g1.psoftg1.lendingmanagement.infrastructure.repositories.impl.mongodb.mongomapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pt.psoft.g1.psoftg1.lendingmanagement.model.Fine;
import pt.psoft.g1.psoftg1.lendingmanagement.model.mongodb.FineMongoEntity;

@Mapper(componentModel = "spring", uses = { LendingMongoEntityMapper.class })
public interface FineMongoEntityMapper {

    @Mapping(target = "lending", ignore = true) // 'pk' não existe no Mongo
    Fine toModel(FineMongoEntity entity);

    @Mapping(target = "fineId", ignore = true) // O Mongo gera o ID
    FineMongoEntity toMongoEntity(Fine model);
}