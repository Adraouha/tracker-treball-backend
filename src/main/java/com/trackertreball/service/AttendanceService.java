package com.trackertreball.service;

import com.trackertreball.model.AttendanceRecord;
import com.trackertreball.model.BreakRecord;
import com.trackertreball.repository.AttendanceRepository;
import com.trackertreball.repository.BreakRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Servicio de logica de negocio para la gestion del control horario laboral y descansos.
 * <p>
 * Implementa las reglas operativas y de cumplimiento legal (RDL 8/2019):
 * <ul>
 *   <li><b>Inicio de jornada (Clock-in):</b> Valida que no exista una jornada abierta en el dia actual,
 *       registra la hora exacta y sella el registro con hash de integridad.</li>
 *   <li><b>Gestion de pausas (Break Toggle):</b> Permite pausar y reanudar de forma idempotente, computando
 *       la duracion exacta en minutos de cada interrupcion.</li>
 *   <li><b>Cierre de jornada (Clock-out):</b> Finaliza cualquier descanso pendiente, calcula el tiempo neto trabajado
 *       y genera una firma digital criptografica SHA-256 para auditorias de la Inspeccion de Trabajo.</li>
 * </ul>
 * </p>
 *
 * @author Hassan Adraou
 * @version 1.0.0
 * @since 2026-11-02
 * @see com.trackertreball.model.AttendanceRecord
 * @see com.trackertreball.model.BreakRecord
 * @see com.trackertreball.repository.AttendanceRepository
 */
