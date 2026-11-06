package com.cabinet.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** Modification d'un médecin par l'administrateur (tous les attributs sauf le mot de passe). */
public record UpdateMedecinRequest(
        @NotBlank(message = "Le nom est obligatoire") String nom,
        @NotBlank(message = "Le prénom est obligatoire") String prenom,
        String adr,
        @NotBlank(message = "L'email est obligatoire") @Email(message = "Email invalide") String email,
        @NotNull(message = "La spécialité est obligatoire") Long idspec) {
}
