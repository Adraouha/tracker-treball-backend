package com.trackertreball.model;

import jakarta.persistence.*;

/**
 * Entidad que modela un proyecto de ingenieria y el seguimiento operativo de sus Sprints.
 * <p>
 * Facilita el computo de imputacion horaria del equipo asignado, porcentaje de avance
 * del Sprint activo y estado actual de ejecucion (IN_PROGRESS, COMPLETED, ON_HOLD).
 * </p>
 *
 * @author Hassan Adraou
 * @version 1.0.0
 * @since 2026-11-02
 */
@Entity
@Table(name = "projects")
public class Project {

    /**
     * Identificador unico del proyecto. Clave primaria autoincremental.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre comercial o tecnico del proyecto (ej. App Tracker Treball, Plataforma Osona Tech).
     */
    @Column(nullable = false, length = 120)
    private String name;

    /**
     * Estado del proyecto: IN_PROGRESS, COMPLETED, REVIEW, ON_HOLD.
     */
    @Column(nullable = false, length = 40)
    private String status = "IN_PROGRESS";

    /**
     * Porcentaje numerico de avance del Sprint actual (0 a 100).
     */
    @Column(name = "sprint_progress", nullable = false)
    private Integer sprintProgress = 0;

    /**
     * Numero de desarrolladores o miembros del equipo asignados al proyecto.
     */
    @Column(name = "team_members", nullable = false)
    private Integer teamMembers = 1;

    /**
     * Total de horas efectivas dedicadas e imputadas al proyecto.
     */
    @Column(name = "dedicated_hours", nullable = false)
    private Integer dedicatedHours = 0;

    /**
     * Constructor por defecto para JPA.
     */
    public Project() {}

    /**
     * Constructor completo para inicializar proyectos.
     *
     * @param name Nombre identificativo del proyecto.
     * @param status Estado de avance.
     * @param sprintProgress Porcentaje de sprint (0-100).
     * @param teamMembers Miembros del equipo.
     * @param dedicatedHours Horas invertidas.
     */
    public Project(String name, String status, Integer sprintProgress, Integer teamMembers, Integer dedicatedHours) {
        this.name = name;
        this.status = status;
        this.sprintProgress = sprintProgress;
        this.teamMembers = teamMembers;
        this.dedicatedHours = dedicatedHours;
    }

    /**
     * Obtiene el identificador unico del proyecto.
     * @return Identificador Long.
     */
    public Long getId() { return id; }

    /**
     * Asigna el identificador unico del proyecto.
     * @param id Identificador Long.
     */
    public void setId(Long id) { this.id = id; }

    /**
     * Obtiene el nombre del proyecto.
     * @return Nombre del proyecto.
     */
    public String getName() { return name; }

    /**
     * Asigna el nombre del proyecto.
     * @param name Nombre del proyecto.
     */
    public void setName(String name) { this.name = name; }

    /**
     * Obtiene el estado actual del proyecto.
     * @return Estado del proyecto.
     */
    public String getStatus() { return status; }

    /**
     * Asigna el estado actual del proyecto.
     * @param status Estado del proyecto.
     */
    public void setStatus(String status) { this.status = status; }

    /**
     * Obtiene el porcentaje de avance del sprint.
     * @return Porcentaje entero (0-100).
     */
    public Integer getSprintProgress() { return sprintProgress; }

    /**
     * Asigna el porcentaje de avance del sprint.
     * @param sprintProgress Porcentaje entero (0-100).
     */
    public void setSprintProgress(Integer sprintProgress) { this.sprintProgress = sprintProgress; }

    /**
     * Obtiene el recuento de miembros asignados.
     * @return Cantidad de profesionales.
     */
    public Integer getTeamMembers() { return teamMembers; }

    /**
     * Asigna el recuento de miembros asignados.
     * @param teamMembers Cantidad de profesionales.
     */
    public void setTeamMembers(Integer teamMembers) { this.teamMembers = teamMembers; }

    /**
     * Obtiene el total de horas imputadas.
     * @return Horas dedicadas.
     */
    public Integer getDedicatedHours() { return dedicatedHours; }

    /**
     * Asigna el total de horas imputadas.
     * @param dedicatedHours Horas dedicadas.
     */
    public void setDedicatedHours(Integer dedicatedHours) { this.dedicatedHours = dedicatedHours; }
}
