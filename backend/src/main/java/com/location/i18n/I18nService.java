package com.location.i18n;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import org.springframework.stereotype.Service;

@Service
public class I18nService {
    private static final List<String> KEYS = List.of(
            "app.name",
            "nav.biens",
            "nav.locataires",
            "nav.loyers",
            "nav.contrats",
            "nav.messages",
            "nav.visites",
            "auth.login",
            "auth.register",
            "paiement.recu",
            "error.unauthorized");

    public Map<String, Object> catalogue(String lang) {
        String tag = normalize(lang);
        ResourceBundle bundle = ResourceBundle.getBundle("i18n.messages", Locale.forLanguageTag(tag.equals("en") ? "en" : tag.equals("wo") ? "wo" : "fr"));
        Map<String, String> messages = new LinkedHashMap<>();
        for (String key : KEYS) {
            messages.put(key, bundle.containsKey(key) ? bundle.getString(key) : key);
        }
        return Map.of("locale", tag, "messages", messages);
    }

    public static String normalize(String lang) {
        if (lang == null || lang.isBlank()) {
            return "fr";
        }
        String l = lang.toLowerCase(Locale.ROOT);
        if (l.startsWith("en")) {
            return "en";
        }
        if (l.startsWith("wo")) {
            return "wo";
        }
        return "fr";
    }
}
