package pt.psoft.g1.psoftg1.lendingmanagement.infrastructure.repositories.impl.mongodb.mongomapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pt.psoft.g1.psoftg1.bookmanagement.infrastructure.repositories.impl.mongodb.mongomapper.BookMongoEntityMapper;
import pt.psoft.g1.psoftg1.lendingmanagement.model.Lending;
import pt.psoft.g1.psoftg1.lendingmanagement.model.mongodb.LendingMongoEntity;
import pt.psoft.g1.psoftg1.readermanagement.infrastructure.repositories.impl.mongodb.mongomapper.ReaderDetailsMongoEntityMapper;

/**
 * Mapper (MapStruct) para converter entre o domínio Lending
 * e a persistência LendingMongoEntity.
 */
@Mapper(componentModel = "spring", uses = {
        BookMongoEntityMapper.class,
        ReaderDetailsMongoEntityMapper.class
})
public interface LendingMongoEntityMapper {

    Lending toModel(LendingMongoEntity entity);

    @Mapping(target = "lendingId", ignore = true) // O Mongo gera o ID
    LendingMongoEntity toMongoEntity(Lending model);
}