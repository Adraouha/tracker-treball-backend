package com.trackertreball.model;

import jakarta.persistence.*;

/**
 * Entidad JPA que representa a un empleado dentro de la organizacion en Tracker Treball.
 * <p>
 * Almacena los datos de identificacion personal, correo electronico corporativo, departamento,
 * cargo o posicion funcional, centro de trabajo asignado y estado de activacion en el sistema.
 * Mapeada a la tabla {@code employees} de la base de datos relacional.
 * </p>
 *
 * @author Hassan Adraou
 * @version 1.0.0
 * @since 2026-11-02
 */
@Entity
@Table(name = "employees")
public class Employee {

    /**
     * Identificador unico del empleado en la base de datos.
     * Clave primaria autoincremental.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre completo y apellidos del empleado.
     */
    @Column(nullable = false, length = 120)
    private String nombre;

    /**
     * Direccion de correo electronico corporativa unica del empleado.
     */
    @Column(nullable = false, unique = true, length = 120)
    private String email;

    /**
     * Departamento al que pertenece el empleado (ej. Tecnologia, Personas, Administracion, Operaciones).
     */
    @Column(nullable = false, length = 80)
    private String departamento;

    /**
     * Cargo o puesto de trabajo asignado (ej. Desarrollador Senior, Responsable de RRHH).
     */
    @Column(nullable = false, length = 80)
    private String puesto;

    /**
     * Centro de trabajo fisico asignado (ej. Vic Central, Oficina A).
     */
    @Column(name = "centro_trabajo", nullable = false, length = 100)
    private String centroTrabajo;

    /**
     * Indicador de estado activo o de baja del empleado en la organizacion.
     */
    @Column(nullable = false)
    private boolean activo = true;

    /**
     * Constructor por defecto requerido por JPA / Hibernate.
     */
    public Employee() {}

    /**
     * Constructor parametrizado para instanciar nuevos empleados con sus atributos requeridos.
     *
     * @param nombre Nombre y apellidos completos del empleado.
     * @param email Correo electronico corporativo unico.
     * @param departamento Nombre del departamento corporativo.
     * @param puesto Cargo laboral o rol del empleado.
     * @param centroTrabajo Nombre del centro de trabajo o edificio.
     */
    public Employee(String nombre, String email, String departamento, String puesto, String centroTrabajo) {
        this.nombre = nombre;
        this.email = email;
        this.departamento = departamento;
        this.puesto = puesto;
        this.centroTrabajo = centroTrabajo;
        this.activo = true;
    }

    /**
     * Obtiene el identificador unico del empleado.
     * @return Identificador Long.
     */
    public Long getId() { return id; }

    /**
     * Asigna el identificador unico del empleado.
     * @param id Identificador Long.
     */
    public void setId(Long id) { this.id = id; }

    /**
     * Obtiene el nombre completo del empleado.
     * @return Nombre y apellidos.
     */
    public String getNombre() { return nombre; }

    /**
     * Asigna el nombre completo del empleado.
     * @param nombre Nombre y apellidos.
     */
    public void setNombre(String nombre) { this.nombre = nombre; }

    /**
     * Obtiene el correo corporativo del empleado.
     * @return Correo electronico.
     */
    public String getEmail() { return email; }

    /**
     * Asigna el correo corporativo del empleado.
     * @param email Correo electronico.
     */
    public void setEmail(String email) { this.email = email; }

    /**
     * Obtiene el departamento del empleado.
     * @return Nombre del departamento.
     */
    public String getDepartamento() { return departamento; }

    /**
     * Asigna el departamento del empleado.
     * @param departamento Nombre del departamento.
     */
    public void setDepartamento(String departamento) { this.departamento = departamento; }

    /**
     * Obtiene el puesto o cargo funcional.
     * @return Cargo laboral.
     */
    public String getPuesto() { return puesto; }

    /**
     * Asigna el puesto o cargo funcional.
     * @param puesto Cargo laboral.
     */
    public void setPuesto(String puesto) { this.puesto = puesto; }

    /**
     * Obtiene el centro de trabajo asignado.
     * @return Centro de trabajo.
     */
    public String getCentroTrabajo() { return centroTrabajo; }

    /**
     * Asigna el centro de trabajo asignado.
     * @param centroTrabajo Centro de trabajo.
     */
    public void setCentroTrabajo(String centroTrabajo) { this.centroTrabajo = centroTrabajo; }

    /**
     * Indica si el empleado se encuentra activo en la organizacion.
     * @return {@code true} si esta activo; {@code false} si esta archivado.
     */
    public boolean isActivo() { return activo; }

    /**
     * Modifica el estado activo del empleado.
     * @param activo Estado activo.
     */
    public void setActivo(boolean activo) { this.activo = activo; }
}
