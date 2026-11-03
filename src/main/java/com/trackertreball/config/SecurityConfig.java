package com.trackertreball.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuracion de seguridad web para las APIs REST de Tracker Treball.
 * <p>
 * Implementa una arquitectura de seguridad sin estado (stateless) adecuada para servicios REST desacoplados.
 * Integra las politicas CORS y permite acceso a los endpoints publicos.
 * </p>
 *
 * @author Hassan Adraou
 * @version 1.0.0
 * @since 2026-11-02
 * @see org.springframework.security.web.SecurityFilterChain
 * @see org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Construye y registra la cadena de filtros de seguridad de Spring Security (SecurityFilterChain).
     *
     * @param http Objeto {@link HttpSecurity} para componer los filtros de seguridad web.
     * @return {@link SecurityFilterChain} compilada con las directivas de seguridad.
     * @throws Exception Si ocurre un fallo en la inicializacion de los filtros de seguridad.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(Customizer.withDefaults())
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/**").permitAll()
                .anyRequest().permitAll()
            );

        return http.build();
    }
}
