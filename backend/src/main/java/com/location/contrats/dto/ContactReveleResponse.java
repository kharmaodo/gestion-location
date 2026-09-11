package com.location.contrats.dto;

public record ContactReveleResponse(
        boolean revele,
        String motif,
        ContactPartie proprietaire,
        ContactPartie locataire) {
    public record ContactPartie(String nom, String email, String telephone) {}
}
