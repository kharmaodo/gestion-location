package com.location.identites.dto;

import java.util.List;
import java.util.UUID;

public record MeResponse(UUID id, String email, String telephone, String prenom, String nom, List<String> roles) {}
