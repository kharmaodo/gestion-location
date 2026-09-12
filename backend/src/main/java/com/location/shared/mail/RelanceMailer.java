package com.location.shared.mail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class RelanceMailer {
    private static final Logger log = LoggerFactory.getLogger(RelanceMailer.class);
    private final JavaMailSender mail;
    private final String from;

    public RelanceMailer(JavaMailSender mail, @Value("${app.mail.from:noreply@gestion-location.local}") String from) {
        this.mail = mail;
        this.from = from;
    }

    public void envoyer(String to, String sujet, String corps) {
        if (to == null || to.isBlank()) {
            log.info("Relance sans destinataire email");
            return;
        }
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom(from);
            msg.setTo(to);
            msg.setSubject(sujet);
            msg.setText(corps);
            mail.send(msg);
            log.info("Relance email envoyee a {}", to);
        } catch (Exception e) {
            log.warn("Relance email non envoyee ({}). Mailhog : docker compose -f docker-compose.dev.yml up -d mailhog", e.getMessage());
        }
    }
}
