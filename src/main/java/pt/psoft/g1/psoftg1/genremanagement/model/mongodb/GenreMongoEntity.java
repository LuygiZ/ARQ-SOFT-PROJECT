package pt.psoft.g1.psoftg1.genremanagement.model.mongodb;

import org.springframework.context.annotation.Profile;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import pt.psoft.g1.psoftg1.genremanagement.model.Genre;

@Profile("mongodb")
@Document(collection = "genres")
@Getter
@Setter
public class GenreMongoEntity {

    @Id
    public String genreId;

    @Size(min = 1, max = Genre.GENRE_MAX_LENGTH, message = "Genre name must be between 1 and 100 characters")
    @Field("genre")
    public String genre;

    public GenreMongoEntity(String genre) {
        this.genre = genre;
    }

    protected GenreMongoEntity() {
        // for ORM only
    }

}
