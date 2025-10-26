package pt.psoft.g1.psoftg1.bookmanagement.model.mongodb;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import pt.psoft.g1.psoftg1.bookmanagement.model.Title;

@Profile("mongodb")
@Primary
@Setter
@Getter
public class TitleMongoEntity {

    @Id
    private String titleId;

    @NotBlank(message = "Title cannot be blank")
    @Size(min = 1, max = Title.TITLE_MAX_LENGTH)
    @Field("title")
    String title;

    protected TitleMongoEntity() {
    }

    public TitleMongoEntity(String title) {
        this.title = title;
    }

}
