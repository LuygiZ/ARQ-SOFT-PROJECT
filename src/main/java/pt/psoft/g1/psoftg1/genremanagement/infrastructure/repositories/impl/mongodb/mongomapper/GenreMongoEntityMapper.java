package pt.psoft.g1.psoftg1.genremanagement.infrastructure.repositories.impl.mongodb.mongomapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pt.psoft.g1.psoftg1.genremanagement.model.Genre;
import pt.psoft.g1.psoftg1.genremanagement.model.mongodb.GenreMongoEntity;

/**
 * Mapper (MapStruct) para converter entre o modelo de domínio Genre
 * e a entidade de persistência GenreMongoEntity.
 *
 * O método toEntity foi renomeado para toMongoEntity para maior clareza.
 */
@Mapper(componentModel = "spring")
public interface GenreMongoEntityMapper {

    /**
     * Converte a entidade de persistência (Mongo) para o modelo de domínio.
     */
    @Mapping(target = "pk", ignore = true)
    Genre toModel(GenreMongoEntity entity);

    /**
     * Converte o modelo de domínio para a entidade de persistência (Mongo).
     * MUDANÇA: Renomeado de 'toEntity' para 'toMongoEntity'.
     */
    @Mapping(target = "genreId", ignore = true)
    @Mapping(source = "genre", target = "genre")
    GenreMongoEntity toMongoEntity(Genre model); // <-- MUDANÇA AQUI
}