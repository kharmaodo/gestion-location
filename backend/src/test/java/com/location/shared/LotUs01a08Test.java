package com.location.shared;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.location.biens.entity.BienImmobilierEntity;
import com.location.biens.entity.UniteLocativeEntity;
import com.location.biens.repository.BienImmobilierRepository;
import com.location.biens.repository.UniteLocativeRepository;
import com.location.biens.service.BienService;
import com.location.biens.service.MediaService;
import com.location.contrats.entity.ContratEntity;
import com.location.contrats.repository.AvenantRepository;
import com.location.contrats.repository.ContratRepository;
import com.location.contrats.service.CautionService;
import com.location.contrats.service.ContratService;
import com.location.identites.dto.LoginRequest;
import com.location.identites.dto.RegisterRequest;
import com.location.identites.entity.UtilisateurEntity;
import com.location.identites.repository.ConsentementRepository;
import com.location.identites.repository.RefreshTokenRepository;
import com.location.identites.repository.ResetPasswordRepository;
import com.location.identites.repository.UtilisateurRepository;
import com.location.identites.repository.UtilisateurRoleRepository;
import com.location.identites.service.AuthService;
import com.location.identites.service.MailService;
import com.location.locataires.dto.KycDecisionRequest;
import com.location.locataires.entity.DossierEntity;
import com.location.locataires.repository.DocumentRepository;
import com.location.locataires.repository.DossierRepository;
import com.location.locataires.service.DossierService;
import com.location.reservations.repository.ReservationRepository;
import com.location.shared.exception.ApiException;
import com.location.shared.security.JwtService;
import com.location.shared.security.TotpService;
import com.location.visites.dto.VisiteRequest;
import com.location.visites.repository.VisiteRepository;
import com.location.visites.service.VisiteService;
import com.location.vitrine.service.VitrineService;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

class LotUs01a08Test {
    @Test
    void us01EmailDejaPrisEtMotDePasseFaux() {
        UtilisateurRepository utilisateurs = mock(UtilisateurRepository.class);
        AuthService auth = new AuthService(
                utilisateurs, mock(UtilisateurRoleRepository.class), mock(ConsentementRepository.class),
                mock(RefreshTokenRepository.class), mock(ResetPasswordRepository.class), mock(PasswordEncoder.class),
                mock(JwtService.class), mock(TotpService.class), mock(MailService.class));
        when(utilisateurs.findByEmail("a@test.sn")).thenReturn(Optional.of(new UtilisateurEntity()));
        ApiException doublon = assertThrows(ApiException.class, () -> auth.register(
                new RegisterRequest("PROPRIETAIRE", "a@test.sn", null, "Motdepasse1", "Awa", "Fall", true)));
        assertEquals(HttpStatus.CONFLICT, doublon.getStatus());

        PasswordEncoder encoder = mock(PasswordEncoder.class);
        AuthService login = new AuthService(
                utilisateurs, mock(UtilisateurRoleRepository.class), mock(ConsentementRepository.class),
                mock(RefreshTokenRepository.class), mock(ResetPasswordRepository.class), encoder,
                mock(JwtService.class), mock(TotpService.class), mock(MailService.class));
        UtilisateurEntity user = new UtilisateurEntity();
        user.setMotDePasseHash("hash");
        user.setStatut("ACTIF");
        when(utilisateurs.findByEmail("a@test.sn")).thenReturn(Optional.of(user));
        when(encoder.matches("mauvais", "hash")).thenReturn(false);
        ApiException refuse = assertThrows(ApiException.class, () -> login.login(new LoginRequest("a@test.sn", "mauvais", null)));
        assertEquals(HttpStatus.UNAUTHORIZED, refuse.getStatus());
    }

