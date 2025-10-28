package pt.psoft.g1.psoftg1.authormanagement.services;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import pt.psoft.g1.psoftg1.authormanagement.infrastructure.repositories.impl.mongodb.mongomapper.BioMongoEntityMapper;
import pt.psoft.g1.psoftg1.authormanagement.model.Author;
import pt.psoft.g1.psoftg1.shared.api.MapperInterface;
import pt.psoft.g1.psoftg1.shared.infrastructure.repositories.impl.mongodb.mongomapper.NameMongoEntityMapper;

@Mapper(componentModel = "spring", uses = { BioMongoEntityMapper.class, NameMongoEntityMapper.class })
public abstract class AuthorMapper extends MapperInterface {
    // @Mapping(target = "photo", source = "photoURI")

    @Mapping(target = "photo", ignore = true)
    public abstract Author create(CreateAuthorRequest request);

    @Mapping(target = "photo", ignore = true)
    public abstract void update(UpdateAuthorRequest request, @MappingTarget Author author);

}
