package pt.psoft.g1.psoftg1.lendingmanagement.model.mongodb;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import pt.psoft.g1.psoftg1.bookmanagement.model.mongodb.BookMongoEntity;

import pt.psoft.g1.psoftg1.lendingmanagement.model.LendingNumber;

import pt.psoft.g1.psoftg1.readermanagement.model.mongodb.ReaderDetailsMongoEntity;

import java.time.LocalDate;

/**
 * The {@code Lending} class associates a {@code Reader} and a {@code Book}.
 * <p>
 * It stores the date it was registered, the date it is supposed to
 * be returned, and the date it actually was returned.
 * It also stores an optional reader {@code commentary} (submitted at the time
 * of the return) and
 * the {@code Fine}, if applicable.
 * <p>
 * It is identified in the system by an auto-generated {@code id}, and has a
 * unique-constrained
 * natural key ({@code LendingNumber}) with its own business rules.
 * 
 * @author rmfranca
 */
@Getter
@Setter
@Document(collection = "lendings")
public class LendingMongoEntity {

    @Id
    private String lendingId;

    @Field("lendingNumber")
    private LendingNumber lendingNumber;

    @NotNull
    @DBRef
    private BookMongoEntity book;

    @NotNull
    @DBRef
    private ReaderDetailsMongoEntity readerDetails;

    @NotNull
    @Field("startDate")
    private LocalDate startDate;

    @NotNull
    @Field("limitDate")
    private LocalDate limitDate;

    @Field("returnedDate")
    private LocalDate returnedDate;

    @Version
    private long version;

    @Size(min = 0, max = 1024)
    @Field("commentary")
    private String commentary = null;

    @Transient
    @Field("daysUntilReturn")
    private Integer daysUntilReturn;

    @Transient
    @Field("daysOverdue")
    private Integer daysOverdue;

    @Field("fineValuePerDayInCents")
    private int fineValuePerDayInCents;

    public LendingMongoEntity(LendingNumber lendingNumber, BookMongoEntity book,
            ReaderDetailsMongoEntity readerDetails, LocalDate startDate,
            LocalDate limitDate, int fineValuePerDayInCents) {
        this.lendingNumber = lendingNumber;
        this.book = book;
        this.readerDetails = readerDetails;
        this.startDate = startDate;
        this.limitDate = limitDate;
        this.fineValuePerDayInCents = fineValuePerDayInCents;
        this.returnedDate = null;
        this.commentary = null;
    }

    public String getTitle() {
        return this.book.getTitle().toString();
    }

    public String getLendingNumber() {
        return this.lendingNumber.toString();
    }

    /** Protected empty constructor for ORM only. */
    protected LendingMongoEntity() {
    }

}
