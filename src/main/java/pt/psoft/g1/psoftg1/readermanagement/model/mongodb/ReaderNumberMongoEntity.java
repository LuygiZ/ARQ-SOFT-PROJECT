package pt.psoft.g1.psoftg1.readermanagement.model.mongodb;

import java.io.Serializable;
import java.time.LocalDate;

public class ReaderNumberMongoEntity implements Serializable {
    private final String readerNumber;

    public ReaderNumberMongoEntity(int year, int number) {
        this.readerNumber = year + "/" + number;
    }

    public ReaderNumberMongoEntity(int number) {
        this.readerNumber = LocalDate.now().getYear() + "/" + number;
    }

    // Getter
    public String getReaderNumber() {
        return readerNumber;
    }

    // Helper
    public String toString() {
        return readerNumber;
    }
}
