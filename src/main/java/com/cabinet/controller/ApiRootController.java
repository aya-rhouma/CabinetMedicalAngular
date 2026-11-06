package com.cabinet.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiRootController {

    @GetMapping
    public Map<String, Object> root() {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("application", "Cabinet médical API");
        body.put("routes", new String[]{
                "/api/specialites", "/api/medecins", "/api/patients",
                "/api/rendezvous", "/api/consultations"});
        return body;
    }
}
