
package pt.psoft.g1.psoftg1.readermanagement.model.mongodb;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class EmailAddressMongoEntity {
    @Email
    String address;

    protected EmailAddressMongoEntity() {
    }
}
