package com.location.shared.security;

import java.nio.ByteBuffer;
import java.security.SecureRandom;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.stereotype.Service;

@Service
public class TotpService {
    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";
    private final SecureRandom random = new SecureRandom();

    public String generateSecret() {
        byte[] buf = new byte[20];
        random.nextBytes(buf);
        return base32(buf);
    }

    public String otpauthUrl(String issuer, String account, String secret) {
        return "otpauth://totp/" + issuer + ":" + account + "?secret=" + secret + "&issuer=" + issuer + "&digits=6&period=30";
    }

    public boolean verify(String secret, String code) {
        if (secret == null || code == null || !code.matches("\\d{6}")) {
            return false;
        }
        long counter = System.currentTimeMillis() / 1000L / 30L;
        for (int i = -1; i <= 1; i++) {
            if (code.equals(generate(secret, counter + i))) {
                return true;
            }
        }
        return false;
    }

    private String generate(String secret, long counter) {
        try {
            byte[] key = fromBase32(secret);
            byte[] data = ByteBuffer.allocate(8).putLong(counter).array();
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(new SecretKeySpec(key, "HmacSHA1"));
            byte[] hash = mac.doFinal(data);
            int offset = hash[hash.length - 1] & 0x0f;
            int binary = ((hash[offset] & 0x7f) << 24)
                    | ((hash[offset + 1] & 0xff) << 16)
                    | ((hash[offset + 2] & 0xff) << 8)
                    | (hash[offset + 3] & 0xff);
            return String.format("%06d", binary % 1_000_000);
        } catch (Exception e) {
            return "";
        }
    }

    private static String base32(byte[] data) {
        StringBuilder sb = new StringBuilder();
        int buffer = 0;
        int bitsLeft = 0;
        for (byte b : data) {
            buffer = (buffer << 8) | (b & 0xff);
            bitsLeft += 8;
            while (bitsLeft >= 5) {
                sb.append(ALPHABET.charAt((buffer >> (bitsLeft - 5)) & 31));
                bitsLeft -= 5;
            }
        }
        if (bitsLeft > 0) {
            sb.append(ALPHABET.charAt((buffer << (5 - bitsLeft)) & 31));
        }
        return sb.toString();
    }

    private static byte[] fromBase32(String encoded) {
        String s = encoded.toUpperCase().replace("=", "");
        int buffer = 0;
        int bitsLeft = 0;
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        for (char c : s.toCharArray()) {
            int val = ALPHABET.indexOf(c);
            if (val < 0) continue;
            buffer = (buffer << 5) | val;
            bitsLeft += 5;
            if (bitsLeft >= 8) {
                out.write((buffer >> (bitsLeft - 8)) & 0xff);
                bitsLeft -= 8;
            }
        }
        return out.toByteArray();
    }
}
