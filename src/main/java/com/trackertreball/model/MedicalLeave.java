package com.trackertreball.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidad JPA para la tramitacion y gestion de bajas medicas de empleados.
 *
 * @author Hassan Adraou
 * @version 1.0.0
 * @since 2026-11-03
 */
@Entity
@Table(name = "medical_leaves")
public class MedicalLeave {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employee_id", nullable = false)
    private Long employeeId;

    @Column(name = "employee_name", nullable = false, length = 120)
    private String employeeName;

    @Column(nullable = false, length = 60)
    private String tipo = "Enfermedad Común";

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin_prevista")
    private LocalDate fechaFinPrevista;

    @Column(name = "fecha_alta_real")
    private LocalDate fechaAltaReal;

    @Column(name = "numero_colegiado", length = 50)
    private String numeroColegiado;

    @Column(name = "centro_salud", length = 120)
    private String centroSalud;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    @Column(nullable = false, length = 30)
    private String status = "ACTIVA"; // ACTIVA, ALTA_MEDICA, EN_REVISION

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public MedicalLeave() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDate getFechaFinPrevista() { return fechaFinPrevista; }
    public void setFechaFinPrevista(LocalDate fechaFinPrevista) { this.fechaFinPrevista = fechaFinPrevista; }

    public LocalDate getFechaAltaReal() { return fechaAltaReal; }
    public void setFechaAltaReal(LocalDate fechaAltaReal) { this.fechaAltaReal = fechaAltaReal; }

    public String getNumeroColegiado() { return numeroColegiado; }
    public void setNumeroColegiado(String numeroColegiado) { this.numeroColegiado = numeroColegiado; }

    public String getCentroSalud() { return centroSalud; }
    public void setCentroSalud(String centroSalud) { this.centroSalud = centroSalud; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

