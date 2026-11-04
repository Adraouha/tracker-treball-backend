package com.trackertreball.controller;

import com.trackertreball.model.Employee;
import com.trackertreball.model.MedicalLeave;
import com.trackertreball.repository.EmployeeRepository;
import com.trackertreball.repository.LeaveRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST para el registro y tramitacion de bajas medicas y altas.
 *
 * @author Hassan Adraou
 * @version 1.0.0
 * @since 2026-11-03
 */
@RestController
@RequestMapping("/api/v1/leaves")
public class LeaveController {

    private final LeaveRepository leaveRepository;
    private final EmployeeRepository employeeRepository;

    public LeaveController(LeaveRepository leaveRepository, EmployeeRepository employeeRepository) {
        this.leaveRepository = leaveRepository;
        this.employeeRepository = employeeRepository;
    }

    @GetMapping
    public ResponseEntity<List<MedicalLeave>> getAllLeaves() {
        return ResponseEntity.ok(leaveRepository.findAllByOrderByFechaInicioDesc());
    }

    @PostMapping
    public ResponseEntity<?> createLeave(@RequestBody Map<String, Object> body) {
        Long employeeId = Long.parseLong(body.get("employeeId").toString());
        Employee emp = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Empleado no encontrado con ID " + employeeId));

        MedicalLeave leave = new MedicalLeave();
        leave.setEmployeeId(employeeId);
        leave.setEmployeeName(emp.getNombre());
        leave.setTipo(body.getOrDefault("tipo", "Enfermedad Común").toString());
        leave.setFechaInicio(LocalDate.parse(body.get("fechaInicio").toString()));
        if (body.get("fechaFinPrevista") != null && !body.get("fechaFinPrevista").toString().isBlank()) {
            leave.setFechaFinPrevista(LocalDate.parse(body.get("fechaFinPrevista").toString()));
        }
        leave.setNumeroColegiado((String) body.get("numeroColegiado"));
        leave.setCentroSalud((String) body.get("centroSalud"));
        leave.setObservaciones((String) body.get("observaciones"));
        leave.setStatus("ACTIVA");

        MedicalLeave saved = leaveRepository.save(leave);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "status", "SUCCESS",
                "leave", saved,
                "message", "Baja médica registrada correctamente."
        ));
    }

    @PutMapping("/{id}/discharge")
    public ResponseEntity<?> markDischarge(@PathVariable Long id, @RequestBody(required = false) Map<String, Object> body) {
        return leaveRepository.findById(id).map(leave -> {
            leave.setStatus("ALTA_MEDICA");
            leave.setFechaAltaReal(LocalDate.now());
            if (body != null && body.containsKey("observaciones")) {
                leave.setObservaciones(leave.getObservaciones() + " | Alta: " + body.get("observaciones"));
            }
            MedicalLeave saved = leaveRepository.save(leave);
            return ResponseEntity.ok(Map.of(
                    "status", "SUCCESS",
                    "leave", saved,
                    "message", "Alta médica registrada correctamente."
            ));
        }).orElse(ResponseEntity.notFound().build());
    }
}

