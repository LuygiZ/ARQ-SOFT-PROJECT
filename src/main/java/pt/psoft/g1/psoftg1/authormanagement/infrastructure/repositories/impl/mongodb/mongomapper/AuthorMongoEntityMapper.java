package pt.psoft.g1.psoftg1.authormanagement.infrastructure.repositories.impl.mongodb.mongomapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import pt.psoft.g1.psoftg1.authormanagement.model.Author;
import pt.psoft.g1.psoftg1.authormanagement.model.mongodb.AuthorMongoEntity;
import pt.psoft.g1.psoftg1.authormanagement.services.CreateAuthorRequest;

import pt.psoft.g1.psoftg1.shared.infrastructure.repositories.impl.mongodb.mongomapper.NameMongoEntityMapper;
import pt.psoft.g1.psoftg1.shared.infrastructure.repositories.impl.mongodb.mongomapper.PhotoMongoEntityMapper;

@Mapper(componentModel = "spring", uses = { BioMongoEntityMapper.class, NameMongoEntityMapper.class,
        PhotoMongoEntityMapper.class })

public interface AuthorMongoEntityMapper {

    AuthorMongoEntityMapper INSTANCE = Mappers.getMapper(AuthorMongoEntityMapper.class);

    @Mappings({
            @Mapping(target = "authorNumber", source = "authorNumber"),
    })
    AuthorMongoEntity toMongoEntity(Author author);

    Author toModel(AuthorMongoEntity authorMongoDB);

    Author create(CreateAuthorRequest request);

}
