package com.location.i18n;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/public/i18n")
public class I18nController {
    private final I18nService service;

    public I18nController(I18nService service) {
        this.service = service;
    }

    @GetMapping
    public Map<String, Object> catalogue(
            @RequestParam(required = false) String lang,
            @RequestHeader(value = "Accept-Language", required = false) String accept) {
        return service.catalogue(lang != null ? lang : accept);
    }
}
