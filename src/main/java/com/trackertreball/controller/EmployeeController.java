package com.trackertreball.controller;

import com.trackertreball.model.Employee;
import com.trackertreball.repository.EmployeeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la administracion y consulta del directorio de empleados.
 * <p>
 * Proporciona acceso al censo corporativo de la organizacion bajo la ruta base {@code /api/v1/employees}:
 * <ul>
 *   <li><b>GET /:</b> Recupera el listado completo de los 21 empleados de la organizacion.</li>
 *   <li><b>GET /{id}:</b> Obtiene los datos detallados de la ficha individual de un empleado.</li>
 *   <li><b>POST /:</b> Da de alta a un nuevo empleado en el sistema.</li>
 * </ul>
 * </p>
 *
 * @author Hassan Adraou
 * @version 1.0.0
 * @since 2026-11-02
 * @see com.trackertreball.model.Employee
 * @see com.trackertreball.repository.EmployeeRepository
 */
@RestController
@RequestMapping("/api/v1/employees")
public class EmployeeController {

    private final EmployeeRepository employeeRepository;

    /**
     * Inyecta el repositorio de empleados para operaciones de lectura y persistencia.
     *
     * @param employeeRepository Repositorio JPA de empleados.
     */
    public EmployeeController(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    /**
     * Endpoint para consultar la lista completa de empleados registrados.
     * <p>
     * <b>Ruta HTTP:</b> {@code GET /api/v1/employees}
     * </p>
     *
     * @return {@link ResponseEntity} con codigo HTTP 200 OK y la lista de todos los empleados {@link Employee}.
     */
    @GetMapping
    public ResponseEntity<List<Employee>> getAllEmployees() {
        return ResponseEntity.ok(employeeRepository.findAll());
    }

    /**
     * Endpoint para consultar la ficha de un empleado especifico por su identificador unico.
     * <p>
     * <b>Ruta HTTP:</b> {@code GET /api/v1/employees/{id}}
     * </p>
     *
     * @param id Identificador unico del empleado.
     * @return {@link ResponseEntity} con el empleado si existe (200 OK) o 404 Not Found si no se localiza.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable Long id) {
        return employeeRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Endpoint para registrar un nuevo empleado en la organizacion.
     * <p>
     * <b>Ruta HTTP:</b> {@code POST /api/v1/employees}
     * </p>
     *
     * @param employee Objeto con los datos del nuevo empleado en formato JSON.
     * @return {@link ResponseEntity} con codigo HTTP 201 Created y el nuevo empleado persistido.
     */
    @PostMapping
    public ResponseEntity<Employee> createEmployee(@RequestBody Employee employee) {
        Employee saved = employeeRepository.save(employee);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
}
