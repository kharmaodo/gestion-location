package com.location.canaux;

import com.location.shared.exception.ApiException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class CanalMockService {
    private final String sms;
    private final String fcm;
    private final String psp;
    private final String signature;
    private final List<Map<String, String>> journal = new ArrayList<>();

    public CanalMockService(String sms, String fcm, String psp) {
        this(sms, fcm, psp, "MOCK");
    }

    @Autowired
    public CanalMockService(
            @Value("${app.channels.sms:MOCK}") String sms,
            @Value("${app.channels.fcm:MOCK}") String fcm,
            @Value("${app.channels.psp:MOCK}") String psp,
            @Value("${app.channels.signature:MOCK}") String signature) {
        this.sms = sms.toUpperCase();
        this.fcm = fcm.toUpperCase();
        this.psp = psp.toUpperCase();
        this.signature = signature.toUpperCase();
    }

    public Map<String, Object> statut() {
        return Map.of(
                "sms", sms,
                "fcm", fcm,
                "psp", psp,
                "signature", signature,
                "journal", List.copyOf(journal));
    }

    public Map<String, String> sms(String telephone, String message) {
        return envoyer("sms", sms, telephone, message);
    }

    public Map<String, String> push(String token, String message) {
        return envoyer("fcm", fcm, token, message);
    }

    public Map<String, String> psp(String fournisseur, String message) {
        return envoyer("psp", psp, fournisseur, message);
    }

    private Map<String, String> envoyer(String canal, String flag, String cible, String message) {
        if ("OFF".equals(flag)) {
            throw new ApiException(HttpStatus.CONFLICT, canal + " desactive");
        }
        if ("LIVE".equals(flag)) {
            throw new ApiException(HttpStatus.NOT_IMPLEMENTED, canal + " live non branche");
        }
        Map<String, String> row = Map.of(
                "canal", canal,
                "mode", "MOCK",
                "cible", cible == null ? "" : cible,
                "message", message == null ? "" : message,
                "at", Instant.now().toString());
        journal.add(row);
        return row;
    }
}
