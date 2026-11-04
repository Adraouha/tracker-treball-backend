package com.trackertreball.model;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * Entidad JPA que representa a un empleado dentro de la organizacion en Tracker Treball.
 * <p>
 * Almacena los datos de identificacion personal, correo electronico corporativo, departamento,
 * cargo o posicion funcional, centro de trabajo asignado, NSS, estado civil, domicilio,
 * fecha de nacimiento y contacto.
 * Mapeada a la tabla {@code employees} de la base de datos relacional.
 * </p>
 *
 * @author Hassan Adraou
 * @version 2.0.0
 * @since 2026-11-02
 */
@Entity
@Table(name = "employees")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(nullable = false, unique = true, length = 120)
    private String email;

    @Column(nullable = false, length = 80)
    private String departamento;

    @Column(nullable = false, length = 80)
    private String puesto;

    @Column(name = "centro_trabajo", nullable = false, length = 100)
    private String centroTrabajo = "Barcelona";

    @Column(name = "work_center_id")
    private Long workCenterId;

    @Column(length = 30)
    private String nss;

    @Column(name = "estado_civil", length = 30)
    private String estadoCivil = "Soltero/a";

    @Column(length = 180)
    private String domicilio;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @Column(length = 30)
    private String telefono;

    @Column(length = 20, nullable = false)
    private String rol = "EMPLOYEE";

    @Column(nullable = false)
    private boolean activo = true;

    public Employee() {}

    public Employee(String nombre, String email, String departamento, String puesto, String centroTrabajo) {
        this.nombre = nombre;
        this.email = email;
        this.departamento = departamento;
        this.puesto = puesto;
        this.centroTrabajo = centroTrabajo;
        this.activo = true;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getDepartamento() { return departamento; }
    public void setDepartamento(String departamento) { this.departamento = departamento; }

    public String getPuesto() { return puesto; }
    public void setPuesto(String puesto) { this.puesto = puesto; }

    public String getCentroTrabajo() { return centroTrabajo; }
    public void setCentroTrabajo(String centroTrabajo) { this.centroTrabajo = centroTrabajo; }

    public Long getWorkCenterId() { return workCenterId; }
    public void setWorkCenterId(Long workCenterId) { this.workCenterId = workCenterId; }

    public String getNss() { return nss; }
    public void setNss(String nss) { this.nss = nss; }

    public String getEstadoCivil() { return estadoCivil; }
    public void setEstadoCivil(String estadoCivil) { this.estadoCivil = estadoCivil; }

    public String getDomicilio() { return domicilio; }
    public void setDomicilio(String domicilio) { this.domicilio = domicilio; }

    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}
