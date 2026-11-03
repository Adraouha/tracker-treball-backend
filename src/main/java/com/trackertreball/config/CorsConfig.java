package com.trackertreball.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

/**
 * Configuracion de politicas de Intercambio de Recursos de Origen Cruzado (CORS).
 * <p>
 * Permite que clientes web externos (como GitHub Pages o servidores locales de desarrollo)
 * puedan consumir de forma segura las APIs REST expuestas.
 * </p>
 *
 * @author Hassan Adraou
 * @version 1.0.0
 * @since 2026-11-02
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Value("${CORS_ORIGINS:http://localhost:5500,http://127.0.0.1:5500}")
    private String allowedOrigins;

    /**
     * Parsea los origenes permitidos delimitados por coma.
     *
     * @return Arreglo de origenes autorizados sin espacios vacios.
     */
    private String[] parseAllowedOrigins() {
        if (allowedOrigins == null || allowedOrigins.trim().isEmpty()) {
            return new String[]{"http://localhost:5500", "http://127.0.0.1:5500"};
        }
        return Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .filter(origin -> !origin.isEmpty())
                .toArray(String[]::new);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(parseAllowedOrigins())
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(false);
    }
}
