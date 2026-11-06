package com.cabinet.controller;

import com.cabinet.entities.Patient;
import com.cabinet.service.interfaces.IPatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class PatientController {

    private final IPatientService patientService;

    @GetMapping
    public List<Patient> getAll(@RequestParam(required = false) String nom) {
        return (nom == null || nom.isBlank()) ? patientService.findAll() : patientService.searchByNom(nom);
    }

    @GetMapping("/{id}")
    public Patient getById(@PathVariable Long id) {
        return patientService.findById(id);
    }

    @PostMapping
    public ResponseEntity<Patient> create(@Valid @RequestBody Patient patient) {
        return ResponseEntity.status(HttpStatus.CREATED).body(patientService.save(patient));
    }

    @PutMapping("/{id}")
    public Patient update(@PathVariable Long id, @Valid @RequestBody Patient patient) {
        return patientService.update(id, patient);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        patientService.delete(id);
    }
}
