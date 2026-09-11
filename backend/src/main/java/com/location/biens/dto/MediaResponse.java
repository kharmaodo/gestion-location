package com.location.biens.dto;

import java.util.UUID;

public record MediaResponse(UUID id, UUID uniteId, String url, String type, int position) {}
