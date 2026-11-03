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
 *
 * @author Hassan Adraou
 * @version 2.0.0
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
            @RequestParam(defaultValue = "1") Long employeeId,
            @RequestParam(defaultValue = "MANUAL") String modality) {
        AttendanceRecord record = attendanceService.clockIn(employeeId, modality);
        return ResponseEntity.status(HttpStatus.CREATED).body(record);
    }

    @PostMapping("/toggle-break")
    public ResponseEntity<BreakRecord> toggleBreak(
            @RequestParam(defaultValue = "1") Long employeeId,
            @RequestParam(defaultValue = "PAUSA") String breakType) {
        BreakRecord record = attendanceService.toggleBreak(employeeId, breakType);
        return ResponseEntity.ok(record);
    }

    @PostMapping("/break/start")
    public ResponseEntity<BreakRecord> startBreak(
            @RequestParam(defaultValue = "1") Long employeeId,
            @RequestParam(defaultValue = "PAUSA") String breakType,
            @RequestParam(defaultValue = "15") Integer minutes) {
        BreakRecord record = attendanceService.startBreak(employeeId, breakType, minutes);
        return ResponseEntity.ok(record);
    }

    @PostMapping("/break/finish")
    public ResponseEntity<BreakRecord> finishBreak(
            @RequestParam(defaultValue = "1") Long employeeId) {
        BreakRecord record = attendanceService.finishBreak(employeeId);
        return ResponseEntity.ok(record);
    }

    @PostMapping("/clock-out")
    public ResponseEntity<AttendanceRecord> clockOut(
            @RequestParam(defaultValue = "1") Long employeeId) {
        AttendanceRecord record = attendanceService.clockOut(employeeId);
        return ResponseEntity.ok(record);
    }

    @GetMapping("/reports/employee/{employeeId}")
    public ResponseEntity<Map<String, Object>> getReports(@PathVariable Long employeeId) {
        return ResponseEntity.ok(attendanceService.getReports(employeeId));
    }
}
