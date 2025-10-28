package pt.psoft.g1.psoftg1.shared.infrastructure.repositories.impl.sql.sqlMapper;

import org.mapstruct.Mapper;

import pt.psoft.g1.psoftg1.shared.model.ForbiddenName;
import pt.psoft.g1.psoftg1.shared.model.sql.ForbiddenNameSqlEntity;

@Mapper(componentModel = "spring")
public interface ForbiddenNameEntityMapper
{
    ForbiddenName toModel(ForbiddenNameSqlEntity entity);
    ForbiddenNameSqlEntity toEntity(ForbiddenName model);
}
