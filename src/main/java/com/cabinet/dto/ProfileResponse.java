package com.cabinet.dto;

import com.cabinet.entities.Role;

public record ProfileResponse(Role role, String nom, String prenom, String adresse, String email, Long idspec) {
}
