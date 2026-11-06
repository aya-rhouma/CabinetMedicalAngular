package com.cabinet.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Création d'un médecin (et de son compte) par l'administrateur. */
public record CreateMedecinRequest(
        @NotBlank(message = "Le nom est obligatoire") String nom,
        @NotBlank(message = "Le prénom est obligatoire") String prenom,
        String adr,
        @NotBlank(message = "L'email est obligatoire") @Email(message = "Email invalide") String email,
        @NotBlank(message = "Le mot de passe initial est obligatoire")
        @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères") String motDePasse,
        @NotNull(message = "La spécialité est obligatoire") Long idspec) {
}
