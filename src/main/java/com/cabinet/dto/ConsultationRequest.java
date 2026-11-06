package com.cabinet.dto;

import java.time.LocalDate;

/** Contenu d'une consultation saisie par le médecin. */
public record ConsultationRequest(
        LocalDate datecons,
        String recapcons,
        String ordonnance,
        String certificat) {
}
