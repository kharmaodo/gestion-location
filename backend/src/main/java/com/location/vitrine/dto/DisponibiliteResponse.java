package com.location.vitrine.dto;

import java.time.LocalDate;

public record DisponibiliteResponse(LocalDate debut, LocalDate fin, String statut) {}
