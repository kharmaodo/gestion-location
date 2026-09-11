package com.location.vitrine.controller;

import com.location.vitrine.dto.AnnonceResponse;
import com.location.vitrine.dto.DisponibiliteResponse;
import com.location.vitrine.service.VitrineService;
import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/public/annonces")
public class VitrineController {
    private final VitrineService vitrineService;

    public VitrineController(VitrineService vitrineService) {
        this.vitrineService = vitrineService;
    }

    @GetMapping
    public List<AnnonceResponse> catalogue(@RequestParam(required = false) String ville) {
        return vitrineService.catalogue(ville);
    }

    @GetMapping("/{uniteId}")
    public AnnonceResponse detail(@PathVariable UUID uniteId) {
        return vitrineService.detail(uniteId);
    }

    @GetMapping("/{uniteId}/disponibilites")
    public List<DisponibiliteResponse> disponibilites(@PathVariable UUID uniteId) {
        return vitrineService.disponibilites(uniteId);
    }
}
