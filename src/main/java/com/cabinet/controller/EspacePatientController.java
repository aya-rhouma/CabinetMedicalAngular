package com.cabinet.controller;

import com.cabinet.dto.DeleteAccountRequest;
import com.cabinet.dto.RdvPatientRequest;
import com.cabinet.entities.Rendezvous;
import com.cabinet.service.interfaces.IEspacePatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patient")
@RequiredArgsConstructor
public class EspacePatientController {

    private final IEspacePatientService espacePatientService;

    @GetMapping("/rendezvous")
    public List<Rendezvous> mesRendezvous(Authentication authentication) {
        return espacePatientService.mesRendezvous(authentication.getName());
    }

    @PostMapping("/rendezvous")
    public ResponseEntity<Rendezvous> prendre(Authentication authentication,
                                              @Valid @RequestBody RdvPatientRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(espacePatientService.prendreRendezvous(authentication.getName(), request));
    }

    /** Suppression de son propre compte, après confirmation du mot de passe. */
    @PostMapping("/supprimer-compte")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void supprimerCompte(Authentication authentication,
                                @Valid @RequestBody DeleteAccountRequest request) {
        espacePatientService.supprimerCompte(authentication.getName(), request.motDePasse());
    }

    @PutMapping("/rendezvous/{id}/annuler")
    public Rendezvous annuler(Authentication authentication, @PathVariable Long id) {
        return espacePatientService.annuler(authentication.getName(), id);
    }
}
