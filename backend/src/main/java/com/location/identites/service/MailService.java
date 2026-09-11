package com.location.identites.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {
    private static final Logger log = LoggerFactory.getLogger(MailService.class);
    private final ObjectProvider<JavaMailSender> mailSender;
    private final String from;
    private final String frontendUrl;

    public MailService(
            ObjectProvider<JavaMailSender> mailSender,
            @Value("${app.mail.from:noreply@localhost}") String from,
            @Value("${app.mail.frontend-url:http://localhost:5173}") String frontendUrl) {
        this.mailSender = mailSender;
        this.from = from;
        this.frontendUrl = frontendUrl;
    }

    public void sendResetPassword(String to, String rawToken) {
        JavaMailSender sender = mailSender.getIfAvailable();
        String link = frontendUrl + "/reset-mot-de-passe?token=" + rawToken;
        if (sender == null || to == null) {
            log.info("Reset password (mail off) pour {} : {}", to, link);
            return;
        }
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(from);
        msg.setTo(to);
        msg.setSubject("Reinitialisation du mot de passe");
        msg.setText("Lien valable 1 heure :\n" + link);
        sender.send(msg);
    }
}
