package pt.psoft.g1.psoftg1.bookmanagement.model.mongodb;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

@Profile("mongodb")
@Primary
@EqualsAndHashCode
@Getter
@Setter
public class IsbnMongoEntity implements Serializable {

    @Id
    private String isbnId;

    @NotNull
    @Size(min = 10, max = 13)
    @Field("isbn")
    private String isbn;

    public IsbnMongoEntity(String isbn) {
        this.isbn = isbn;
    }

    protected IsbnMongoEntity() {
    };

}
