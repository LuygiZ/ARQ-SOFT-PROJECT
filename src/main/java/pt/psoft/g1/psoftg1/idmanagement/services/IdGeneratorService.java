package pt.psoft.g1.psoftg1.idmanagement.services;

public interface IdGeneratorService {
    String generateId();
    String generateIdWithPrefix(String prefix);
}