package com.cabinet.controller;

import com.cabinet.entities.Consultation;
import com.cabinet.service.interfaces.IConsultationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/consultations")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class ConsultationController {

    private final IConsultationService consultationService;

    @GetMapping
    public List<Consultation> getAll() {
        return consultationService.findAll();
    }

    @GetMapping("/rendezvous/{idrdv}")
    public Consultation getByRendezvous(@PathVariable Long idrdv) {
        return consultationService.findByRendezvous(idrdv);
    }

    @GetMapping("/{id}")
    public Consultation getById(@PathVariable Long id) {
        return consultationService.findById(id);
    }

    @PostMapping
    public ResponseEntity<Consultation> create(@Valid @RequestBody Consultation consultation) {
        return ResponseEntity.status(HttpStatus.CREATED).body(consultationService.save(consultation));
    }

    @PutMapping("/{id}")
    public Consultation update(@PathVariable Long id, @Valid @RequestBody Consultation consultation) {
        return consultationService.update(id, consultation);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        consultationService.delete(id);
    }
}
