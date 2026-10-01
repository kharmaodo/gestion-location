package com.location.dashboard.controller;

import com.location.dashboard.dto.DashboardResponse;
import com.location.dashboard.dto.SerieMois;
import com.location.dashboard.service.DashboardService;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dashboard")
@PreAuthorize("hasRole('PROPRIETAIRE')")
public class DashboardController {
    private final DashboardService service;

    public DashboardController(DashboardService service) {
        this.service = service;
    }

    @GetMapping
    public DashboardResponse resume(Authentication auth) {
        return service.resume(UUID.fromString(auth.getName()));
    }

    @GetMapping("/series")
    public List<SerieMois> series(Authentication auth) {
        return service.series(UUID.fromString(auth.getName()));
    }

    @GetMapping(value = "/series/export", produces = "text/plain")
    public ResponseEntity<String> seriesCsv(Authentication auth) {
        StringBuilder csv = new StringBuilder("mois,du,encaisse,aRecouvrer\n");
        for (SerieMois s : service.series(UUID.fromString(auth.getName()))) {
            csv.append(s.mois()).append(',').append(s.du()).append(',').append(s.encaisse()).append(',').append(s.aRecouvrer()).append('\n');
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=series.csv")
                .contentType(MediaType.TEXT_PLAIN)
                .body(csv.toString());
    }
}
