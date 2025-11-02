package pt.psoft.g1.psoftg1.idmanagement.services.impl;

import org.springframework.stereotype.Service;
import pt.psoft.g1.psoftg1.idmanagement.services.IdGeneratorService;

import java.security.SecureRandom;
import java.time.Instant;

/**
 * Generates IDs using Base65 encoding of timestamp
 * Format: {base65-timestamp}{6-digit-hex-suffix}
 * Example: T7bK2pQ3A1B2C3
 */
@Service("timestampBase65IdGenerator")
public class TimestampBase65IdGenerator implements IdGeneratorService {

    private static final String BASE65_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz-_.";
    private static final int BASE = 65;
    private static final SecureRandom random = new SecureRandom();

    @Override
    public String generateId() {
        // Usar timestamp atual
        long timestamp = Instant.now().toEpochMilli();

        // Converter para Base65
        String base65Part = toBase65(timestamp);

        // Gerar sufixo hexadecimal de 6 dígitos
        String hexSuffix = generateHexSuffix();

        return base65Part + hexSuffix;
    }

    @Override
    public String generateIdWithPrefix(String prefix) {
        return prefix + "-" + generateId();
    }

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

    private String generateHexSuffix() {
        int randomHex = random.nextInt(0xFFFFFF);
        return String.format("%06X", randomHex);
    }
}