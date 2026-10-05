package com.location.i18n;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class I18nServiceTest {
    @Test
    void normaliseLesLanguesConnues() {
        assertEquals("fr", I18nService.normalize(null));
        assertEquals("fr", I18nService.normalize("  "));
        assertEquals("en", I18nService.normalize("en-US"));
        assertEquals("wo", I18nService.normalize("WO"));
        assertEquals("fr", I18nService.normalize("es"));
    }
}
