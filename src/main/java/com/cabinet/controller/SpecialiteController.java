package com.cabinet.controller;

import com.cabinet.entities.Specialite;
import com.cabinet.service.interfaces.ISpecialiteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/specialites")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class SpecialiteController {

    private final ISpecialiteService specialiteService;

    @GetMapping
    public List<Specialite> getAll() {
        return specialiteService.findAll();
    }

    @GetMapping("/{id}")
    public Specialite getById(@PathVariable Long id) {
        return specialiteService.findById(id);
    }

    @PostMapping
    public ResponseEntity<Specialite> create(@Valid @RequestBody Specialite specialite) {
        return ResponseEntity.status(HttpStatus.CREATED).body(specialiteService.save(specialite));
    }

    @PutMapping("/{id}")
    public Specialite update(@PathVariable Long id, @Valid @RequestBody Specialite specialite) {
        return specialiteService.update(id, specialite);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        specialiteService.delete(id);
    }
}
