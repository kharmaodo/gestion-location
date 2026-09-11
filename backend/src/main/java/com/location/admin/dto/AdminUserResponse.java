package com.location.admin.dto;

import java.util.List;
import java.util.UUID;

public record AdminUserResponse(UUID id, String email, String telephone, String prenom, String nom, String statut, List<String> roles) {}
