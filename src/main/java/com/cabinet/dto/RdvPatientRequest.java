package com.cabinet.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

/** Demande de rendez-vous faite par un patient connecté. */
public record RdvPatientRequest(
        @NotNull(message = "Le médecin est obligatoire") Long idMedecin,
        @NotNull(message = "La date est obligatoire") LocalDate daterdv,
        @NotNull(message = "L'heure est obligatoire") LocalTime heurerdv) {
}
