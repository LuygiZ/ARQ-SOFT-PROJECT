package pt.psoft.g1.psoftg1.idmanagement.services.impl;

import org.springframework.stereotype.Service;
import pt.psoft.g1.psoftg1.idmanagement.services.IdGeneratorService;

import java.security.SecureRandom;

/**
 * Generates IDs using Base65 encoding of random numbers
 * Format: {base65-random}{6-digit-hex-suffix}
 * Example: A7bK2pQ3d4f5
 */
@Service("base65RandomIdGenerator")
public class Base65RandomIdGenerator implements IdGeneratorService {

    private static final String BASE65_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz-_.";
    private static final int BASE = 65;
    private static final SecureRandom random = new SecureRandom();

    @Override
    public String generateId() {
        // Gerar número aleatório
        long randomNumber = Math.abs(random.nextLong());

        // Converter para Base65
        String base65Part = toBase65(randomNumber);

        // Gerar sufixo hexadecimal de 6 dígitos
        String hexSuffix = generateHexSuffix();

        return base65Part + hexSuffix;
    }

    @Override
    public String generateIdWithPrefix(String prefix) {
        return prefix + "-" + generateId();
    }

    /**
     * Converte um número para Base65
     */
    private String toBase65(long number) {
        if (number == 0) {
            return String.valueOf(BASE65_CHARS.charAt(0));
        }

        StringBuilder result = new StringBuilder();
        while (number > 0) {
            int remainder = (int) (number % BASE);
            result.insert(0, BASE65_CHARS.charAt(remainder));
            number /= BASE;
        }

        return result.toString();
    }

    /**
     * Gera sufixo hexadecimal de 6 dígitos
     */
    private String generateHexSuffix() {
        int randomHex = random.nextInt(0xFFFFFF); // 6 dígitos hex (16^6)
        return String.format("%06X", randomHex);
    }

    /**
     * Converte Base65 de volta para número (para validação)
     */
    public long fromBase65(String base65String) {
        long result = 0;
        for (char c : base65String.toCharArray()) {
            int digitValue = BASE65_CHARS.indexOf(c);
            if (digitValue == -1) {
                throw new IllegalArgumentException("Invalid Base65 character: " + c);
            }
            result = result * BASE + digitValue;
        }
        return result;
    }
}