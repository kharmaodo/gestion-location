package com.location.identites.dto;

public record TwoFactorSetupResponse(String secret, String otpauthUrl) {}
