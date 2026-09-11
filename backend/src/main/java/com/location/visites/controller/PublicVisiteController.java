package com.location.visites.controller;

import com.location.visites.dto.VisiteRequest;
import com.location.visites.dto.VisiteResponse;
import com.location.visites.service.VisiteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/public/visites")
public class PublicVisiteController {
    private final VisiteService service;

    public PublicVisiteController(VisiteService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VisiteResponse demander(@Valid @RequestBody VisiteRequest request) {
        return service.demander(request);
    }
}
