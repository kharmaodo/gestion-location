package com.location.shared.crypto;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FieldCrypto {
    private static final int GCM_TAG = 128;
    private final byte[] key;
    private final SecureRandom random = new SecureRandom();

    public FieldCrypto(@Value("${app.crypto.key:dev-only-field-key-32b!!}") String secret) {
        byte[] raw = secret.getBytes(StandardCharsets.UTF_8);
        byte[] k = new byte[32];
        System.arraycopy(raw, 0, k, 0, Math.min(raw.length, 32));
        this.key = k;
    }

    public String encrypt(String plain) {
        if (plain == null || plain.isBlank()) {
            return plain;
        }
        try {
            byte[] iv = new byte[12];
            random.nextBytes(iv);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"), new GCMParameterSpec(GCM_TAG, iv));
            byte[] enc = cipher.doFinal(plain.getBytes(StandardCharsets.UTF_8));
            return "enc:" + Base64.getEncoder().encodeToString(iv) + ":" + Base64.getEncoder().encodeToString(enc);
        } catch (Exception e) {
            throw new IllegalStateException("chiffrement impossible", e);
        }
    }

    public String decrypt(String stored) {
        if (stored == null || !stored.startsWith("enc:")) {
            return stored;
        }
        try {
            String[] parts = stored.split(":", 3);
            byte[] iv = Base64.getDecoder().decode(parts[1]);
            byte[] enc = Base64.getDecoder().decode(parts[2]);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"), new GCMParameterSpec(GCM_TAG, iv));
            return new String(cipher.doFinal(enc), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalStateException("dechiffrement impossible", e);
        }
    }
}
