    @Transactional
    public List<ReservationResponse> listerProprio(UUID proprietaireId) {
        Instant maintenant = Instant.now();
        return reservations.findByProprietaireIdOrderByCreeLeDesc(proprietaireId).stream().map(e -> {
            String visible = VerrouReservation.statutVisible(e.getStatut(), e.getCreeLe(), maintenant);
            if (!visible.equals(e.getStatut())) {
                e.setStatut(visible);
                e.setMajLe(maintenant);
                reservations.save(e);
            }
            return toDto(e);
        }).toList();
    }
