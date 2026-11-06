package com.cabinet.controller;

import com.cabinet.dto.CreateMedecinRequest;
import com.cabinet.dto.UpdateMedecinRequest;
import com.cabinet.entities.Medecin;
import com.cabinet.entities.Patient;
import com.cabinet.entities.Specialite;
import com.cabinet.service.interfaces.IAdminService;
import com.cabinet.service.interfaces.IMedecinService;
import com.cabinet.service.interfaces.ISpecialiteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final IAdminService adminService;
    private final IMedecinService medecinService;
    private final ISpecialiteService specialiteService;

    @GetMapping("/stats")
    public Map<String, Object> stats() {
        return adminService.statistiques();
    }

    @GetMapping("/medecins")
    public List<Medecin> medecins() {
        return medecinService.findAll();
    }

    @PostMapping("/medecins")
    public ResponseEntity<Medecin> creerMedecin(@Valid @RequestBody CreateMedecinRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.creerMedecin(request));
    }

    @PutMapping("/medecins/{id}")
    public Medecin modifierMedecin(@PathVariable Long id, @Valid @RequestBody UpdateMedecinRequest request) {
        return adminService.modifierMedecin(id, request);
    }

    @DeleteMapping("/medecins/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void supprimerMedecin(@PathVariable Long id) {
        adminService.supprimerMedecin(id);
    }

    @GetMapping("/patients")
    public List<Patient> patients() {
        return adminService.patients();
    }

    @GetMapping("/specialites")
    public List<Specialite> specialites() {
        return specialiteService.findAll();
    }

    @PostMapping("/specialites")
    public ResponseEntity<Specialite> creerSpecialite(@Valid @RequestBody Specialite specialite) {
        return ResponseEntity.status(HttpStatus.CREATED).body(specialiteService.save(specialite));
    }

    @PutMapping("/specialites/{id}")
    public Specialite modifierSpecialite(@PathVariable Long id, @Valid @RequestBody Specialite specialite) {
        return specialiteService.update(id, specialite);
    }

    @DeleteMapping("/specialites/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void supprimerSpecialite(@PathVariable Long id) {
        specialiteService.delete(id);
    }
}
