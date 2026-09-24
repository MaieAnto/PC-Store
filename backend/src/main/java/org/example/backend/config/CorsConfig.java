package org.example.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

/*
 * Configurazione CORS globale (sostituisce i vecchi @CrossOrigin sui singoli controller).
 *
 * Il frontend Angular gira su http://localhost:4200 e il backend su http://localhost:8080:
 * per il browser sono due "origini" diverse (cambia la porta), quindi blocca le risposte
 * del backend a meno che il backend non dichiari esplicitamente che l'origine del
 * frontend e' ammessa. Questo filtro aggiunge a ogni risposta di /api/** le intestazioni
 * CORS necessarie (Access-Control-Allow-Origin, ...).
 *
 * Il bean si chiama "corsFilter": Spring Security (vedi SecurityConfig, .cors(...))
 * lo trova per nome e lo usa all'interno della propria catena di filtri.
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        // Origini ammesse: solo il frontend in sviluppo.
        config.setAllowedOrigins(List.of("http://localhost:4200"));
        // Metodi HTTP ammessi (OPTIONS serve per le richieste "preflight" del browser).
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        // Qualsiasi intestazione (es. Content-Type: application/json).
        config.setAllowedHeaders(List.of("*"));
        // Permette l'invio di credenziali (cookie / Authorization) quando aggiungeremo la sicurezza.
        config.setAllowCredentials(true);

        // Applichiamo la configurazione a tutte le API.
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);

        return new CorsFilter(source);
    }
}
