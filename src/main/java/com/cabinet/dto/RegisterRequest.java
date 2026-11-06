package com.cabinet.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Inscription d'un patient. */
public record RegisterRequest(
        @NotBlank(message = "Le nom est obligatoire") String nom,
        @NotBlank(message = "Le prénom est obligatoire") String prenom,
        String adresse,
        @NotBlank(message = "L'email est obligatoire") @Email(message = "Email invalide") String email,
        @NotBlank(message = "Le mot de passe est obligatoire")
        @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères") String motDePasse) {
}
