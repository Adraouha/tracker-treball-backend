package com.trackertreball.controller;

import com.trackertreball.model.VacationRequest;
import com.trackertreball.service.VacationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controlador REST para la gestion, consulta y aprobacion de solicitudes de vacaciones.
 *
 * @author Hassan Adraou
 * @version 1.0.0
 * @since 2026-11-03
 */
@RestController
@RequestMapping("/api/v1/vacations")
public class VacationController {

    private final VacationService vacationService;

    public VacationController(VacationService vacationService) {
        this.vacationService = vacationService;
    }

    @GetMapping("/requests")
    public ResponseEntity<Map<String, Object>> getRequests(
            @RequestParam(defaultValue = "1") Long employeeId,
            @RequestHeader(value = "X-User-Role", required = false) String callerRole) {
        return ResponseEntity.ok(vacationService.getVacationsOverview(employeeId, callerRole));
    }

    @PostMapping("/requests")
    public ResponseEntity<Map<String, Object>> createRequest(
            @RequestParam(required = false) Long employeeId,
            @RequestBody Map<String, Object> body) {
        Long resolvedEmpId = employeeId;
        if (resolvedEmpId == null && body.containsKey("employeeId")) {
            resolvedEmpId = Long.parseLong(body.get("employeeId").toString());
        }
        if (resolvedEmpId == null) resolvedEmpId = 1L;

        VacationRequest saved = vacationService.createRequest(resolvedEmpId, body);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "status", "SUCCESS",
                "request", saved,
                "message", "Solicitud de vacaciones registrada con éxito."
        ));
    }

    @PutMapping("/requests/{id}")
    public ResponseEntity<Map<String, Object>> updateRequest(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body) {
        VacationRequest updated = vacationService.updateRequest(id, body);
        return ResponseEntity.ok(Map.of(
                "status", "SUCCESS",
                "request", updated,
                "message", "Solicitud de vacaciones actualizada con éxito."
        ));
    }

    @PostMapping("/requests/{id}/review")
    public ResponseEntity<Map<String, Object>> reviewRequest(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body) {
        String action = body.getOrDefault("action", "accept").toString();
        String note = (String) body.get("note");
        String reviewerName = (String) body.getOrDefault("reviewerName", "Administrador / Supervisor");

        VacationRequest reviewed = vacationService.reviewRequest(id, action, note, reviewerName);
        return ResponseEntity.ok(Map.of(
                "status", "SUCCESS",
                "request", reviewed,
                "message", "Solicitud de vacaciones revisada correctamente."
        ));
    }
}

