package com.cabinet.controller;

import com.cabinet.entities.Medecin;
import com.cabinet.entities.Specialite;
import com.cabinet.service.interfaces.IMedecinService;
import com.cabinet.service.interfaces.ISpecialiteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Données visibles sans connexion (page d'accueil). */
@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class PublicController {

    private final IMedecinService medecinService;
    private final ISpecialiteService specialiteService;

    @GetMapping("/medecins")
    public List<Medecin> medecins() {
        return medecinService.findAll();
    }

    @GetMapping("/specialites")
    public List<Specialite> specialites() {
        return specialiteService.findAll();
    }
}
