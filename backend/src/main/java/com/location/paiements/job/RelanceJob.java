package com.location.paiements.job;

import com.location.paiements.service.RelanceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.relances.enabled", havingValue = "true", matchIfMissing = true)
public class RelanceJob {
    private static final Logger log = LoggerFactory.getLogger(RelanceJob.class);
    private final RelanceService relances;

    public RelanceJob(RelanceService relances) {
        this.relances = relances;
    }

    @Scheduled(cron = "${app.relances.cron:0 0 8 * * *}")
    public void run() {
        int n = relances.declencherTous();
        log.info("Job relances : {} email(s)", n);
    }
}
