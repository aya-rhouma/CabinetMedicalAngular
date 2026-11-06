package com.cabinet.controller;

import com.cabinet.dto.AuthResponse;
import com.cabinet.dto.ChangePasswordRequest;
import com.cabinet.dto.LoginRequest;
import com.cabinet.dto.ProfileRequest;
import com.cabinet.dto.ProfileResponse;
import com.cabinet.dto.RegisterRequest;
import com.cabinet.service.interfaces.IAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final IAuthService authService;

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    /** Inscription d'un patient. */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    /** Profil de l'utilisateur connecté (tout rôle). */
    @GetMapping("/profile")
    public ProfileResponse profile(Authentication authentication) {
        return authService.getProfile(authentication.getName());
    }

    /** Modifie le profil ; renvoie une nouvelle session (le login peut changer). */
    @PutMapping("/profile")
    public AuthResponse updateProfile(Authentication authentication,
                                      @Valid @RequestBody ProfileRequest request) {
        return authService.updateProfile(authentication.getName(), request);
    }

    /** Disponible pour tout utilisateur connecté. */
    @PostMapping("/change-password")
    public Map<String, String> changePassword(Authentication authentication,
                                              @Valid @RequestBody ChangePasswordRequest request) {
        authService.changePassword(authentication.getName(), request);
        return Map.of("message", "Mot de passe modifié avec succès.");
    }
}
