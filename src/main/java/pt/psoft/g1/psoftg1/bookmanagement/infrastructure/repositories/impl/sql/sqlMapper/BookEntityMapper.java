package pt.psoft.g1.psoftg1.bookmanagement.infrastructure.repositories.impl.sql.sqlMapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pt.psoft.g1.psoftg1.authormanagement.infrastructure.repositories.impl.sql.sqlMapper.AuthorEntityMapper;
import pt.psoft.g1.psoftg1.bookmanagement.model.Book;
import pt.psoft.g1.psoftg1.bookmanagement.model.sql.BookSqlEntity;
import pt.psoft.g1.psoftg1.bookmanagement.model.sql.DescriptionSqlEntity;
import pt.psoft.g1.psoftg1.bookmanagement.model.sql.IsbnSqlEntity;
import pt.psoft.g1.psoftg1.bookmanagement.model.sql.TitleSqlEntity;
import pt.psoft.g1.psoftg1.shared.infrastructure.repositories.impl.sql.sqlMapper.PhotoEntityMapper;

//@Mapper(componentModel = "spring", uses = {GenreEntityMapper.class, AuthorEntityMapper.class, PhotoEntityMapper.class})
//public interface BookEntityMapper
//{
//    @Mapping(target="photoURI", source="photo")
//    Book toModel(BookSqlEntity entity);
//    BookSqlEntity toEntity(Book model);
//
//    // default String map(Isbn value)
//    // {
//    //     return value == null ? null : value.getIsbn();
//    // }
//
//    // default String map(Bio value)
//    // {
//    //     return value == null ? null : value.getValue();
//    // }
//
//    default String map(TitleSqlEntity value)
//    {
//        return value == null ? null : value.getTitle();
//    }
//
//    // default String map(Title value)
//    // {
//    //     return value == null ? null : value.getTitle();
//    // }
//
//    default String map(IsbnSqlEntity entity) {
//        return entity == null ? null : entity.getIsbn();
//    }
//
//    default String map(DescriptionSqlEntity entity) {
//        return entity == null ? null : entity.getDescription();
//    }
//
//    // default Description toDescritpion(String value)
//    // {
//    //     return value == null ? null : new Description(value);
//    // }
//
//    // default Isbn toIsbn(String value)
//    // {
//    //     return new Isbn(value);
//    // }
//}
