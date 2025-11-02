package pt.psoft.g1.psoftg1.idmanagement.services.impl;

import org.springframework.stereotype.Service;
import pt.psoft.g1.psoftg1.idmanagement.services.IdGeneratorService;

import java.security.SecureRandom;
import java.util.UUID;

/**
 * Generates IDs using hexadecimal suffix
 * Format: {uuid-prefix}{6-digit-hex-suffix}
 * Example: a1b2c3d4-e5f6-A1B2C3
 *
 * Alternative format: {timestamp-hex}{6-digit-hex-suffix}
 * Example: 18A3F2B4C5D6-A1B2C3
 */
@Service("hexSuffixIdGenerator")
public class HexSuffixIdGenerator implements IdGeneratorService {

    private static final SecureRandom random = new SecureRandom();

    @Override
    public String generateId() {
        // Gerar parte principal (timestamp em hex)
        long timestamp = System.currentTimeMillis();
        String hexPrefix = Long.toHexString(timestamp).toUpperCase();

        // Gerar sufixo hexadecimal de 6 dígitos
        String hexSuffix = generateHexSuffix();

        return hexPrefix + "-" + hexSuffix;
    }

    @Override
    public String generateIdWithPrefix(String prefix) {
        return prefix + "-" + generateId();
    }

    /**
     * Gera ID usando UUID + sufixo hex
     */
    public String generateUuidBasedId() {
        // Pegar primeiros 8 caracteres do UUID
        String uuidPrefix = UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 8)
                .toUpperCase();

        // Gerar sufixo hexadecimal de 6 dígitos
        String hexSuffix = generateHexSuffix();

        return uuidPrefix + "-" + hexSuffix;
    }

    /**
     * Gera ID compacto: apenas hex
     * Format: 14 caracteres hexadecimais
     * Example: A1B2C3D4E5F6G7
     */
    public String generateCompactHexId() {
        // 8 caracteres do timestamp + 6 do sufixo = 14 total
        long timestamp = System.currentTimeMillis();
        String hexPrefix = String.format("%08X", timestamp & 0xFFFFFFFFL); // 8 dígitos
        String hexSuffix = generateHexSuffix(); // 6 dígitos

        return hexPrefix + hexSuffix;
    }

    /**
     * Gera sufixo hexadecimal de 6 dígitos
     */
    private String generateHexSuffix() {
        int randomHex = random.nextInt(0xFFFFFF); // 16777215 em decimal
        return String.format("%06X", randomHex);
    }

    /**
     * Valida se uma string é um ID hexadecimal válido
     */
    public boolean isValidHexId(String id) {
        if (id == null || id.isEmpty()) {
            return false;
        }

        // Remove hífens para validação
        String cleanId = id.replace("-", "");

        // Verifica se todos os caracteres são hexadecimais
        return cleanId.matches("[0-9A-Fa-f]+");
    }

    /**
     * Extrai o timestamp de um ID (se foi gerado com timestamp)
     */
    public long extractTimestamp(String id) {
        if (id == null || !id.contains("-")) {
            throw new IllegalArgumentException("Invalid ID format");
        }

        String hexPrefix = id.split("-")[0];
        return Long.parseLong(hexPrefix, 16);
    }
}