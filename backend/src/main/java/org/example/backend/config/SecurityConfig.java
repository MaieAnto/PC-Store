package org.example.backend.config;

import org.example.backend.security.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
@Configuration
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthFilter jwtAuthFilter) throws Exception {
        http
                // CSRF: non serve per un'API REST stateless consumata da Angular.
                .csrf(csrf -> csrf.disable())
                // CORS abilitato.
                .cors(Customizer.withDefaults())
                // Nessuna sessione, ci si affida al token a ogni richiesta.
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        //login e registrazione pubblici
                        .requestMatchers(HttpMethod.POST, "/api/utenti/login", "/api/utenti/registrazione").permitAll()

                        // L'elenco dei prodotti ELIMINATI e' solo per il fornitore:
                        .requestMatchers(HttpMethod.GET, "/api/prodotti/eliminati").hasRole("FORNITORE")
                        // Catalogo navigabile anche senza login
                        .requestMatchers(HttpMethod.GET, "/api/prodotti", "/api/prodotti/**").permitAll()
                        // Recensioni e media di un prodotto visibili a tutti.
                        .requestMatchers(HttpMethod.GET, "/api/recensioni/prodotto/**").permitAll()
                        //Gestione prodotti solo fornitore
                        .requestMatchers(HttpMethod.POST, "/api/prodotti").hasRole("FORNITORE")
                        .requestMatchers(HttpMethod.PUT, "/api/prodotti/**").hasRole("FORNITORE")
                        .requestMatchers(HttpMethod.DELETE, "/api/prodotti/**").hasRole("FORNITORE")
                        // --- Rifornimenti magazzino solo fornitore
                        .requestMatchers("/api/rifornimenti/**").hasRole("FORNITORE")
                        // --- Carrello, ordini e creazione recensioni solo cliente
                        .requestMatchers("/api/carrello/**").hasRole("CLIENTE")
                        .requestMatchers("/api/ordini/**").hasRole("CLIENTE")
                        .requestMatchers(HttpMethod.POST, "/api/recensioni").hasRole("CLIENTE")

                        .anyRequest().authenticated()
                )
                // Inserisce il filtro JWT prima del filtro standard di autenticazione.
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
