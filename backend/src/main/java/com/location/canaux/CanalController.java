package com.location.canaux;

import java.util.Map;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/canaux")
@PreAuthorize("isAuthenticated()")
public class CanalController {
    private final CanalMockService service;

    public CanalController(CanalMockService service) {
        this.service = service;
    }

    @GetMapping
    public Map<String, Object> statut() {
        return service.statut();
    }

    @PostMapping("/sms")
    public Map<String, String> sms(@RequestBody Map<String, String> body) {
        return service.sms(body.get("telephone"), body.get("message"));
    }

    @PostMapping("/push")
    public Map<String, String> push(@RequestBody Map<String, String> body) {
        return service.push(body.get("token"), body.get("message"));
    }

    @PostMapping("/psp")
    public Map<String, String> psp(@RequestBody Map<String, String> body) {
        return service.psp(body.get("fournisseur"), body.get("message"));
    }
}
