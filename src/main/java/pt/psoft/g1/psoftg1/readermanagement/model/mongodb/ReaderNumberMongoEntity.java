package pt.psoft.g1.psoftg1.readermanagement.model.mongodb;

import java.io.Serializable;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Getter;
import lombok.Setter;

@Profile("mongodb")
@Primary
@Getter
@Setter
public class ReaderNumberMongoEntity implements Serializable {

    @Id
    private String readernumberId;

    @Field("reader_number")
    private String readerNumber;

    public ReaderNumberMongoEntity(String readerNumber) {
        this.readerNumber = readerNumber;
    }

    protected ReaderNumberMongoEntity() {
    }

}
