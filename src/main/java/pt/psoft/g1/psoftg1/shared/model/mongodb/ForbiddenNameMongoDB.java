package pt.psoft.g1.psoftg1.shared.model.mongodb;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Profile("mongodb")
@Document(collection = "forbiddenNames")
@NoArgsConstructor
@Getter
@Setter
@Primary
public class ForbiddenNameMongoDB {

    @Id
    private String forbiddenNameId;

    @Size(min = 1)
    @Getter
    @Setter
    @Field("forbidden_name")
    private String forbiddenName;

    public ForbiddenNameMongoDB(String forbiddenName) {
        this.forbiddenName = forbiddenName;
    }
}