package org.example.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/*
 * Configurazione di Spring Security (versione PROVVISORIA).
 *
 * Appena si aggiunge la dipendenza spring-boot-starter-security, Spring protegge
 * TUTTI gli endpoint con una login di default (utente "user" + password generata
 * all'avvio): il frontend riceverebbe 401 su ogni chiamata. Per ora la parte di
 * sicurezza non e' ancora sviluppata, quindi:
 *  - csrf disabilitato: e' una protezione pensata per le sessioni con cookie; con
 *    un'API REST chiamata da Angular bloccherebbe le POST/PUT/DELETE;
 *  - cors abilitato: usa il bean "corsFilter" definito in CorsConfig;
 *  - tutte le richieste sono permesse (permitAll).
 *
 * Quando svilupperemo la sicurezza (login con token, ruoli, password cifrate)
 * restringeremo qui le regole, come in InkLink.
 */
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(Customizer.withDefaults())
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());

        return http.build();
    }
}
