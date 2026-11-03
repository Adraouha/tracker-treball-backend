package com.trackertreball.repository;

import com.trackertreball.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio de acceso a datos para la entidad {@link Employee}.
 * <p>
 * Proporciona operaciones CRUD estandar y consultas derivadas de Spring Data JPA
 * para la gestion de altas, bajas, consultas por departamento y busquedas por correo electronico.
 * </p>
 *
 * @author Hassan Adraou
 * @version 1.0.0
 * @since 2026-11-02
 * @see com.trackertreball.model.Employee
 * @see org.springframework.data.jpa.repository.JpaRepository
 */
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    /**
     * Localiza un empleado a partir de su correo electronico corporativo unico.
     *
     * @param email Direccion de correo electronico corporativa (ej. nombre.apellidos@test.es).
     * @return {@link Optional} que contiene el empleado si existe, o vacio si no se encuentra.
     */
    Optional<Employee> findByEmail(String email);

    Optional<Employee> findByEmailIgnoreCase(String email);

    /**
     * Obtiene el listado de todos los empleados que pertenecen a un departamento especifico.
     *
     * @param departamento Nombre del departamento (Tecnologia, Personas, Administracion, Operaciones).
     * @return Lista de empleados pertenecientes a dicho departamento.
     */
    List<Employee> findByDepartamento(String departamento);

    /**
     * Recupera todos los empleados segun su estado de activacion en el sistema.
     *
     * @param activo {@code true} para recuperar empleados en plantilla activa; {@code false} para historicos.
     * @return Lista de empleados que coinciden con el estado especificado.
     */
    List<Employee> findByActivo(boolean activo);
}
