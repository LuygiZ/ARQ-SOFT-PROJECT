package pt.psoft.g1.psoftg1.idmanagement.model;

import java.util.Objects;

public class GeneratedId {
    private final String value;

    public GeneratedId(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Generated ID cannot be null or empty");
        }
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GeneratedId that = (GeneratedId) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}