package pt.psoft.g1.psoftg1.shared.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.springframework.context.annotation.PropertySource;

public class Name
{
    private String name;

    public Name(String name)
    {
        setName(name);
    }

    protected Name()
    {
        // smth here
    }

    // Setter
    public void setName(String name)
    {
        if (name == null)
        {
            throw new IllegalArgumentException("Name cannot be null");
        }
        if (name.isBlank())
        {
            throw new IllegalArgumentException("Name cannot be blank or only white spaces");
        }
        if (!StringUtilsCustom.isAlphanumeric(name))
        {
            throw new IllegalArgumentException("Name can only contain alphanumeric characters");
        }

        this.name = name;
    }

    // Getter
    public String getName() { return this.name; }

    // Helper
    public String toString() { return this.name; }
}

