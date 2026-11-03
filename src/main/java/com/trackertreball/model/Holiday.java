package com.trackertreball.model;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * Entidad JPA que representa los festivos y calendarios laborales por centro de trabajo.
 *
 * @author Hassan Adraou
 * @version 1.0.0
 * @since 2026-11-03
 */
@Entity
@Table(name = "work_center_holidays")
public class Holiday {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "work_center_id")
    private Long workCenterId;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(nullable = false, length = 40)
    private String tipo; // NACIONAL, AUTONOMICO, LOCAL

    @Column(nullable = false)
    private Integer ano = 2026;

    public Holiday() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getWorkCenterId() { return workCenterId; }
    public void setWorkCenterId(Long workCenterId) { this.workCenterId = workCenterId; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public Integer getAno() { return ano; }
    public void setAno(Integer ano) { this.ano = ano; }
}

