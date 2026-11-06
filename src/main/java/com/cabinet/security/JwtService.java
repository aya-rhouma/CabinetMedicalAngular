package com.cabinet.security;

import com.cabinet.entities.Utilisateur;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey key;
    private final long expirationMs;

    public JwtService(
            @Value("${app.jwt.secret:cabinet-medical-secret-key-change-me-0123456789}") String secret,
            @Value("${app.jwt.expiration-ms:86400000}") long expirationMs) {
        // la clé HS256 doit faire au moins 32 octets
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    public String generate(Utilisateur utilisateur) {
        Date now = new Date();
        return Jwts.builder()
                .subject(utilisateur.getEmail())
                .claim("role", utilisateur.getRole().name())
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expirationMs))
                .signWith(key)
                .compact();
    }

    /** Lève une JwtException si le jeton est invalide ou expiré. */
    public Claims parse(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }
}
