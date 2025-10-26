package pt.psoft.g1.psoftg1.readermanagement.model.mongodb;

import lombok.Getter;

import lombok.Setter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;

@Profile("mongodb")
@PropertySource({ "classpath:config/library.properties" })
@Getter
@Setter
public class BirthDateMongoEntity {

    @Id
    private String birthdateId;

    @Getter
    @Field("birthDate")
    LocalDate birthDate;;

    @Value("${minimumReaderAge}")
    @Field("minimumAge")
    private int minimumAge;

    public BirthDateMongoEntity(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    protected BirthDateMongoEntity() {
    }

}
