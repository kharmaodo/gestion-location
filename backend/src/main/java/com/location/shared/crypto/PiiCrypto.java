package com.location.shared.crypto;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PiiCrypto {
    private static final String PREFIX = "enc:";
    private static volatile byte[] KEY;
    private final SecureRandom random = new SecureRandom();

    public PiiCrypto(@Value("${app.pii.key:AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=}") String keyB64) {
        byte[] decoded = Base64.getDecoder().decode(keyB64);
        if (decoded.length != 32) {
            throw new IllegalStateException("app.pii.key doit être 32 octets en Base64");
        }
        KEY = decoded;
    }

    public static String seal(String plain) {
        if (plain == null || plain.isBlank() || plain.startsWith(PREFIX)) {
            return plain;
        }
        try {
            byte[] iv = new byte[12];
            new SecureRandom().nextBytes(iv);
            Cipher c = Cipher.getInstance("AES/GCM/NoPadding");
            c.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(KEY, "AES"), new GCMParameterSpec(128, iv));
            byte[] cipher = c.doFinal(plain.getBytes(StandardCharsets.UTF_8));
            ByteBuffer buf = ByteBuffer.allocate(iv.length + cipher.length);
            buf.put(iv).put(cipher);
            return PREFIX + Base64.getEncoder().encodeToString(buf.array());
        } catch (Exception e) {
            throw new IllegalStateException("chiffrement PII impossible", e);
        }
    }

    public static String open(String stored) {
        if (stored == null || !stored.startsWith(PREFIX)) {
            return stored;
        }
        try {
            byte[] raw = Base64.getDecoder().decode(stored.substring(PREFIX.length()));
            ByteBuffer buf = ByteBuffer.wrap(raw);
            byte[] iv = new byte[12];
            buf.get(iv);
            byte[] cipher = new byte[buf.remaining()];
            buf.get(cipher);
            Cipher c = Cipher.getInstance("AES/GCM/NoPadding");
            c.init(Cipher.DECRYPT_MODE, new SecretKeySpec(KEY, "AES"), new GCMParameterSpec(128, iv));
            return new String(c.doFinal(cipher), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalStateException("dechiffrement PII impossible", e);
        }
    }
}
