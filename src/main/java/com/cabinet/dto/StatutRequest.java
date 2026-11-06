package com.cabinet.dto;

import com.cabinet.entities.StatutRdv;
import jakarta.validation.constraints.NotNull;

public record StatutRequest(@NotNull(message = "Le statut est obligatoire") StatutRdv statut) {
}
