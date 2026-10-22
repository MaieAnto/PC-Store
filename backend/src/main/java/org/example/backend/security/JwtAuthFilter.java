package org.example.backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/* Filtro che gira UNA volta per ogni richiesta HTTP

    se la richiesta porta un token valido nell'header
   "Authorization: Bearer <token>", riconosce l'utente e lo segna come
   "autenticato" dentro Spring Security, indicando anche il suo RUOLO
   (ROLE_CLIENTE / ROLE_FORNITORE). Da quel momento le regole in SecurityConfig
   possono decidere se consentire o negare l'accesso.

   Se il token manca o non e' valido non blocchiamo qui: lasciamo proseguire la
   richiesta "non autenticata" e sara' SecurityConfig a rifiutarla se la risorsa
   e' protetta. */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // toglie "Bearer "

            if (jwtUtil.validateToken(token)) {
                String email = jwtUtil.extractEmail(token);
                String ruolo = jwtUtil.extractRuolo(token);
                var autorizzazioni = List.of(new SimpleGrantedAuthority("ROLE_" + ruolo));
                var autenticazione = new UsernamePasswordAuthenticationToken(email, null, autorizzazioni);
                SecurityContextHolder.getContext().setAuthentication(autenticazione);
            }
        }
        filterChain.doFilter(request, response);
    }
}
