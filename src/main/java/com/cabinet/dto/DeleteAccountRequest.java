package com.cabinet.dto;

import jakarta.validation.constraints.NotBlank;

public record DeleteAccountRequest(
        @NotBlank(message = "Le mot de passe est obligatoire pour confirmer") String motDePasse) {
}
