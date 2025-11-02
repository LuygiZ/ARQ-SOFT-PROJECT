package pt.psoft.g1.psoftg1.idmanagement.factory;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pt.psoft.g1.psoftg1.idmanagement.services.IdGeneratorService;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class IdGeneratorFactory {

    private final Map<String, IdGeneratorService> generators;

    /**
     * Retorna o gerador de IDs baseado na estratégia
     * @param strategy "base65Random", "timestampBase65", "hexSuffix"
     */
    public IdGeneratorService getGenerator(String strategy) {
        IdGeneratorService generator = generators.get(strategy + "IdGenerator");
        if (generator == null) {
            throw new IllegalArgumentException("Unknown ID generation strategy: " + strategy);
        }
        return generator;
    }

    /**
     * Retorna o gerador padrão (Base65 Random)
     */
    public IdGeneratorService getDefaultGenerator() {
        return generators.get("base65RandomIdGenerator");
    }
}