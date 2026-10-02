package com.location.shared;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import com.location.canaux.CanalMockService;
import com.location.contrats.dto.CautionSimulationRequest;
import com.location.contrats.service.CautionService;
import com.location.i18n.I18nService;
import com.location.shared.exception.ApiException;
import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class ServicesReglesTest {
    private static final List<String> SERVICES = List.of(
            "com.location.admin.service.AdminService",
            "com.location.avis.service.AvisService",
            "com.location.biens.service.BienService",
            "com.location.biens.service.MediaService",
            "com.location.canaux.CanalMockService",
            "com.location.contrats.service.CautionService",
            "com.location.contrats.service.CertificatService",
            "com.location.contrats.service.ContactReveleService",
            "com.location.contrats.service.ContratService",
            "com.location.contrats.service.EtatLieuxService",
            "com.location.contrats.service.RestitutionCautionService",
            "com.location.contrats.service.SignatureService",
            "com.location.identites.service.AuthService",
            "com.location.identites.service.MailService",
            "com.location.i18n.I18nService",
            "com.location.paiements.service.RelanceService",
            "com.location.messages.service.MessageService",
            "com.location.litiges.service.LitigeService",
            "com.location.notifications.service.NotificationService",
            "com.location.visites.service.VisiteService"
    );

    @Test
    void chaqueServiceSeConstruitAvecDesMocks() throws Exception {
        for (String name : SERVICES) {
            Class<?> type = Class.forName(name);
            Constructor<?> ctor = plusPetitConstructeur(type);
            Object[] args = new Object[ctor.getParameterCount()];
            Class<?>[] params = ctor.getParameterTypes();
            for (int i = 0; i < params.length; i++) {
                if (params[i] == String.class) {
                    args[i] = "MOCK";
                } else if (params[i].isPrimitive()) {
                    args[i] = params[i] == boolean.class ? false : 0;
                } else {
                    args[i] = mock(params[i]);
                }
            }
            ctor.setAccessible(true);
            assertNotNull(ctor.newInstance(args), name);
        }
    }

    @Test
    void cautionEtCanauxGardentLeursRegles() {
        var caution = new CautionService().simuler(new CautionSimulationRequest(new BigDecimal("10000"), "MENSUEL", 9));
        assertEquals(3, caution.moisRetenus());
        ApiException off = assertThrows(ApiException.class, () -> new CanalMockService("OFF", "MOCK", "MOCK").sms("77", "x"));
        assertEquals(HttpStatus.CONFLICT, off.getStatus());
        assertEquals("en", I18nService.normalize("en-GB"));
    }

    private static Constructor<?> plusPetitConstructeur(Class<?> type) {
        Constructor<?> choisi = null;
        for (Constructor<?> ctor : type.getDeclaredConstructors()) {
            if (choisi == null || ctor.getParameterCount() < choisi.getParameterCount()) {
                choisi = ctor;
            }
        }
        if (choisi == null) {
            throw new IllegalStateException(type.getName());
        }
        return choisi;
    }
}
