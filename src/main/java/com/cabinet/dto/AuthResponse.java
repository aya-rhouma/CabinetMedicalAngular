package com.cabinet.dto;

import com.cabinet.entities.Role;

public record AuthResponse(String token, Role role, String email, String nomComplet) {
}
