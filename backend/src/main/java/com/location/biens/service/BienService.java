package com.location.biens.service;

import com.location.biens.dto.BienRequest;
import com.location.biens.dto.BienResponse;
import com.location.biens.dto.PeriodiciteRequest;
import com.location.biens.dto.UniteRequest;
import com.location.biens.dto.UniteResponse;
import com.location.biens.entity.BienImmobilierEntity;
import com.location.biens.entity.UniteLocativeEntity;
import com.location.biens.repository.BienImmobilierRepository;
import com.location.biens.repository.UniteLocativeRepository;
import com.location.shared.exception.ApiException;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BienService {
    private static final Set<String> TYPES_BIEN = Set.of("MAISON", "IMMEUBLE", "RESIDENCE");
    private static final Set<String> TYPES_UNITE = Set.of(
            "MAISON", "APPARTEMENT", "STUDIO", "CHAMBRE_SIMPLE", "CHAMBRE_SDB");
    private static final Set<String> PERIODICITES = Set.of("JOURNALIER", "HEBDOMADAIRE", "MENSUEL");

    private final BienImmobilierRepository biens;
    private final UniteLocativeRepository unites;

    public BienService(BienImmobilierRepository biens, UniteLocativeRepository unites) {
        this.biens = biens;
        this.unites = unites;
    }

    @Transactional(readOnly = true)
    public List<BienResponse> lister(UUID proprietaireId) {
        return biens.findByProprietaireIdOrderByMajLeDesc(proprietaireId).stream()
                .map(b -> toBien(b, false))
                .toList();
    }

    @Transactional(readOnly = true)
    public BienResponse detail(UUID proprietaireId, UUID id) {
        return toBien(owned(proprietaireId, id), true);
    }

    @Transactional
    public BienResponse creer(UUID proprietaireId, BienRequest req) {
        assertType(req.type(), TYPES_BIEN, "type de bien invalide");
        BienImmobilierEntity e = new BienImmobilierEntity();
        e.setId(UUID.randomUUID());
        e.setProprietaireId(proprietaireId);
        apply(e, req);
        biens.save(e);
        return toBien(e, true);
    }

    @Transactional
    public BienResponse modifier(UUID proprietaireId, UUID id, BienRequest req) {
        assertType(req.type(), TYPES_BIEN, "type de bien invalide");
        BienImmobilierEntity e = owned(proprietaireId, id);
        apply(e, req);
        e.setMajLe(Instant.now());
        biens.save(e);
        return toBien(e, true);
    }

    @Transactional
    public void supprimer(UUID proprietaireId, UUID id) {
        BienImmobilierEntity e = owned(proprietaireId, id);
        unites.findByBienIdOrderByLibelleAsc(id).forEach(unites::delete);
        biens.delete(e);
    }

    @Transactional
    public UniteResponse ajouterUnite(UUID proprietaireId, UUID bienId, UniteRequest req) {
        owned(proprietaireId, bienId);
        assertType(req.type(), TYPES_UNITE, "type d'unite invalide");
        UniteLocativeEntity u = new UniteLocativeEntity();
        u.setId(UUID.randomUUID());
        u.setBienId(bienId);
        apply(u, req);
        unites.save(u);
        return toUnite(u);
    }

    @Transactional
    public UniteResponse modifierUnite(UUID proprietaireId, UUID bienId, UUID uniteId, UniteRequest req) {
        owned(proprietaireId, bienId);
        assertType(req.type(), TYPES_UNITE, "type d'unite invalide");
        UniteLocativeEntity u = unites.findByIdAndBienId(uniteId, bienId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Unite introuvable"));
        apply(u, req);
        u.setMajLe(Instant.now());
        unites.save(u);
        return toUnite(u);
    }

    @Transactional
    public UniteResponse periodicite(UUID proprietaireId, UUID bienId, UUID uniteId, PeriodiciteRequest req) {
        owned(proprietaireId, bienId);
        String p = req.periodicite().toUpperCase();
        if (!PERIODICITES.contains(p)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "periodicite invalide");
        }
        UniteLocativeEntity u = unites.findByIdAndBienId(uniteId, bienId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Unite introuvable"));
        if ("OCCUPE".equals(u.getStatut())) {
            throw new ApiException(HttpStatus.CONFLICT, "periodicite verrouillee : contrat actif, passer par un avenant");
        }
        u.setPeriodicite(p);
        u.setJourEcheance(req.jourEcheance());
        u.setMajLe(Instant.now());
        unites.save(u);
        return toUnite(u);
    }

    private void apply(BienImmobilierEntity e, BienRequest req) {
        e.setDesignation(req.designation());
        e.setType(req.type().toUpperCase());
        e.setAdresse(req.adresse());
        e.setVille(req.ville());
        e.setLatitude(req.latitude());
        e.setLongitude(req.longitude());
    }

    private void apply(UniteLocativeEntity u, UniteRequest req) {
        u.setLibelle(req.libelle());
        u.setType(req.type().toUpperCase());
        u.setSurfaceM2(req.surfaceM2());
        u.setMeuble(req.meuble());
        u.setLoyer(req.loyer());
        String p = req.periodicite() == null || req.periodicite().isBlank() ? "MENSUEL" : req.periodicite().toUpperCase();
        if (!PERIODICITES.contains(p)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "periodicite invalide");
        }
        u.setPeriodicite(p);
        u.setJourEcheance(req.jourEcheance());
    }

    private BienImmobilierEntity owned(UUID proprietaireId, UUID id) {
        return biens.findByIdAndProprietaireId(id, proprietaireId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Bien introuvable"));
    }

    private void assertType(String value, Set<String> allowed, String message) {
        if (value == null || !allowed.contains(value.toUpperCase())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, message);
        }
    }

    private BienResponse toBien(BienImmobilierEntity e, boolean withUnites) {
        List<UniteLocativeEntity> list = unites.findByBienIdOrderByLibelleAsc(e.getId());
        long libres = list.stream().filter(u -> "LIBRE".equals(u.getStatut())).count();
        List<UniteResponse> detail = withUnites ? list.stream().map(this::toUnite).toList() : List.of();
        return new BienResponse(
                e.getId(), e.getDesignation(), e.getType(), e.getAdresse(), e.getVille(),
                e.getLatitude(), e.getLongitude(), e.getStatut(), list.size(), libres, detail);
    }

    private UniteResponse toUnite(UniteLocativeEntity u) {
        return new UniteResponse(
                u.getId(), u.getBienId(), u.getLibelle(), u.getType(), u.getSurfaceM2(), u.isMeuble(),
                u.getLoyer(), u.getDevise(), u.getPeriodicite(), u.getJourEcheance(), u.getStatut(), u.isPublie());
    }
}
