package pt.psoft.g1.psoftg1.genremanagement.infrastructure.repositories.impl.sql.sqlmapper;

import org.mapstruct.Mapper;
import pt.psoft.g1.psoftg1.genremanagement.model.Genre;
import pt.psoft.g1.psoftg1.genremanagement.model.sql.GenreSqlEntity;

@Mapper(componentModel = "spring")
public interface GenreEntityMapper {
    Genre toModel(GenreSqlEntity entity);

    GenreSqlEntity toEntity(Genre model);
}
