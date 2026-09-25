package com.location.i18n;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import org.springframework.stereotype.Service;

@Service
public class I18nService {
    public Map<String, Object> catalogue(String lang) {
        String tag = normalize(lang);
        Locale locale = Locale.forLanguageTag(tag.equals("en") ? "en" : tag.equals("wo") ? "wo" : "fr");
        ResourceBundle bundle = ResourceBundle.getBundle("i18n.messages", locale);
        Map<String, String> messages = new LinkedHashMap<>();
        for (String key : bundle.keySet()) {
            messages.put(key, bundle.getString(key));
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
