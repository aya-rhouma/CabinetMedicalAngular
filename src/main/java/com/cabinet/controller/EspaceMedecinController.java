package com.cabinet.controller;

import com.cabinet.dto.ConsultationRequest;
import com.cabinet.dto.StatutRequest;
import com.cabinet.entities.Rendezvous;
import com.cabinet.service.interfaces.IEspaceMedecinService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medecin")
@RequiredArgsConstructor
public class EspaceMedecinController {

    private final IEspaceMedecinService espaceMedecinService;

    @GetMapping("/rendezvous")
    public List<Rendezvous> mesRendezvous(Authentication authentication) {
        return espaceMedecinService.mesRendezvous(authentication.getName());
    }

    @PutMapping("/rendezvous/{id}/statut")
    public Rendezvous changerStatut(Authentication authentication, @PathVariable Long id,
                                    @Valid @RequestBody StatutRequest request) {
        return espaceMedecinService.changerStatut(authentication.getName(), id, request.statut());
    }

    @PostMapping("/rendezvous/{id}/consultation")
    public ResponseEntity<Rendezvous> ajouterConsultation(Authentication authentication, @PathVariable Long id,
                                                          @RequestBody ConsultationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(espaceMedecinService.ajouterConsultation(authentication.getName(), id, request));
    }

    @PutMapping("/rendezvous/{id}/consultation")
    public Rendezvous modifierConsultation(Authentication authentication, @PathVariable Long id,
                                           @RequestBody ConsultationRequest request) {
        return espaceMedecinService.modifierConsultation(authentication.getName(), id, request);
    }
}
