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

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    // Per ogni richiesta: se c'e' un token JWT valido, autentica l'utente nel contesto di sicurezza.
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
