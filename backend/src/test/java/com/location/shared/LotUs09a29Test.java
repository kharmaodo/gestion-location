package com.location.shared;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.location.avis.dto.AvisRequest;
import com.location.avis.entity.AvisEntity;
import com.location.avis.repository.AvisRepository;
import com.location.avis.service.AvisService;
import com.location.biens.dto.MediaRequest;
import com.location.biens.entity.BienImmobilierEntity;
import com.location.biens.entity.UniteLocativeEntity;
import com.location.biens.repository.BienImmobilierRepository;
import com.location.biens.repository.MediaRepository;
import com.location.biens.repository.UniteLocativeRepository;
import com.location.biens.service.MediaService;
import com.location.contrats.dto.EtatLieuxRequest;
import com.location.contrats.dto.ResiliationRequest;
import com.location.contrats.entity.ContratEntity;
import com.location.contrats.repository.AvenantRepository;
import com.location.contrats.repository.ContratRepository;
import com.location.contrats.repository.EtatLieuxRepository;
import com.location.contrats.repository.SignatureRepository;
import com.location.contrats.service.CautionService;
import com.location.contrats.service.CertificatService;
import com.location.contrats.service.ContactReveleService;
import com.location.contrats.service.ContratService;
import com.location.contrats.service.EtatLieuxService;
import com.location.dashboard.service.AlerteService;
import com.location.identites.repository.UtilisateurRepository;
import com.location.litiges.dto.LitigeDecisionRequest;
import com.location.litiges.repository.LitigeRepository;
import com.location.litiges.service.LitigeService;
import com.location.locataires.repository.DossierRepository;
import com.location.messagerie.entity.ConversationEntity;
import com.location.messagerie.repository.ConversationRepository;
import com.location.messagerie.repository.MessageRepository;
import com.location.messagerie.service.MessageBus;
import com.location.messagerie.service.MessagerieService;
import com.location.notifications.repository.NotificationRepository;
import com.location.notifications.service.NotificationHub;
import com.location.notifications.service.NotificationService;
import com.location.paiements.entity.EcheanceEntity;
import com.location.paiements.repository.EcheanceRepository;
import com.location.paiements.repository.IntentionPaiementRepository;
import com.location.paiements.repository.RelanceRepository;
import com.location.paiements.service.PaiementEnLigneService;
import com.location.paiements.service.PaiementService;
import com.location.paiements.service.RelanceService;
import com.location.reservations.repository.ReservationRepository;
import com.location.shared.exception.ApiException;
import com.location.shared.mail.RelanceMailer;
import com.location.shared.storage.MinioStorageService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class LotUs09a29Test {
    @Test
    void us09TypeMediaInvalide() {
        UniteLocativeRepository unites = mock(UniteLocativeRepository.class);
        BienImmobilierRepository biens = mock(BienImmobilierRepository.class);
        MediaService service = new MediaService(mock(MediaRepository.class), unites, biens, mock(MinioStorageService.class));
        UUID proprio = UUID.randomUUID();
        UUID uniteId = UUID.randomUUID();
        UUID bienId = UUID.randomUUID();
        UniteLocativeEntity u = new UniteLocativeEntity();
        u.setId(uniteId);
        u.setBienId(bienId);
        BienImmobilierEntity b = new BienImmobilierEntity();
        b.setProprietaireId(proprio);
        when(unites.findById(uniteId)).thenReturn(Optional.of(u));
        when(biens.findById(bienId)).thenReturn(Optional.of(b));
        ApiException ex = assertThrows(ApiException.class, () -> service.ajouter(proprio, uniteId, new MediaRequest("https://example.com/a.jpg", "AUDIO")));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
    }

    @Test
    void us10PasDeDoublonEntree() {
        EtatLieuxRepository etats = mock(EtatLieuxRepository.class);
        ContratRepository contrats = mock(ContratRepository.class);
        EtatLieuxService service = new EtatLieuxService(etats, contrats);
        UUID proprio = UUID.randomUUID();
        UUID contratId = UUID.randomUUID();
        ContratEntity c = new ContratEntity();
        c.setId(contratId);
        when(contrats.findByIdAndProprietaireId(contratId, proprio)).thenReturn(Optional.of(c));
        when(etats.existsByContratIdAndType(contratId, "ENTREE")).thenReturn(true);
        ApiException ex = assertThrows(ApiException.class, () -> service.creer(proprio, new EtatLieuxRequest(contratId, "ENTREE", "ok", BigDecimal.ZERO)));
        assertEquals(HttpStatus.CONFLICT, ex.getStatus());
    }

    @Test
    void us11DecisionLitigeInvalide() {
        LitigeService service = new LitigeService(mock(LitigeRepository.class), mock(ContratRepository.class), mock(DossierRepository.class));
        ApiException ex = assertThrows(ApiException.class, () -> service.decider(UUID.randomUUID(), UUID.randomUUID(), new LitigeDecisionRequest("FERME", null)));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
    }

    @Test
    void us13Et16PartielResteUneAlerteEtPayeeDisparait() {
        EcheanceRepository echeances = mock(EcheanceRepository.class);
        AlerteService alertes = new AlerteService(echeances);
        UUID proprio = UUID.randomUUID();
        EcheanceEntity partiel = new EcheanceEntity();
        partiel.setId(UUID.randomUUID());
        partiel.setStatut("PARTIEL");
        partiel.setPeriodeFin(LocalDate.now().plusDays(2));
        partiel.setMontant(new BigDecimal("10000"));
        EcheanceEntity payee = new EcheanceEntity();
        payee.setStatut("PAYEE");
        when(echeances.findByProprietaireIdOrderByPeriodeDebutDesc(proprio)).thenReturn(List.of(partiel, payee));
        assertEquals(1, alertes.lister(proprio).size());
        assertEquals("LOYER_A_PAYER", alertes.lister(proprio).get(0).type());
    }

    @Test
    void us14RelanceIgnoreUneEcheancePayee() {
        EcheanceRepository echeances = mock(EcheanceRepository.class);
        RelanceService service = new RelanceService(echeances, mock(RelanceRepository.class), mock(ContratRepository.class), mock(DossierRepository.class), mock(RelanceMailer.class));
        UUID proprio = UUID.randomUUID();
        EcheanceEntity payee = new EcheanceEntity();
        payee.setStatut("PAYEE");
        when(echeances.findByProprietaireIdOrderByPeriodeDebutDesc(proprio)).thenReturn(List.of(payee));
        assertTrue(service.declencher(proprio).isEmpty());
    }

    @Test
    void us15MessageReserveAuxParticipants() {
        ConversationRepository conversations = mock(ConversationRepository.class);
        MessagerieService service = new MessagerieService(conversations, mock(MessageRepository.class), mock(UtilisateurRepository.class), mock(MessageBus.class));
        UUID id = UUID.randomUUID();
        ConversationEntity c = new ConversationEntity();
        c.setParticipantA(UUID.randomUUID());
        c.setParticipantB(UUID.randomUUID());
        when(conversations.findById(id)).thenReturn(Optional.of(c));
        ApiException ex = assertThrows(ApiException.class, () -> service.ecrire(UUID.randomUUID(), id, "bonjour"));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
    }

    @Test
    void us18EcheanceDejaPayeeRefuseUneIntention() {
        EcheanceRepository echeances = mock(EcheanceRepository.class);
        PaiementEnLigneService service = new PaiementEnLigneService(echeances, mock(IntentionPaiementRepository.class), mock(PaiementService.class));
        UUID proprio = UUID.randomUUID();
        UUID echeanceId = UUID.randomUUID();
        EcheanceEntity e = new EcheanceEntity();
        e.setStatut("PAYEE");
        when(echeances.findByIdAndProprietaireId(echeanceId, proprio)).thenReturn(Optional.of(e));
        ApiException ex = assertThrows(ApiException.class, () -> service.initier(proprio, echeanceId, "WAVE"));
        assertEquals(HttpStatus.CONFLICT, ex.getStatus());
    }

    @Test
    void us21AvisDoublon() {
        AvisRepository avis = mock(AvisRepository.class);
        UniteLocativeRepository unites = mock(UniteLocativeRepository.class);
        AvisService service = new AvisService(avis, unites);
        UUID auteur = UUID.randomUUID();
        UUID unite = UUID.randomUUID();
        when(unites.findById(unite)).thenReturn(Optional.of(new UniteLocativeEntity()));
        when(avis.findByAuteurIdAndCibleUniteId(auteur, unite)).thenReturn(Optional.of(new AvisEntity()));
        ApiException ex = assertThrows(ApiException.class, () -> service.publier(auteur, new AvisRequest(unite, 5, "bien")));
        assertEquals(HttpStatus.CONFLICT, ex.getStatus());
    }

    @Test
    void us23NotificationDUnAutreUtilisateurInvisible() {
        NotificationService service = new NotificationService(mock(NotificationRepository.class), mock(NotificationHub.class));
        ApiException ex = assertThrows(ApiException.class, () -> service.marquerLu(UUID.randomUUID(), UUID.randomUUID()));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
    }

    @Test
    void us26ContactsMasquesTantQueLesDeuxNontPasSigne() {
        ContratRepository contrats = mock(ContratRepository.class);
        ContactReveleService service = new ContactReveleService(contrats, mock(SignatureRepository.class), mock(UtilisateurRepository.class), mock(DossierRepository.class));
        UUID proprio = UUID.randomUUID();
        UUID contratId = UUID.randomUUID();
        when(contrats.findByIdAndProprietaireId(contratId, proprio)).thenReturn(Optional.of(new ContratEntity()));
        assertFalse(service.contacts(proprio, contratId).revele());
    }

    @Test
    void us28CertificatRefuseSiContratNonActif() {
        ContratRepository contrats = mock(ContratRepository.class);
        CertificatService service = new CertificatService(contrats, mock(UniteLocativeRepository.class), mock(BienImmobilierRepository.class), mock(DossierRepository.class));
        UUID proprio = UUID.randomUUID();
        UUID contratId = UUID.randomUUID();
        ContratEntity c = new ContratEntity();
        c.setStatut("BROUILLON");
        when(contrats.findByIdAndProprietaireId(contratId, proprio)).thenReturn(Optional.of(c));
        ApiException ex = assertThrows(ApiException.class, () -> service.emettre(proprio, contratId));
        assertEquals(HttpStatus.CONFLICT, ex.getStatus());
    }

    @Test
    void us29ResiliationRefuseeSiContratNonActif() {
        ContratRepository contrats = mock(ContratRepository.class);
        ContratService service = new ContratService(
                contrats, mock(AvenantRepository.class), mock(UniteLocativeRepository.class), mock(BienImmobilierRepository.class),
                mock(DossierRepository.class), mock(ReservationRepository.class), mock(CautionService.class));
        UUID proprio = UUID.randomUUID();
        UUID id = UUID.randomUUID();
        ContratEntity c = new ContratEntity();
        c.setStatut("BROUILLON");
        when(contrats.findByIdAndProprietaireId(id, proprio)).thenReturn(Optional.of(c));
        ApiException ex = assertThrows(ApiException.class, () -> service.resilier(proprio, id, new ResiliationRequest(10)));
        assertEquals(HttpStatus.CONFLICT, ex.getStatus());
    }
}
