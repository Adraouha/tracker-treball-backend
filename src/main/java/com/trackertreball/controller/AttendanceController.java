package com.trackertreball.controller;

import com.trackertreball.model.AttendanceRecord;
import com.trackertreball.model.BreakRecord;
import com.trackertreball.service.AttendanceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

/**
 * Controlador REST para la gestion del registro de jornada laboral obligatoria, pausas y auditorias.
 * Soporta parametros tanto por QueryParams como por RequestBody JSON.
 *
 * @author Hassan Adraou
 * @version 2.1.0
 * @since 2026-11-02
 */
@RestController
@RequestMapping("/api/v1/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @GetMapping("/today")
    public ResponseEntity<?> getToday(@RequestParam(defaultValue = "1") Long employeeId) {
        Optional<AttendanceRecord> record = attendanceService.getTodayAttendance(employeeId);
        if (record.isPresent()) {
            return ResponseEntity.ok(record.get());
        }
        return ResponseEntity.ok(Map.of("status", "NO_CLOCK_IN_TODAY"));
    }

    @PostMapping("/clock-in")
    public ResponseEntity<AttendanceRecord> clockIn(
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) String modality,
            @RequestBody(required = false) Map<String, Object> body) {
        Long resolvedEmpId = resolveEmployeeId(employeeId, body);
        String resolvedModality = modality != null ? modality :
                (body != null && body.containsKey("modality") ? body.get("modality").toString() : "PRESENCIAL");

        AttendanceRecord record = attendanceService.clockIn(resolvedEmpId, resolvedModality);
        return ResponseEntity.status(HttpStatus.CREATED).body(record);
    }

    @PostMapping("/toggle-break")
    public ResponseEntity<BreakRecord> toggleBreak(
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) String breakType,
            @RequestBody(required = false) Map<String, Object> body) {
        Long resolvedEmpId = resolveEmployeeId(employeeId, body);
        String resolvedType = breakType != null ? breakType :
                (body != null && body.containsKey("breakType") ? body.get("breakType").toString() : "PAUSA");

        BreakRecord record = attendanceService.toggleBreak(resolvedEmpId, resolvedType);
        return ResponseEntity.ok(record);
    }

    @PostMapping("/break/start")
    public ResponseEntity<BreakRecord> startBreak(
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) String breakType,
            @RequestParam(required = false) Integer minutes,
            @RequestBody(required = false) Map<String, Object> body) {
        Long resolvedEmpId = resolveEmployeeId(employeeId, body);
        String resolvedType = breakType != null ? breakType :
                (body != null && body.containsKey("breakType") ? body.get("breakType").toString() : "PAUSA");
        Integer resolvedMinutes = minutes != null ? minutes :
                (body != null && body.containsKey("minutes") ? Integer.valueOf(body.get("minutes").toString()) : 15);

        BreakRecord record = attendanceService.startBreak(resolvedEmpId, resolvedType, resolvedMinutes);
        return ResponseEntity.ok(record);
    }

    @PostMapping("/break/finish")
    public ResponseEntity<BreakRecord> finishBreak(
            @RequestParam(required = false) Long employeeId,
            @RequestBody(required = false) Map<String, Object> body) {
        Long resolvedEmpId = resolveEmployeeId(employeeId, body);
        BreakRecord record = attendanceService.finishBreak(resolvedEmpId);
        return ResponseEntity.ok(record);
    }

    @PostMapping("/clock-out")
    public ResponseEntity<AttendanceRecord> clockOut(
            @RequestParam(required = false) Long employeeId,
            @RequestBody(required = false) Map<String, Object> body) {
        Long resolvedEmpId = resolveEmployeeId(employeeId, body);
        AttendanceRecord record = attendanceService.clockOut(resolvedEmpId);
        return ResponseEntity.ok(record);
    }

    @GetMapping("/reports/employee/{employeeId}")
    public ResponseEntity<Map<String, Object>> getReports(@PathVariable Long employeeId) {
        return ResponseEntity.ok(attendanceService.getReports(employeeId));
    }

    private Long resolveEmployeeId(Long queryParamId, Map<String, Object> body) {
        if (queryParamId != null) return queryParamId;
        if (body != null && body.containsKey("employeeId")) {
            return Long.valueOf(body.get("employeeId").toString());
        }
        return 1L;
    }
}
