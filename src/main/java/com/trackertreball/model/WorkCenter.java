package com.trackertreball.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad JPA que representa un Centro de Trabajo oficial en Tracker Treball.
 *
 * @author Hassan Adraou
 * @version 2.0.0
 * @since 2026-11-03
 */
@Entity
@Table(name = "work_centers")
public class WorkCenter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 80)
    private String nombre;

    @Column(nullable = false, length = 80)
    private String ciudad;

    @Column(nullable = false, length = 80)
    private String provincia;

    @Column(name = "comunidad_autonoma", nullable = false, length = 80)
    private String comunidadAutonoma;

    @Column(name = "zona_geografica", nullable = false, length = 80)
    private String zonaGeografica;

    @Column(nullable = false, length = 160)
    private String direccion;

    @Column(name = "codigo_centro", length = 20)
    private String codigoCentro = "CT-01";

    @Column(length = 30)
    private String telefono = "+34 93 456 78 90";

    @Column(length = 80)
    private String horario = "Lunes a Viernes 08:00 - 18:00";

    @Column(nullable = false, precision = 9, scale = 6)
    private BigDecimal latitud;

    @Column(nullable = false, precision = 9, scale = 6)
    private BigDecimal longitud;

    @Column(nullable = false)
    private boolean activo = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public WorkCenter() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    public String getProvincia() { return provincia; }
    public void setProvincia(String provincia) { this.provincia = provincia; }

    public String getComunidadAutonoma() { return comunidadAutonoma; }
    public void setComunidadAutonoma(String comunidadAutonoma) { this.comunidadAutonoma = comunidadAutonoma; }

    public String getZonaGeografica() { return zonaGeografica; }
    public void setZonaGeografica(String zonaGeografica) { this.zonaGeografica = zonaGeografica; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getCodigoCentro() { return codigoCentro; }
    public void setCodigoCentro(String codigoCentro) { this.codigoCentro = codigoCentro; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getHorario() { return horario; }
    public void setHorario(String horario) { this.horario = horario; }

    public BigDecimal getLatitud() { return latitud; }
    public void setLatitud(BigDecimal latitud) { this.latitud = latitud; }

    public BigDecimal getLongitud() { return longitud; }
    public void setLongitud(BigDecimal longitud) { this.longitud = longitud; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
