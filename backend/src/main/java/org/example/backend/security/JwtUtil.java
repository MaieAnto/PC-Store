package org.example.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.example.backend.entities.Ruolo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration-ms}")
    private long expirationMs;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String email, Ruolo ruolo) {
        Date ora = new Date();
        Date scadenza = new Date(ora.getTime() + expirationMs);
        return Jwts.builder().subject(email).claim("ruolo", ruolo.name()).issuedAt(ora).expiration(scadenza)
                .signWith(getSigningKey()).compact();
    }

    /* Email contenuta nel token */
    public String extractEmail(String token) {
        return parse(token).getSubject();
    }

    /* Ruolo contenuto nel token */
    public String extractRuolo(String token) {
        return parse(token).get("ruolo", String.class);
    }

    /* Verifica che il token sia valido */
    public boolean validateToken(String token) {
        try {
            parse(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Verifica la firma e restituisce il contenuto del token.
    private Claims parse(String token) {
        return Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload();
    }
}
