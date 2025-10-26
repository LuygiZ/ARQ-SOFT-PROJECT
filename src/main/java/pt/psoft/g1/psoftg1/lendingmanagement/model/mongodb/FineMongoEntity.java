package pt.psoft.g1.psoftg1.lendingmanagement.model.mongodb;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * The {@code Fine} class models a fine applied when a lending is past its due
 * date.
 * <p>
 * It stores its current value, and the associated {@code Lending}.
 * 
 * @author rmfranca
 */
@Getter
@Setter
@Profile("mongodb")
@Primary
@Document(collection = "fines")
public class FineMongoEntity {
    @Id
    private String fineId;

    @PositiveOrZero
    @Field("finevalueperdayincents")
    private int fineValuePerDayInCents;

    /** Fine value in Euro cents */
    @PositiveOrZero
    @Field("centsValue")
    private int centsValue;

    @DBRef
    @Indexed(unique = true)
    private LendingMongoEntity lending;

    public FineMongoEntity(int fineValuePerDayInCents, int centsValue, LendingMongoEntity lending) {
        this.fineValuePerDayInCents = fineValuePerDayInCents;
        this.centsValue = centsValue;
        this.lending = lending;
    }

    /** Protected empty constructor for ORM only. */
    protected FineMongoEntity() {
    }
}
