package pt.psoft.g1.psoftg1.lendingmanagement.model.mongodb;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "lendings")
@Profile("mongodb")
@Primary
@Getter
@Setter
public class LendingNumberMongoEntity implements Serializable {

    @Id
    private String lendingid;

    @Field("lending_number")
    @NotNull
    @NotBlank
    @Size(min = 6, max = 32)
    private String lendingNumber;

    public LendingNumberMongoEntity(String lendingNumber) {
        this.lendingNumber = lendingNumber;
    }

    /** Protected empty constructor for ORM only. */
    public LendingNumberMongoEntity() {
    }

    public String toString() {
        return this.lendingNumber;
    }

}
