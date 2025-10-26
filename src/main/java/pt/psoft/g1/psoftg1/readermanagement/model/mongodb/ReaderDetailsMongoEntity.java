package pt.psoft.g1.psoftg1.readermanagement.model.mongodb;

import lombok.Getter;
import lombok.Setter;

import pt.psoft.g1.psoftg1.genremanagement.model.mongodb.GenreMongoEntity;
import pt.psoft.g1.psoftg1.shared.model.mongodb.EntityWithPhotoMongoEntity;
import pt.psoft.g1.psoftg1.shared.model.mongodb.PhotoMongoEntity;
import pt.psoft.g1.psoftg1.usermanagement.model.mongodb.ReaderMongoEntity;

import java.util.List;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Profile("mongodb")
@Document(collection = "reader_details")
@Primary
@Getter
@Setter
public class ReaderDetailsMongoEntity extends EntityWithPhotoMongoEntity {

    @Id
    private String readerdetailId;

    @DBRef
    private ReaderMongoEntity reader;

    private ReaderNumberMongoEntity readerNumber;

    private BirthDateMongoEntity birthDate;

    private PhoneNumberMongoEntity phoneNumber;

    private boolean gdprConsent;

    private boolean marketingConsent;

    private boolean thirdPartySharingConsent;

    @Version
    private Long version;

    @DBRef
    private List<GenreMongoEntity> interestList;

    public ReaderDetailsMongoEntity(
            ReaderNumberMongoEntity readerNumber,
            ReaderMongoEntity reader,
            BirthDateMongoEntity birthDate,
            PhoneNumberMongoEntity phoneNumber,
            boolean gdpr,
            boolean marketing,
            boolean thirdParty,
            PhotoMongoEntity photoURI,
            List<GenreMongoEntity> interestList) {

        setReaderNumber(readerNumber);
        setReader(reader);
        setBirthDate(birthDate);
        setPhoneNumber(phoneNumber);
        setGdprConsent(gdpr);
        setMarketingConsent(marketing);
        setThirdPartySharingConsent(thirdParty);
        setPhoto(photoURI);
        setInterestList(interestList);
    }

    protected ReaderDetailsMongoEntity() {
        // for ORM only
    }
}
