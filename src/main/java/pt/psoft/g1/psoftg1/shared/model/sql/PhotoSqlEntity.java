package pt.psoft.g1.psoftg1.shared.model.sql;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Profile("sql")
@Primary
@Entity
public class PhotoSqlEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long pk;

    @NotNull
    @Getter
    @Setter
    private String photoFile;

    protected PhotoSqlEntity() { }

    public PhotoSqlEntity(String photoFile)
    {
        setPhotoFile(photoFile);
    }
}
