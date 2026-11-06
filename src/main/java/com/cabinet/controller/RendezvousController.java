package com.cabinet.controller;

import com.cabinet.entities.Rendezvous;
import com.cabinet.service.interfaces.IRendezvousService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rendezvous")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class RendezvousController {

    private final IRendezvousService rendezvousService;

    @GetMapping
    public List<Rendezvous> getAll() {
        return rendezvousService.findAll();
    }

    @GetMapping("/medecin/{idMedecin}")
    public List<Rendezvous> getByMedecin(@PathVariable Long idMedecin) {
        return rendezvousService.findByMedecin(idMedecin);
    }

    @GetMapping("/patient/{idPatient}")
    public List<Rendezvous> getByPatient(@PathVariable Long idPatient) {
        return rendezvousService.findByPatient(idPatient);
    }

    @GetMapping("/{id}")
    public Rendezvous getById(@PathVariable Long id) {
        return rendezvousService.findById(id);
    }

    @PostMapping
    public ResponseEntity<Rendezvous> create(@Valid @RequestBody Rendezvous rendezvous) {
        return ResponseEntity.status(HttpStatus.CREATED).body(rendezvousService.save(rendezvous));
    }

    @PutMapping("/{id}")
    public Rendezvous update(@PathVariable Long id, @Valid @RequestBody Rendezvous rendezvous) {
        return rendezvousService.update(id, rendezvous);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        rendezvousService.delete(id);
    }
}
