package pt.psoft.g1.psoftg1.readermanagement.model.mongodb;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import pt.psoft.g1.psoftg1.genremanagement.model.sql.GenreSqlEntity;
import pt.psoft.g1.psoftg1.shared.model.sql.EntityWithPhotoSqlEntity;
import pt.psoft.g1.psoftg1.shared.model.sql.PhotoSqlEntity;
import pt.psoft.g1.psoftg1.usermanagement.model.sql.ReaderSqlEntity;

import java.util.List;

@Profile("sql")
@Primary
@Entity
@Table(name = "READER_DETAILS")
public class ReaderDetailsMongoEntity extends EntityWithPhotoSqlEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long pk;

    @Getter
    @Setter
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "reader_id", referencedColumnName = "USER_ID")
    private ReaderSqlEntity reader;

    @Embedded
    @Getter
    @Setter
    private ReaderNumberMongoEntity readerNumber;

    @Embedded
    @Getter
    @Setter
    private BirthDateMongoEntity birthDate;

    @Embedded
    @Getter
    @Setter
    private PhoneNumberMongoEntity phoneNumber;

    @Basic
    @Getter
    @Setter
    private boolean gdprConsent;

    @Getter
    @Setter
    @Basic
    private boolean marketingConsent;

    @Getter
    @Setter
    @Basic
    private boolean thirdPartySharingConsent;

    @Version
    @Getter
    @Setter
    private Long version;

    @Getter
    @Setter
    @ManyToMany
    private List<GenreSqlEntity> interestList;

    protected ReaderDetailsMongoEntity() {
    }

    public ReaderDetailsMongoEntity(ReaderNumberMongoEntity readerNumber, ReaderSqlEntity reader,
            BirthDateMongoEntity birthDate, PhoneNumberMongoEntity phoneNumber,
            boolean gdprConsent, boolean marketingConsent, boolean thirdPartySharingConsent,
            PhotoSqlEntity photo, List<GenreSqlEntity> interestList) {
        setReader(reader);
        setReaderNumber(readerNumber);
        setPhoneNumber(phoneNumber);
        setBirthDate(birthDate);
        setGdprConsent(gdprConsent);
        setPhoto(photo);
        setMarketingConsent(marketingConsent);
        setThirdPartySharingConsent(thirdPartySharingConsent);
        setInterestList(interestList);
    }
}
