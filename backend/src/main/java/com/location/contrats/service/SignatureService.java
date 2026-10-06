    public SignatureResponse signer(String rawToken) {
        mode.reference();
        SignatureEntity e = signatures.findByTokenHash(sha256(rawToken))
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Lien de signature invalide"));
        if (!"EN_ATTENTE".equals(e.getStatut())) {
            throw new ApiException(HttpStatus.CONFLICT, "deja signe");
        }
        e.setStatut("SIGNE");
        e.setSigneLe(Instant.now());
        signatures.save(e);
        return toDto(e, null);
    }
