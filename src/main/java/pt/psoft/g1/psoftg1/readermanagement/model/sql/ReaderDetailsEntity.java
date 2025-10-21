package pt.psoft.g1.psoftg1.readermanagement.model.sql;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import pt.psoft.g1.psoftg1.genremanagement.model.sql.GenreEntity;
import pt.psoft.g1.psoftg1.shared.model.sql.EntityWithPhotoEntity;
import pt.psoft.g1.psoftg1.shared.model.sql.PhotoEntity;
import pt.psoft.g1.psoftg1.usermanagement.model.sql.ReaderEntity;

import java.util.List;

@Profile("sql")
@Primary
@Entity
@Table(name = "READER_DETAILS")
public class ReaderDetailsEntity extends EntityWithPhotoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long pk;

    @Getter
    @Setter
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "reader_id", referencedColumnName = "USER_ID")
    private ReaderEntity reader;

    @Embedded
    @Getter
    @Setter
    private ReaderNumberEntity readerNumber;

    @Embedded
    @Getter
    @Setter
    private BirthDateEntity birthDate;

    @Embedded
    @Getter
    @Setter
    private PhoneNumberEntity phoneNumber;

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
    private List<GenreEntity> interestList;

    protected ReaderDetailsEntity() {}

    public ReaderDetailsEntity(ReaderNumberEntity readerNumber, ReaderEntity reader, BirthDateEntity birthDate, PhoneNumberEntity phoneNumber,
                               boolean gdprConsent, boolean marketingConsent, boolean thirdPartySharingConsent,
                               PhotoEntity photo, List<GenreEntity> interestList)
    {
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

