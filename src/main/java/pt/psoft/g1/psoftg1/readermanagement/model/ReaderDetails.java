package pt.psoft.g1.psoftg1.readermanagement.model;

import jakarta.persistence.*;
import pt.psoft.g1.psoftg1.exceptions.ConflictException;
import pt.psoft.g1.psoftg1.genremanagement.model.Genre;
import pt.psoft.g1.psoftg1.readermanagement.services.UpdateReaderRequest;
import pt.psoft.g1.psoftg1.shared.model.EntityWithPhoto;
import pt.psoft.g1.psoftg1.usermanagement.model.Reader;

import java.nio.file.InvalidPathException;
import java.util.List;

public class ReaderDetails extends EntityWithPhoto {

    // Fields
    private Reader reader;
    private ReaderNumber readerNumber;
    private BirthDate birthDate;
    private PhoneNumber phoneNumber;
    private boolean gdprConsent;
    private boolean marketingConsent;
    private boolean thirdPartySharingConsent;
    private Long version;
    private List<Genre> interestList;

    // Constructor
    public ReaderDetails(int readerNumber, Reader reader, String birthDate, String phoneNumber,
                         boolean gdpr, boolean marketing, boolean thirdParty,
                         String photoURI, List<Genre> interestList) {
        validateConstructorArguments(reader, phoneNumber, gdpr);

        setReader(reader);
        setReaderNumber(new ReaderNumber(readerNumber));
        setBirthDate(new BirthDate(birthDate));
        setPhoneNumber(new PhoneNumber(phoneNumber));
        setGdprConsent(gdpr);
        setMarketingConsent(marketing);
        setThirdPartySharingConsent(thirdParty);
        setPhotoInternal(photoURI);
        setInterestList(interestList);
    }

    protected ReaderDetails() {
        // smth here
    }

    // Business Methods
    public void applyPatch(final long currentVersion, final UpdateReaderRequest request,
                           String photoURI, List<Genre> interestList) {
        validateVersion(currentVersion);

        updateReaderInfo(request);
        updatePersonalInfo(request);
        updateConsents(request);
        updatePhoto(photoURI);
        updateInterestList(interestList);
    }

    public void removePhoto(long desiredVersion) {
        validateVersion(desiredVersion);
        setPhotoInternal((String) null);
    }

    // Getters
    public Reader getReader() {
        return reader;
    }

    public String getReaderNumber() {
        return this.readerNumber.toString();
    }

    public BirthDate getBirthDate() { return birthDate; }

    public String getPhoneNumber() {
        return this.phoneNumber.toString();
    }

    public boolean isGdprConsent() {
        return gdprConsent;
    }

    public boolean isMarketingConsent() {
        return marketingConsent;
    }

    public boolean isThirdPartySharingConsent() {
        return thirdPartySharingConsent;
    }

    public Long getVersion() {
        return version;
    }

    public List<Genre> getInterestList() {
        return interestList;
    }

    // Private Setters
    private void setReader(Reader reader) {
        if (reader != null) {
            this.reader = reader;
        }
    }

    private void setReaderNumber(ReaderNumber readerNumber) {
        if (readerNumber != null) {
            this.readerNumber = readerNumber;
        }
    }

    private void setBirthDate(BirthDate birthDate) {
        if (birthDate != null) {
            this.birthDate = birthDate;
        }
    }

    private void setPhoneNumber(PhoneNumber phoneNumber) {
        if (phoneNumber != null) {
            this.phoneNumber = phoneNumber;
        }
    }

    private void setGdprConsent(boolean gdprConsent) {
        this.gdprConsent = gdprConsent;
    }

    private void setMarketingConsent(boolean marketingConsent) {
        this.marketingConsent = marketingConsent;
    }

    private void setThirdPartySharingConsent(boolean thirdPartySharingConsent) {
        this.thirdPartySharingConsent = thirdPartySharingConsent;
    }

    private void setInterestList(List<Genre> interestList) {
        if (interestList != null) {
            this.interestList = interestList;
        }
    }

    // Validation Methods
    private void validateConstructorArguments(Reader reader, String phoneNumber, boolean gdpr) {
        if (reader == null || phoneNumber == null) {
            throw new IllegalArgumentException("Provided argument resolves to null object");
        }

        if (!gdpr) {
            throw new IllegalArgumentException("Readers must agree with the GDPR rules");
        }
    }

    private void validateVersion(long currentVersion) {
        if (currentVersion != this.version) {
            throw new ConflictException("Provided version does not match latest version of this object");
        }
    }

    // Helper Methods for applyPatch
    private void updateReaderInfo(UpdateReaderRequest request) {
        if (request.getUsername() != null) {
            this.reader.setUsername(request.getUsername());
        }

        if (request.getPassword() != null) {
            this.reader.setPassword(request.getPassword());
        }

        if (request.getFullName() != null) {
            this.reader.setName(request.getFullName());
        }
    }

    private void updatePersonalInfo(UpdateReaderRequest request) {
        if (request.getBirthDate() != null) {
            setBirthDate(new BirthDate(request.getBirthDate()));
        }

        if (request.getPhoneNumber() != null) {
            setPhoneNumber(new PhoneNumber(request.getPhoneNumber()));
        }
    }

    private void updateConsents(UpdateReaderRequest request) {
        if (request.getMarketing() != this.marketingConsent) {
            setMarketingConsent(request.getMarketing());
        }

        if (request.getThirdParty() != this.thirdPartySharingConsent) {
            setThirdPartySharingConsent(request.getThirdParty());
        }
    }

    private void updatePhoto(String photoURI) {
        if (photoURI != null) {
            try {
                setPhotoInternal(photoURI);
            } catch (InvalidPathException ignored) {
                // Photo path is invalid, skip update
            }
        }
    }

    private void updateInterestList(List<Genre> interestList) {
        if (interestList != null) {
            this.interestList = interestList;
        }
    }
}