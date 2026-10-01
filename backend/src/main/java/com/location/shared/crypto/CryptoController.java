package com.location.shared.crypto;

import java.util.Map;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/crypto")
@PreAuthorize("isAuthenticated()")
public class CryptoController {
    private final FieldCrypto crypto;

    public CryptoController(FieldCrypto crypto) {
        this.crypto = crypto;
    }

    @PostMapping("/roundtrip")
    public Map<String, String> roundtrip(@RequestBody Map<String, String> body) {
        String plain = body.getOrDefault("plain", "");
        String stored = crypto.encrypt(plain);
        return Map.of("stored", stored, "plain", crypto.decrypt(stored), "algo", "AES-GCM");
    }
}
