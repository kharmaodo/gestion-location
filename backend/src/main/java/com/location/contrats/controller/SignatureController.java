package com.location.contrats.controller;

import com.location.contrats.dto.SignatureInviteRequest;
import com.location.contrats.dto.SignatureResponse;
import com.location.contrats.service.SignatureService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SignatureController {
    private final SignatureService service;

    public SignatureController(SignatureService service) {
        this.service = service;
    }

    @PostMapping("/api/v1/signatures")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('PROPRIETAIRE')")
    public SignatureResponse inviter(Authentication auth, @Valid @RequestBody SignatureInviteRequest request) {
        return service.inviter(UUID.fromString(auth.getName()), request);
    }

    @GetMapping("/api/v1/signatures")
    @PreAuthorize("hasRole('PROPRIETAIRE')")
    public List<SignatureResponse> lister(Authentication auth, @RequestParam UUID contratId) {
        return service.lister(UUID.fromString(auth.getName()), contratId);
    }

    @PostMapping("/api/v1/public/signatures/{token}")
    public SignatureResponse signer(@PathVariable String token) {
        return service.signer(token);
    }
}