    @Test
    void us03PublicationRefuseeSansTroisPhotos() {
        BienImmobilierRepository biens = mock(BienImmobilierRepository.class);
        UniteLocativeRepository unites = mock(UniteLocativeRepository.class);
        MediaService medias = mock(MediaService.class);
        BienService service = new BienService(biens, unites, medias);
        UUID proprio = UUID.randomUUID();
        UUID bienId = UUID.randomUUID();
        UUID uniteId = UUID.randomUUID();
        BienImmobilierEntity bien = new BienImmobilierEntity();
        bien.setId(bienId);
        bien.setProprietaireId(proprio);
        UniteLocativeEntity unite = new UniteLocativeEntity();
        unite.setId(uniteId);
        when(biens.findByIdAndProprietaireId(bienId, proprio)).thenReturn(Optional.of(bien));
        when(unites.findByIdAndBienId(uniteId, bienId)).thenReturn(Optional.of(unite));
        when(medias.nombrePhotos(uniteId)).thenReturn(2L);
        ApiException ex = assertThrows(ApiException.class, () -> service.publier(proprio, bienId, uniteId, true));
        assertEquals(HttpStatus.CONFLICT, ex.getStatus());
    }

    @Test
    void us05VisiteRefuseeSiUniteNonPubliee() {
        UniteLocativeRepository unites = mock(UniteLocativeRepository.class);
        VisiteService service = new VisiteService(mock(VisiteRepository.class), unites, mock(BienImmobilierRepository.class));
        UUID uniteId = UUID.randomUUID();
        UniteLocativeEntity unite = new UniteLocativeEntity();
        unite.setPublie(false);
        unite.setStatut("LIBRE");
        when(unites.findById(uniteId)).thenReturn(Optional.of(unite));
        ApiException ex = assertThrows(ApiException.class, () -> service.demander(
                new VisiteRequest(uniteId, "Diop", "770000000", null, Instant.now().plusSeconds(3600))));
        assertEquals(HttpStatus.CONFLICT, ex.getStatus());
    }

    @Test
    void us06AnnonceNonPublieeInvisible() {
        UniteLocativeRepository unites = mock(UniteLocativeRepository.class);
        VitrineService service = new VitrineService(unites, mock(BienImmobilierRepository.class), mock(ReservationRepository.class));
        UUID uniteId = UUID.randomUUID();
        UniteLocativeEntity unite = new UniteLocativeEntity();
        unite.setPublie(false);
        when(unites.findById(uniteId)).thenReturn(Optional.of(unite));
        ApiException ex = assertThrows(ApiException.class, () -> service.detail(uniteId));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
    }

    @Test
    void us07KycNePeutPasRedevenirEnAttente() {
        DossierRepository dossiers = mock(DossierRepository.class);
        DossierService service = new DossierService(dossiers, mock(DocumentRepository.class));
        UUID proprio = UUID.randomUUID();
        UUID id = UUID.randomUUID();
        DossierEntity dossier = new DossierEntity();
        dossier.setId(id);
        when(dossiers.findByIdAndProprietaireId(id, proprio)).thenReturn(Optional.of(dossier));
        ApiException ex = assertThrows(ApiException.class, () -> service.deciderKyc(proprio, id, new KycDecisionRequest("EN_ATTENTE", null)));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
    }

    @Test
    void us08ContratDejaActifNonReactivable() {
        ContratRepository contrats = mock(ContratRepository.class);
        ContratService service = new ContratService(
                contrats, mock(AvenantRepository.class), mock(UniteLocativeRepository.class), mock(BienImmobilierRepository.class),
                mock(DossierRepository.class), mock(ReservationRepository.class), mock(CautionService.class));
        UUID proprio = UUID.randomUUID();
        UUID id = UUID.randomUUID();
        ContratEntity contrat = new ContratEntity();
        contrat.setStatut("ACTIF");
        when(contrats.findByIdAndProprietaireId(id, proprio)).thenReturn(Optional.of(contrat));
        ApiException ex = assertThrows(ApiException.class, () -> service.activer(proprio, id));
        assertEquals(HttpStatus.CONFLICT, ex.getStatus());
    }
}
