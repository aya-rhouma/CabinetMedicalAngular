package com.cabinet.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/** Modification de son propre profil (le mot de passe se change séparément). */
public record ProfileRequest(
        @NotBlank(message = "Le nom est obligatoire") String nom,
        @NotBlank(message = "Le prénom est obligatoire") String prenom,
        String adresse,
        @NotBlank(message = "Le login (email) est obligatoire") @Email(message = "Email invalide") String email,
        Long idspec) {
}
