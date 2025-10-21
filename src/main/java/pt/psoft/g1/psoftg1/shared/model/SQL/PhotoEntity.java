package pt.psoft.g1.psoftg1.shared.model.SQL;

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
public class PhotoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long pk;

    @NotNull
    @Getter
    @Setter
    private String photoFile;

    protected PhotoEntity() { }

    public PhotoEntity(String photoFile)
    {
        setPhotoFile(photoFile);
    }
}
