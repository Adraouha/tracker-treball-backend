package com.trackertreball.controller;

import com.trackertreball.model.Employee;
import com.trackertreball.repository.EmployeeRepository;
import com.trackertreball.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST para la administracion y consulta del directorio de empleados.
 * Integra la generacion automatica de credenciales seguras de acceso.
 *
 * @author Hassan Adraou
 * @version 2.0.0
 * @since 2026-11-02
 */
@RestController
@RequestMapping("/api/v1/employees")
public class EmployeeController {

    private final EmployeeRepository employeeRepository;
    private final AuthService authService;

    public EmployeeController(EmployeeRepository employeeRepository, AuthService authService) {
        this.employeeRepository = employeeRepository;
        this.authService = authService;
    }

    @GetMapping
    public ResponseEntity<List<Employee>> getAllEmployees() {
        return ResponseEntity.ok(employeeRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable Long id) {
        return employeeRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createEmployee(@RequestBody Employee employee) {
        Employee saved = employeeRepository.save(employee);
        String generatedPassword = authService.createCredentialsForEmployee(saved);

        Map<String, Object> response = new HashMap<>();
        response.put("employee", saved);
        response.put("generatedPassword", generatedPassword);
        response.put("message", "Empleado registrado correctamente con credenciales generadas.");

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Employee> updateEmployee(@PathVariable Long id, @RequestBody Employee updated) {
        return employeeRepository.findById(id).map(emp -> {
            if (updated.getNombre() != null) emp.setNombre(updated.getNombre());
            if (updated.getEmail() != null) emp.setEmail(updated.getEmail());
            if (updated.getDepartamento() != null) emp.setDepartamento(updated.getDepartamento());
            if (updated.getPuesto() != null) emp.setPuesto(updated.getPuesto());
            if (updated.getCentroTrabajo() != null) emp.setCentroTrabajo(updated.getCentroTrabajo());
            if (updated.getWorkCenterId() != null) emp.setWorkCenterId(updated.getWorkCenterId());
            if (updated.getNss() != null) emp.setNss(updated.getNss());
            if (updated.getEstadoCivil() != null) emp.setEstadoCivil(updated.getEstadoCivil());
            if (updated.getDomicilio() != null) emp.setDomicilio(updated.getDomicilio());
            if (updated.getFechaNacimiento() != null) emp.setFechaNacimiento(updated.getFechaNacimiento());
            if (updated.getTelefono() != null) emp.setTelefono(updated.getTelefono());
            emp.setActivo(updated.isActivo());
            return ResponseEntity.ok(employeeRepository.save(emp));
        }).orElse(ResponseEntity.notFound().build());
    }
}
