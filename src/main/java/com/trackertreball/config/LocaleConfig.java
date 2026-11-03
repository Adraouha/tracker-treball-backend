package com.trackertreball.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.Arrays;
import java.util.Locale;

/**
 * Configuracion de internacionalizacion y resolucion de idioma para Tracker Treball.
 * <p>
 * Gestiona el soporte bilingue oficial de la plataforma:
 * <ul>
 *   <li><b>Catalan ({@code ca}):</b> Idioma predeterminado del sistema de gestion laboral en Vic / Catalunya.</li>
 *   <li><b>Castellano ({@code es}):</b> Idioma oficial con soporte completo en toda la interfaz y modelos de datos.</li>
 * </ul>
 * </p>
 * <p>
 * Utiliza {@link AcceptHeaderLocaleResolver} para determinar el idioma a traves de la cabecera
 * HTTP estandar {@code Accept-Language} enviada por el navegador o cliente API.
 * </p>
 *
 * @author Hassan Adraou
 * @version 1.0.0
 * @since 2026-11-02
 * @see org.springframework.web.servlet.LocaleResolver
 * @see org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver
 */
@Configuration
public class LocaleConfig {

    /**
     * Declara el bean {@link LocaleResolver} que define la configuracion regional de la aplicacion.
     * <p>
     * Establece {@code ca} (Catalan) como idioma por defecto cuando la peticion no especifique cabecera
     * o contenga un valor no admitido, y registra {@code ca} y {@code es} como idiomas soportados.
     * </p>
     *
     * @return Instancia configurada de {@link AcceptHeaderLocaleResolver}.
     */
    @Bean
    public LocaleResolver localeResolver() {
        AcceptHeaderLocaleResolver resolver = new AcceptHeaderLocaleResolver();
        resolver.setDefaultLocale(new Locale("ca"));
        resolver.setSupportedLocales(Arrays.asList(
            new Locale("ca"),
            new Locale("es")
        ));
        return resolver;
    }
}
