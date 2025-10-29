package pt.psoft.g1.psoftg1.authormanagement.services;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import pt.psoft.g1.psoftg1.authormanagement.infrastructure.repositories.impl.sql.sqlmapper.BioEntityMapper;
import pt.psoft.g1.psoftg1.authormanagement.model.Author;
import pt.psoft.g1.psoftg1.shared.api.MapperInterface;
import pt.psoft.g1.psoftg1.shared.infrastructure.repositories.impl.sql.sqlmapper.NameEntityMapper;
import pt.psoft.g1.psoftg1.shared.model.Name;

import pt.psoft.g1.psoftg1.authormanagement.model.Author;
import pt.psoft.g1.psoftg1.authormanagement.model.Bio;
import pt.psoft.g1.psoftg1.shared.api.MapperInterface;
import pt.psoft.g1.psoftg1.shared.model.Name;
import pt.psoft.g1.psoftg1.shared.model.Photo;
import java.nio.file.Path;

@Mapper(componentModel = "spring")
public abstract class AuthorMapper extends MapperInterface {

    @Mapping(target = "authorNumber", ignore = true)
    public abstract Author create(CreateAuthorRequest request);

    @Mapping(target = "authorNumber", ignore = true)
    public abstract void update(UpdateAuthorRequest request, @MappingTarget Author author);

    // Custom mapping methods
    public Name stringToName(String value) {
        return new Name(value);
    }

    public Bio stringToBio(String value) {
        return new Bio(value);
    }

    public Photo multipartFileToPhoto(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }
        return new Photo(Path.of(file.getOriginalFilename()));
    }

}