@Service
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final BreakRepository breakRepository;

    /**
     * Constructor con inyeccion de dependencias de los repositorios de asistencia y pausas.
     *
     * @param attendanceRepository Repositorio JPA de jornadas laborales.
     * @param breakRepository Repositorio JPA de descansos y pausas.
     */
    public AttendanceService(AttendanceRepository attendanceRepository, BreakRepository breakRepository) {
        this.attendanceRepository = attendanceRepository;
        this.breakRepository = breakRepository;
    }

    /**
     * Obtiene el registro de jornada del dia de hoy para un empleado concreto.
     *
     * @param employeeId Identificador unico del empleado.
     * @return {@link Optional} que contiene la jornada activa o cerrada de hoy si existe.
     */
    public Optional<AttendanceRecord> getTodayAttendance(Long employeeId) {
        return attendanceRepository.findByEmployeeIdAndWorkDate(employeeId, LocalDate.now());
    }

    /**
     * Inicia una nueva jornada laboral diaria para el empleado (fichaje de entrada).
     * <p>
     * Comprueba primero que el empleado no tenga ya una jornada abierta. En caso satisfactorio,
     * almacena la marca temporal en el servidor y genera un hash inicial con SHA-256.
     * </p>
     *
     * @param employeeId Identificador del empleado que inicia la jornada.
     * @param modality Modalidad de fichaje utilizada (ej. MANUAL, BIOMETRIC, REMOTE).
     * @return El objeto {@link AttendanceRecord} persistido en la base de datos.
     * @throws IllegalStateException Si el empleado ya posee una jornada abierta en el dia actual.
     */
    @Transactional
    public AttendanceRecord clockIn(Long employeeId, String modality) {
        LocalDate today = LocalDate.now();
        Optional<AttendanceRecord> existing = attendanceRepository.findByEmployeeIdAndWorkDate(employeeId, today);
        if (existing.isPresent() && "OPEN".equals(existing.get().getStatus())) {
            throw new IllegalStateException("El empleado ya tiene una jornada activa.");
        }

        LocalDateTime now = LocalDateTime.now();
        AttendanceRecord record = new AttendanceRecord(employeeId, today, now, modality);
        record.setRecordHash(generateHash(employeeId, now.toString()));
        return attendanceRepository.save(record);
    }

    /**
     * Alterna el estado de descanso del empleado (iniciar o finalizar pausa).
     * <p>
     * Si el empleado tiene una pausa abierta sin hora de fin, la cierra calculando los minutos
     * transcurridos. Si no hay pausas activas, crea un nuevo registro con la hora actual.
     * </p>
     *
     * @param employeeId Identificador unico del empleado.
     * @param breakType Tipo de pausa solicitada (ej. PAUSA, COMIDA, PRL_VISUAL).
     * @return El {@link BreakRecord} actualizado o creado.
     * @throws IllegalStateException Si no existe una jornada de trabajo activa en la fecha actual.
     */
    @Transactional
    public BreakRecord toggleBreak(Long employeeId, String breakType) {
        LocalDate today = LocalDate.now();
        AttendanceRecord attendance = attendanceRepository.findByEmployeeIdAndWorkDate(employeeId, today)
                .orElseThrow(() -> new IllegalStateException("No hay jornada activa para gestionar pausas."));

        Optional<BreakRecord> openBreak = breakRepository.findFirstByAttendanceIdAndEndTimeIsNullOrderByStartTimeDesc(attendance.getId());
        LocalDateTime now = LocalDateTime.now();

        if (openBreak.isPresent()) {
            BreakRecord b = openBreak.get();
            b.setEndTime(now);
            long minutes = Duration.between(b.getStartTime(), now).toMinutes();
            b.setDurationMinutes((int) minutes);
            return breakRepository.save(b);
        } else {
            BreakRecord b = new BreakRecord(attendance.getId(), breakType, now);
            return breakRepository.save(b);
        }
    }

    /**
     * Finaliza la jornada laboral activa del empleado (fichaje de salida).
     * <p>
     * Cierra cualquier pausa que haya quedado abierta, calcula los minutos totales trabajados
     * entre la hora de entrada y la de salida, descuenta los tiempos de pausa registrados,
     * cambia el estado a {@code CLOSED} y sella criptograficamente el registro mediante SHA-256.
     * </p>
     *
     * @param employeeId Identificador unico del empleado.
     * @return El {@link AttendanceRecord} debidamente cerrado y firmado.
     * @throws IllegalStateException Si no se encuentra una jornada abierta para el dia actual.
     */
    @Transactional
    public AttendanceRecord clockOut(Long employeeId) {
        LocalDate today = LocalDate.now();
        AttendanceRecord attendance = attendanceRepository.findByEmployeeIdAndWorkDate(employeeId, today)
                .orElseThrow(() -> new IllegalStateException("No hay jornada activa para cerrar."));

        LocalDateTime now = LocalDateTime.now();
        attendance.setClockOutTime(now);

        Optional<BreakRecord> openBreak = breakRepository.findFirstByAttendanceIdAndEndTimeIsNullOrderByStartTimeDesc(attendance.getId());
        if (openBreak.isPresent()) {
            BreakRecord b = openBreak.get();
            b.setEndTime(now);
            b.setDurationMinutes((int) Duration.between(b.getStartTime(), now).toMinutes());
            breakRepository.save(b);
        }

        long grossMinutes = Duration.between(attendance.getClockInTime(), now).toMinutes();
        int breakMinutes = breakRepository.findByAttendanceId(attendance.getId()).stream()
                .mapToInt(b -> b.getDurationMinutes() != null ? b.getDurationMinutes() : 0)
                .sum();

        int netMinutes = (int) Math.max(0, grossMinutes - breakMinutes);
        attendance.setWorkedMinutes(netMinutes);
        attendance.setStatus("CLOSED");
        attendance.setRecordHash(generateHash(employeeId, attendance.getClockInTime() + "|" + now + "|" + netMinutes));

        return attendanceRepository.save(attendance);
    }

    /**
     * Genera una cadena hash criptografica SHA-256 inmutable a partir de los datos de la jornada.
     *
     * @param employeeId ID del empleado.
     * @param payload Cadena con marcas temporales y minutos netos.
     * @return Representacion hexadecimal en minusculas del hash SHA-256.
     */
    private String generateHash(Long employeeId, String payload) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String input = "TRACKER_TREBALL|" + employeeId + "|" + payload;
            byte[] hash = digest.digest(input.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            return "HASH_FALLBACK_" + System.currentTimeMillis();
        }
    }
}
