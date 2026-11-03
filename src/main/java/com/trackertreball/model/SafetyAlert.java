package com.trackertreball.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad que modela las alertas preventivas meteorologicas y de proteccion civil de PRL.
 * <p>
 * Empleada por el modulo de Prevencion de Riesgos Laborales para emitir avisos automaticos
 * (como alertas de viento, nieve o lluvia intensa en la comarca de Osona via AEMET / Meteocat)
 * que afectan al desplazamiento in itinere de los trabajadores.
 * </p>
 *
 * @author Hassan Adraou
 * @version 1.0.0
 * @since 2026-11-02
 */
@Entity
@Table(name = "safety_alerts")
public class SafetyAlert {

    /**
     * Identificador unico de la alerta. Clave primaria autoincremental.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Fuente oficial emisora de la alerta (ej. AEMET, METEOCAT, PROTECCIO_CIVIL, MUTUA).
     */
    @Column(nullable = false, length = 40)
    private String source = "AEMET";

    /**
     * Nivel de gravedad de la alerta: INFO, WARNING, SEVERE, CRITICAL.
     */
    @Column(name = "alert_level", nullable = false, length = 20)
    private String alertLevel = "WARNING";

    /**
     * Titulo descriptivo breve de la alerta preventiva.
     */
    @Column(nullable = false, length = 150)
    private String title;

    /**
     * Mensaje detallado e instrucciones preventivas recomendadas para el personal.
     */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    /**
     * Fecha y hora en que entra en vigor la alerta preventiva.
     */
    @Column(name = "effective_start", nullable = false)
    private LocalDateTime effectiveStart;

    /**
     * Fecha y hora en que expira la alerta preventiva.
     */
    @Column(name = "effective_end", nullable = false)
    private LocalDateTime effectiveEnd;

    /**
     * Indicador booleano de si la alerta se encuentra actualmente activa en el sistema.
     */
    @Column(nullable = false)
    private boolean active = true;

    /**
     * Constructor por defecto para JPA.
     */
    public SafetyAlert() {}

    /**
     * Constructor parametrizado para crear alertas de seguridad activas.
     *
     * @param source Organismo o servicio emisor.
     * @param alertLevel Nivel de riesgo (INFO, WARNING, CRITICAL).
     * @param title Titular de la alerta.
     * @param message Cuerpo con las medidas preventivas recomendadas.
     * @param effectiveStart Inicio de validez.
     * @param effectiveEnd Fin de validez.
     */
    public SafetyAlert(String source, String alertLevel, String title, String message, LocalDateTime effectiveStart, LocalDateTime effectiveEnd) {
        this.source = source;
        this.alertLevel = alertLevel;
        this.title = title;
        this.message = message;
        this.effectiveStart = effectiveStart;
        this.effectiveEnd = effectiveEnd;
        this.active = true;
    }

    /**
     * Obtiene el ID de la alerta.
     * @return Identificador Long.
     */
    public Long getId() { return id; }

    /**
     * Asigna el ID de la alerta.
     * @param id Identificador Long.
     */
    public void setId(Long id) { this.id = id; }

    /**
     * Obtiene la fuente emisora.
     * @return Cadena con la fuente.
     */
    public String getSource() { return source; }

    /**
     * Asigna la fuente emisora.
     * @param source Cadena con la fuente.
     */
    public void setSource(String source) { this.source = source; }

    /**
     * Obtiene el nivel de severidad.
     * @return Nivel de alerta.
     */
    public String getAlertLevel() { return alertLevel; }

    /**
     * Asigna el nivel de severidad.
     * @param alertLevel Nivel de alerta.
     */
    public void setAlertLevel(String alertLevel) { this.alertLevel = alertLevel; }

    /**
     * Obtiene el titulo de la alerta.
     * @return Titulo.
     */
    public String getTitle() { return title; }

    /**
     * Asigna el titulo de la alerta.
     * @param title Titulo.
     */
    public void setTitle(String title) { this.title = title; }

    /**
     * Obtiene las recomendaciones de seguridad.
     * @return Texto descriptivo.
     */
    public String getMessage() { return message; }

    /**
     * Asigna las recomendaciones de seguridad.
     * @param message Texto descriptivo.
     */
    public void setMessage(String message) { this.message = message; }

    /**
     * Obtiene el inicio de vigencia.
     * @return Marca temporal inicial.
     */
    public LocalDateTime getEffectiveStart() { return effectiveStart; }

    /**
     * Asigna el inicio de vigencia.
     * @param effectiveStart Marca temporal inicial.
     */
    public void setEffectiveStart(LocalDateTime effectiveStart) { this.effectiveStart = effectiveStart; }

    /**
     * Obtiene el fin de vigencia.
     * @return Marca temporal final.
     */
    public LocalDateTime getEffectiveEnd() { return effectiveEnd; }

    /**
     * Asigna el fin de vigencia.
     * @param effectiveEnd Marca temporal final.
     */
    public void setEffectiveEnd(LocalDateTime effectiveEnd) { this.effectiveEnd = effectiveEnd; }

    /**
     * Comprueba si la alerta esta activa.
     * @return {@code true} si esta vigente; {@code false} en caso contrario.
     */
    public boolean isActive() { return active; }

    /**
     * Cambia la vigencia de la alerta.
     * @param active Estado activo.
     */
    public void setActive(boolean active) { this.active = active; }
}
