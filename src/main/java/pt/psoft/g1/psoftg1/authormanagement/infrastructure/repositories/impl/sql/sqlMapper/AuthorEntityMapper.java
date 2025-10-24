package pt.psoft.g1.psoftg1.authormanagement.infrastructure.repositories.impl.sql.sqlMapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pt.psoft.g1.psoftg1.authormanagement.model.Author;
import pt.psoft.g1.psoftg1.authormanagement.model.Bio;
import pt.psoft.g1.psoftg1.authormanagement.model.sql.AuthorSqlEntity;
import pt.psoft.g1.psoftg1.authormanagement.model.sql.BioSqlEntity;
import pt.psoft.g1.psoftg1.shared.model.Photo;
import pt.psoft.g1.psoftg1.shared.infrastructure.repositories.impl.sql.sqlMapper.NameEntityMapper;
import pt.psoft.g1.psoftg1.shared.model.Name;
import pt.psoft.g1.psoftg1.shared.model.sql.NameSqlEntity;
import pt.psoft.g1.psoftg1.shared.model.sql.PhotoSqlEntity;

@Mapper(componentModel = "spring", uses = { NameEntityMapper.class, BioEntityMapper.class })
public interface AuthorEntityMapper
{
    Author toModel(AuthorSqlEntity entity);

    @Mapping(source = "photo", target = "photoURI")
    AuthorSqlEntity toEntity(Author model);

    default String map(Photo photo)
    {
        return photo == null ? null : photo.getPhotoFile();
    }

    default String map(PhotoSqlEntity photo)
    {
        return photo == null ? null : photo.getPhotoFile();
    }

    default String map(Name value)
    {
        return value == null ? null : value.getName();
    }

    default String map(Bio value)
    {
        return value == null ? null : value.getValue();
    }

    default String map(BioSqlEntity value)
    {
        return value == null ? null : value.getBio();
    }

    default NameSqlEntity map(String value) {
        return value == null ? null : new NameSqlEntity(value);
    }

    default String map(NameSqlEntity value) {
        return value == null ? null : value.getName();
    }
}