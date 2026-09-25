package com.location.dashboard.job;

import com.location.notifications.repository.NotificationRepository;
import com.location.notifications.service.NotificationService;
import com.location.paiements.entity.EcheanceEntity;
import com.location.paiements.repository.EcheanceRepository;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.supervision.enabled", havingValue = "true", matchIfMissing = true)
public class SupervisionJob {
    private static final Logger log = LoggerFactory.getLogger(SupervisionJob.class);
    private final EcheanceRepository echeances;
    private final NotificationService notifications;
    private final NotificationRepository notifRepo;

    public SupervisionJob(
            EcheanceRepository echeances,
            NotificationService notifications,
            NotificationRepository notifRepo) {
        this.echeances = echeances;
        this.notifications = notifications;
        this.notifRepo = notifRepo;
    }

    @Scheduled(cron = "${app.supervision.cron:0 30 8 * * *}")
    public void run() {
        LocalDate today = LocalDate.now();
        Instant debutJour = today.atStartOfDay().toInstant(ZoneOffset.UTC);
        int n = 0;
        for (EcheanceEntity e : echeances.findAll()) {
            if ("PAYEE".equals(e.getStatut())) {
                continue;
            }
            if (e.getPeriodeFin() == null || !e.getPeriodeFin().isBefore(today)) {
                continue;
            }
            if (notifRepo.existsByDestinataireIdAndTypeAndCreeLeAfter(e.getProprietaireId(), "LOYER_RETARD", debutJour)) {
                continue;
            }
            notifications.notifier(
                    e.getProprietaireId(),
                    "LOYER_RETARD",
                    "Loyer en retard depuis " + e.getPeriodeFin() + " — " + e.getMontant() + " " + e.getDevise());
            n++;
        }
        log.info("Job supervision : {} alerte(s)", n);
    }
}
