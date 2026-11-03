package com.trackertreball.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad que registra los incidentes de seguridad laboral y condiciones peligrosas notificadas.
 * <p>
 * Permite documentar con total trazabilidad situaciones de riesgo potencial (Near Misses),
 * accidentes leves o necesidades de adaptacion ergonomica reportadas por los empleados,
 * facilitando la respuesta inmediata por parte del comite de seguridad y salud de la empresa.
 * </p>
 *
 * @author Hassan Adraou
 * @version 1.0.0
 * @since 2026-11-02
 */
@Entity
@Table(name = "work_incidents")
public class WorkIncident {

    /**
     * Identificador unico del incidente registrado. Clave primaria autoincremental.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Identificador del empleado que reporto la condicion o incidente.
     */
    @Column(name = "reporter_employee_id", nullable = false)
    private Long reporterEmployeeId;

    /**
     * Marca temporal exacta en que se registro la notificacion.
     */
    @Column(name = "incident_date", nullable = false)
    private LocalDateTime incidentDate;

    /**
     * Ubicacion fisica donde se detecto el riesgo (ej. Escalera Planta 1, Sala de Servidores).
     */
    @Column(nullable = false, length = 150)
    private String location;

    /**
     * Descripcion exhaustiva de la situacion de peligro o incidente observado.
     */
    @Column(name = "hazard_description", nullable = false, columnDefinition = "TEXT")
    private String hazardDescription;

    /**
     * Accion preventiva o correctiva sugerida por el informador.
     */
    @Column(name = "suggested_action", columnDefinition = "TEXT")
    private String suggestedAction;

    /**
     * Estado del incidente: REPORTED, IN_REVIEW, RESOLVED, DISMISSED.
     */
    @Column(nullable = false, length = 30)
    private String status = "REPORTED";

    /**
     * Constructor por defecto para JPA.
     */
    public WorkIncident() {}

    /**
     * Constructor completo para registrar nuevos incidentes de seguridad.
     *
     * @param reporterEmployeeId ID del empleado que emite el reporte.
     * @param incidentDate Fecha y hora del suceso.
     * @param location Ubicacion o puesto de trabajo.
     * @param hazardDescription Descripcion del peligro.
     * @param suggestedAction Medida preventiva propuesta.
     */
    public WorkIncident(Long reporterEmployeeId, LocalDateTime incidentDate, String location, String hazardDescription, String suggestedAction) {
        this.reporterEmployeeId = reporterEmployeeId;
        this.incidentDate = incidentDate;
        this.location = location;
        this.hazardDescription = hazardDescription;
        this.suggestedAction = suggestedAction;
        this.status = "REPORTED";
    }

    /**
     * Obtiene el identificador del incidente.
     * @return Identificador Long.
     */
    public Long getId() { return id; }

    /**
     * Asigna el identificador del incidente.
     * @param id Identificador Long.
     */
    public void setId(Long id) { this.id = id; }

    /**
     * Obtiene el ID del empleado informador.
     * @return ID del empleado.
     */
    public Long getReporterEmployeeId() { return reporterEmployeeId; }

    /**
     * Asigna el ID del empleado informador.
     * @param reporterEmployeeId ID del empleado.
     */
    public void setReporterEmployeeId(Long reporterEmployeeId) { this.reporterEmployeeId = reporterEmployeeId; }

    /**
     * Obtiene la marca temporal del incidente.
     * @return Fecha y hora LocalDateTime.
     */
    public LocalDateTime getIncidentDate() { return incidentDate; }

    /**
     * Asigna la marca temporal del incidente.
     * @param incidentDate Fecha y hora LocalDateTime.
     */
    public void setIncidentDate(LocalDateTime incidentDate) { this.incidentDate = incidentDate; }

    /**
     * Obtiene la ubicacion reportada.
     * @return Ubicacion fisica.
     */
    public String getLocation() { return location; }

    /**
     * Asigna la ubicacion reportada.
     * @param location Ubicacion fisica.
     */
    public void setLocation(String location) { this.location = location; }

    /**
     * Obtiene la descripcion del peligro.
     * @return Descripcion textual.
     */
    public String getHazardDescription() { return hazardDescription; }

    /**
     * Asigna la descripcion del peligro.
     * @param hazardDescription Descripcion textual.
     */
    public void setHazardDescription(String hazardDescription) { this.hazardDescription = hazardDescription; }

    /**
     * Obtiene la accion correctiva sugerida.
     * @return Accion recomendada.
     */
    public String getSuggestedAction() { return suggestedAction; }

    /**
     * Asigna la accion correctiva sugerida.
     * @param suggestedAction Accion recomendada.
     */
    public void setSuggestedAction(String suggestedAction) { this.suggestedAction = suggestedAction; }

    /**
     * Obtiene el estado actual de gestion del incidente.
     * @return Estado (REPORTED, IN_REVIEW, RESOLVED).
     */
    public String getStatus() { return status; }

    /**
     * Asigna el estado actual de gestion del incidente.
     * @param status Estado (REPORTED, IN_REVIEW, RESOLVED).
     */
    public void setStatus(String status) { this.status = status; }
}
