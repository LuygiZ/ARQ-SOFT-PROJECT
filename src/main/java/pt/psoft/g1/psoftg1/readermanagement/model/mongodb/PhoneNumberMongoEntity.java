package pt.psoft.g1.psoftg1.readermanagement.model.mongodb;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.annotation.Id;

import lombok.Getter;
import lombok.Setter;

@Profile("mongodb")
@Primary
@Getter
@Setter
public class PhoneNumberMongoEntity {

    @Id
    private String phoneNumber;

    public PhoneNumberMongoEntity(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    protected PhoneNumberMongoEntity() {
    }
}
