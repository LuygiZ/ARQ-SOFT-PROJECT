package pt.psoft.g1.psoftg1.shared.model.mongodb;

import org.springframework.context.annotation.Profile;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Getter;
import lombok.Setter;

@Profile("mongodb")
@Document(collection = "forbiddenNames")
@Getter
@Setter
public class ForbiddenNameMongoEntity {

    @Id
    private String forbiddenNameId;

    @Indexed(unique = true)
    private String forbiddenName;

    public ForbiddenNameMongoEntity(String forbiddenName) {
        this.forbiddenName = forbiddenName;
    }

    protected ForbiddenNameMongoEntity() {
        // para ORM
    }
}