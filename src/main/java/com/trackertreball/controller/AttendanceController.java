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
 * Controlador REST para la gestion del registro de jornada laboral obligatoria y pausas.
 * <p>
 * Expone los endpoints publicos bajo la ruta {@code /api/v1/attendance}:
 * <ul>
 *   <li><b>GET /today:</b> Consulta el estado de la jornada del dia para el empleado.</li>
 *   <li><b>POST /clock-in:</b> Realiza el fichaje de entrada y apertura de jornada.</li>
 *   <li><b>POST /toggle-break:</b> Pausa o reanuda la jornada (cafe, comida, descanso visual PRL).</li>
 *   <li><b>POST /clock-out:</b> Cierra la jornada laboral y genera el hash criptografico SHA-256.</li>
 * </ul>
 * </p>
 *
 * @author Hassan Adraou
 * @version 1.0.0
 * @since 2026-11-02
 * @see com.trackertreball.service.AttendanceService
 * @see com.trackertreball.model.AttendanceRecord
 */
@RestController
@RequestMapping("/api/v1/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;

    /**
     * Inyecta el servicio de control horario y pausas.
     *
     * @param attendanceService Servicio de logica de asistencia.
     */
    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    /**
     * Endpoint para obtener el estado de la jornada de hoy de un empleado.
     * <p>
     * <b>Ruta HTTP:</b> {@code GET /api/v1/attendance/today?employeeId={id}}
     * </p>
     *
     * @param employeeId Identificador del empleado (por defecto 1).
     * @return {@link ResponseEntity} con el {@link AttendanceRecord} si existe o un mapa con estado NO_CLOCK_IN_TODAY.
     */
    @GetMapping("/today")
    public ResponseEntity<?> getToday(@RequestParam(defaultValue = "1") Long employeeId) {
        Optional<AttendanceRecord> record = attendanceService.getTodayAttendance(employeeId);
        if (record.isPresent()) {
            return ResponseEntity.ok(record.get());
        }
        return ResponseEntity.ok(Map.of("status", "NO_CLOCK_IN_TODAY"));
    }

    /**
     * Endpoint para registrar el fichaje de entrada a la jornada de trabajo.
     * <p>
     * <b>Ruta HTTP:</b> {@code POST /api/v1/attendance/clock-in?employeeId={id}&modality={mod}}
     * </p>
     *
     * @param employeeId ID del empleado que ficha entrada.
     * @param modality Modalidad de fichaje (MANUAL, BIOMETRIC, REMOTE).
     * @return {@link ResponseEntity} con codigo HTTP 201 Created y el nuevo registro de jornada creado.
     */
    @PostMapping("/clock-in")
    public ResponseEntity<AttendanceRecord> clockIn(
            @RequestParam(defaultValue = "1") Long employeeId,
            @RequestParam(defaultValue = "MANUAL") String modality) {
        AttendanceRecord record = attendanceService.clockIn(employeeId, modality);
        return ResponseEntity.status(HttpStatus.CREATED).body(record);
    }

    /**
     * Endpoint para alternar entre iniciar y reanudar pausas y descansos.
     * <p>
     * <b>Ruta HTTP:</b> {@code POST /api/v1/attendance/toggle-break?employeeId={id}&breakType={tipo}}
     * </p>
     *
     * @param employeeId ID del empleado.
     * @param breakType Tipo de pausa (PAUSA, COMIDA, PRL_VISUAL).
     * @return {@link ResponseEntity} con codigo HTTP 200 OK y el registro de la pausa procesada.
     */
    @PostMapping("/toggle-break")
    public ResponseEntity<BreakRecord> toggleBreak(
            @RequestParam(defaultValue = "1") Long employeeId,
            @RequestParam(defaultValue = "PAUSA") String breakType) {
        BreakRecord record = attendanceService.toggleBreak(employeeId, breakType);
        return ResponseEntity.ok(record);
    }

    /**
     * Endpoint para registrar el fichaje de salida y clausura de la jornada diaria.
     * <p>
     * <b>Ruta HTTP:</b> {@code POST /api/v1/attendance/clock-out?employeeId={id}}
     * </p>
     *
     * @param employeeId ID del empleado que finaliza la jornada.
     * @return {@link ResponseEntity} con codigo HTTP 200 OK, tiempo total computado y hash SHA-256.
     */
    @PostMapping("/clock-out")
    public ResponseEntity<AttendanceRecord> clockOut(
            @RequestParam(defaultValue = "1") Long employeeId) {
        AttendanceRecord record = attendanceService.clockOut(employeeId);
        return ResponseEntity.ok(record);
    }
}
