package com.cabinet.controller;

import com.cabinet.entities.Medecin;
import com.cabinet.service.interfaces.IMedecinService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medecins")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class MedecinController {

    private final IMedecinService medecinService;

    @GetMapping
    public List<Medecin> getAll(@RequestParam(required = false) String nom) {
        return (nom == null || nom.isBlank()) ? medecinService.findAll() : medecinService.searchByNom(nom);
    }

    @GetMapping("/specialite/{idspec}")
    public List<Medecin> getBySpecialite(@PathVariable Long idspec) {
        return medecinService.findBySpecialite(idspec);
    }

    @GetMapping("/{id}")
    public Medecin getById(@PathVariable Long id) {
        return medecinService.findById(id);
    }

    @PostMapping
    public ResponseEntity<Medecin> create(@Valid @RequestBody Medecin medecin) {
        return ResponseEntity.status(HttpStatus.CREATED).body(medecinService.save(medecin));
    }

    @PutMapping("/{id}")
    public Medecin update(@PathVariable Long id, @Valid @RequestBody Medecin medecin) {
        return medecinService.update(id, medecin);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        medecinService.delete(id);
    }
}
