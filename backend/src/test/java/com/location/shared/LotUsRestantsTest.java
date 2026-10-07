package com.location.shared;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.location.biens.dto.BienRequest;
import com.location.biens.repository.BienImmobilierRepository;
import com.location.biens.repository.UniteLocativeRepository;
import com.location.biens.service.BienService;
import com.location.biens.service.MediaService;
import com.location.contrats.entity.ContratEntity;
import com.location.contrats.entity.EtatLieuxEntity;
import com.location.contrats.repository.ContratRepository;
import com.location.contrats.repository.EtatLieuxRepository;
import com.location.contrats.service.RestitutionCautionService;
import com.location.dashboard.controller.DashboardController;
import com.location.dashboard.dto.SerieMois;
import com.location.dashboard.service.DashboardService;
import com.location.notifications.service.NotificationService;
import com.location.paiements.dto.PaiementRequest;
import com.location.paiements.entity.EcheanceEntity;
import com.location.paiements.repository.EcheanceRepository;
import com.location.paiements.repository.PaiementRepository;
import com.location.paiements.service.PaiementService;
import com.location.shared.exception.ApiException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;

class LotUsRestantsTest {
    @Test
    void us02TypeDeBienInvalide() {
        BienService service = new BienService(mock(BienImmobilierRepository.class), mock(UniteLocativeRepository.class), mock(MediaService.class));
        ApiException ex = assertThrows(ApiException.class, () -> service.creer(
                UUID.randomUUID(), new BienRequest("Villa", "TERRAIN", null, "Dakar", null, null)));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
    }

    @Test
    void us04ExportCsvASixMois() {
        DashboardService service = mock(DashboardService.class);
        DashboardController controller = new DashboardController(service);
        Authentication auth = mock(Authentication.class);
        UUID proprio = UUID.randomUUID();
        when(auth.getName()).thenReturn(proprio.toString());
        when(service.series(proprio)).thenReturn(List.of(new SerieMois("2026-10", new BigDecimal("100"), new BigDecimal("40"), new BigDecimal("60"))));
        String csv = controller.seriesCsv(auth).getBody();
        assertTrue(csv.startsWith("mois,du,encaisse,aRecouvrer"));
        assertTrue(csv.contains("2026-10,100,40,60"));
    }

    @Test
    void us13PaiementPartielPasseLeStatutAPartiel() {
        EcheanceRepository echeances = mock(EcheanceRepository.class);
        PaiementRepository paiements = mock(PaiementRepository.class);
        PaiementService service = new PaiementService(mock(ContratRepository.class), echeances, paiements, mock(NotificationService.class));
        UUID proprio = UUID.randomUUID();
        UUID echeanceId = UUID.randomUUID();
        EcheanceEntity e = new EcheanceEntity();
        e.setId(echeanceId);
        e.setMontant(new BigDecimal("10000"));
        e.setStatut("A_PAYER");
        when(echeances.findByIdAndProprietaireId(echeanceId, proprio)).thenReturn(Optional.of(e));
        when(paiements.findByEcheanceIdOrderByPayeLeDesc(echeanceId)).thenReturn(List.of());
        var reponse = service.encaisser(proprio, echeanceId, new PaiementRequest(new BigDecimal("4000"), "ESPECES", null));
        assertEquals("PARTIEL", reponse.statut());
    }

    @Test
    void us25QuittanceEmiseMemeSurUnPaiementPartiel() {
        EcheanceRepository echeances = mock(EcheanceRepository.class);
        PaiementRepository paiements = mock(PaiementRepository.class);
        NotificationService notifications = mock(NotificationService.class);
        PaiementService service = new PaiementService(mock(ContratRepository.class), echeances, paiements, notifications);
        UUID proprio = UUID.randomUUID();
        UUID echeanceId = UUID.randomUUID();
        EcheanceEntity e = new EcheanceEntity();
        e.setId(echeanceId);
        e.setMontant(new BigDecimal("10000"));
        when(echeances.findByIdAndProprietaireId(echeanceId, proprio)).thenReturn(Optional.of(e));
        when(paiements.findByEcheanceIdOrderByPayeLeDesc(any())).thenReturn(List.of());
        service.encaisser(proprio, echeanceId, new PaiementRequest(new BigDecimal("1000"), "WAVE", "ref"));
        org.mockito.Mockito.verify(notifications).notifier(org.mockito.ArgumentMatchers.eq(proprio), org.mockito.ArgumentMatchers.eq("PAIEMENT"), org.mockito.ArgumentMatchers.contains("quittance"));
    }

    @Test
    void us27CautionNonRestitueeSansEdlSortieValide() {
        ContratRepository contrats = mock(ContratRepository.class);
        EtatLieuxRepository etats = mock(EtatLieuxRepository.class);
        RestitutionCautionService service = new RestitutionCautionService(contrats, etats);
        UUID proprio = UUID.randomUUID();
        UUID contratId = UUID.randomUUID();
        ContratEntity c = new ContratEntity();
        c.setId(contratId);
        c.setCaution(new BigDecimal("255000"));
        EtatLieuxEntity sortie = new EtatLieuxEntity();
        sortie.setStatut("BROUILLON");
        sortie.setCoutReparations(new BigDecimal("10000"));
        when(contrats.findByIdAndProprietaireId(contratId, proprio)).thenReturn(Optional.of(c));
        when(etats.findByContratIdAndType(contratId, "SORTIE")).thenReturn(Optional.of(sortie));
        var calcul = service.calculer(proprio, contratId);
        assertFalse(calcul.edlSortieValide());
        assertEquals(BigDecimal.ZERO, calcul.montantRetenu());
    }
}
