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
import java.util.*;

/**
 * Servicio de logica de negocio para la gestion del control horario laboral, pausas y auditorias.
 * Incluye calculo automatico de exceso de descansos y reportes semanales/mensuales.
 *
 * @author Hassan Adraou
 * @version 2.1.0
 * @since 2026-11-02
 */
@Service
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final BreakRepository breakRepository;

    public AttendanceService(AttendanceRepository attendanceRepository, BreakRepository breakRepository) {
        this.attendanceRepository = attendanceRepository;
        this.breakRepository = breakRepository;
    }

    public Optional<AttendanceRecord> getTodayAttendance(Long employeeId) {
        return attendanceRepository.findFirstByEmployeeIdAndWorkDateOrderByClockInTimeDesc(employeeId, LocalDate.now());
    }

    @Transactional
    public AttendanceRecord clockIn(Long employeeId, String modality) {
        LocalDate today = LocalDate.now();
        Optional<AttendanceRecord> existingOpen = attendanceRepository
                .findFirstByEmployeeIdAndWorkDateAndStatusOrderByClockInTimeDesc(employeeId, today, "OPEN");

        if (existingOpen.isPresent()) {
            throw new IllegalStateException("El empleado ya tiene una jornada activa en curso.");
        }

        LocalDateTime now = LocalDateTime.now();
        String chosenModality = (modality != null && !modality.isBlank()) ? modality : "PRESENCIAL";
        AttendanceRecord record = new AttendanceRecord(employeeId, today, now, chosenModality);
        record.setRecordHash(generateHash(employeeId, now.toString()));
        return attendanceRepository.save(record);
    }

    @Transactional
    public BreakRecord startBreak(Long employeeId, String breakType, Integer tiempoEstipuladoMinutos) {
        LocalDate today = LocalDate.now();
        AttendanceRecord attendance = attendanceRepository
                .findFirstByEmployeeIdAndWorkDateAndStatusOrderByClockInTimeDesc(employeeId, today, "OPEN")
                .orElseThrow(() -> new IllegalStateException("No hay jornada activa para iniciar descanso."));

        Optional<BreakRecord> openBreak = breakRepository
                .findFirstByAttendanceIdAndEndTimeIsNullOrderByStartTimeDesc(attendance.getId());
        if (openBreak.isPresent()) {
            return openBreak.get();
        }

        int estipulado = (tiempoEstipuladoMinutos != null && tiempoEstipuladoMinutos > 0) ? tiempoEstipuladoMinutos : 15;
        String type = (breakType != null && !breakType.isBlank()) ? breakType : "PAUSA";
        BreakRecord b = new BreakRecord(attendance.getId(), type, LocalDateTime.now(), estipulado);
        return breakRepository.save(b);
    }

    @Transactional
    public BreakRecord finishBreak(Long employeeId) {
        LocalDate today = LocalDate.now();
        AttendanceRecord attendance = attendanceRepository
                .findFirstByEmployeeIdAndWorkDateAndStatusOrderByClockInTimeDesc(employeeId, today, "OPEN")
                .orElseThrow(() -> new IllegalStateException("No hay jornada activa para finalizar descanso."));

        BreakRecord b = breakRepository
                .findFirstByAttendanceIdAndEndTimeIsNullOrderByStartTimeDesc(attendance.getId())
                .orElseThrow(() -> new IllegalStateException("No hay ninguna pausa abierta actualmente."));

        LocalDateTime now = LocalDateTime.now();
        b.setEndTime(now);

        long duracionMin = Math.max(1, Duration.between(b.getStartTime(), now).toMinutes());
        b.setDurationMinutes((int) duracionMin);

        int estipulado = b.getTiempoEstipuladoMinutos() != null ? b.getTiempoEstipuladoMinutos() : 15;
        int exceso = (int) Math.max(0, duracionMin - estipulado);
        b.setMinutosAdicionales(exceso);

        if (exceso > 0) {
            b.setMensajeNotificacion("Has consumido " + duracionMin + " minutos de pausa (" + exceso + " minutos adicionales sobre los " + estipulado + " permitidos).");
        } else {
            b.setMensajeNotificacion("Pausa completada dentro del tiempo estipulado (" + duracionMin + " de " + estipulado + " min).");
        }

        return breakRepository.save(b);
    }

    @Transactional
    public BreakRecord toggleBreak(Long employeeId, String breakType) {
        LocalDate today = LocalDate.now();
        AttendanceRecord attendance = attendanceRepository
                .findFirstByEmployeeIdAndWorkDateAndStatusOrderByClockInTimeDesc(employeeId, today, "OPEN")
                .orElseThrow(() -> new IllegalStateException("No hay jornada activa para gestionar pausas."));

        Optional<BreakRecord> openBreak = breakRepository
                .findFirstByAttendanceIdAndEndTimeIsNullOrderByStartTimeDesc(attendance.getId());
        if (openBreak.isPresent()) {
            return finishBreak(employeeId);
        } else {
            int estipulado = "COMIDA".equalsIgnoreCase(breakType) || "ALMUERZO".equalsIgnoreCase(breakType) ? 60 :
                             "PRL_VISUAL".equalsIgnoreCase(breakType) ? 5 : 15;
            return startBreak(employeeId, breakType, estipulado);
        }
    }

    @Transactional
    public AttendanceRecord clockOut(Long employeeId) {
        LocalDate today = LocalDate.now();
        AttendanceRecord attendance = attendanceRepository
                .findFirstByEmployeeIdAndWorkDateAndStatusOrderByClockInTimeDesc(employeeId, today, "OPEN")
                .orElseThrow(() -> new IllegalStateException("No hay jornada activa para cerrar."));

        LocalDateTime now = LocalDateTime.now();
        attendance.setClockOutTime(now);

        Optional<BreakRecord> openBreak = breakRepository
                .findFirstByAttendanceIdAndEndTimeIsNullOrderByStartTimeDesc(attendance.getId());
        if (openBreak.isPresent()) {
            finishBreak(employeeId);
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

    public Map<String, Object> getReports(Long employeeId) {
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.minusDays(7);
        LocalDate monthStart = today.minusDays(30);

        List<AttendanceRecord> records = attendanceRepository.findByEmployeeId(employeeId);

        int totalWorkedMinutes = 0;
        int weekWorkedMinutes = 0;
        int monthWorkedMinutes = 0;

        int totalOvertimeMinutes = 0;
        int weekOvertimeMinutes = 0;
        int monthOvertimeMinutes = 0;

        int totalPausaMinutos = 0;
        int totalMinutosAdicionales = 0;
        int weekMinutosAdicionales = 0;
        int monthMinutosAdicionales = 0;

        List<Map<String, Object>> breakDetails = new ArrayList<>();

        for (AttendanceRecord att : records) {
            int worked = att.getWorkedMinutes() != null ? att.getWorkedMinutes() : 0;
            totalWorkedMinutes += worked;

            // Horas extra sobre jornada estandar diaria de 8h (480 minutos)
            int dailyOvertime = Math.max(0, worked - 480);
            totalOvertimeMinutes += dailyOvertime;

            if (!att.getWorkDate().isBefore(weekStart)) {
                weekWorkedMinutes += worked;
                weekOvertimeMinutes += dailyOvertime;
            }
            if (!att.getWorkDate().isBefore(monthStart)) {
                monthWorkedMinutes += worked;
                monthOvertimeMinutes += dailyOvertime;
            }

            List<BreakRecord> breaks = breakRepository.findByAttendanceId(att.getId());
            for (BreakRecord b : breaks) {
                int dur = b.getDurationMinutes() != null ? b.getDurationMinutes() : 0;
                int exc = b.getMinutosAdicionales() != null ? b.getMinutosAdicionales() : 0;
                totalPausaMinutos += dur;
                totalMinutosAdicionales += exc;

                if (!att.getWorkDate().isBefore(weekStart)) {
                    weekMinutosAdicionales += exc;
                }
                if (!att.getWorkDate().isBefore(monthStart)) {
                    monthMinutosAdicionales += exc;
                }

                Map<String, Object> item = new HashMap<>();
                item.put("fecha", att.getWorkDate());
                item.put("tipo", b.getBreakType());
                item.put("duracionMinutos", dur);
                item.put("estipuladoMinutos", b.getTiempoEstipuladoMinutos());
                item.put("minutosAdicionales", exc);
                item.put("mensaje", b.getMensajeNotificacion());
                breakDetails.add(item);
            }
        }

        Map<String, Object> res = new HashMap<>();
        res.put("employeeId", employeeId);
        res.put("totalHorasTrabajadas", formatHoursAndMinutes(totalWorkedMinutes));
        res.put("semanalHorasTrabajadas", formatHoursAndMinutes(weekWorkedMinutes));
        res.put("mensualHorasTrabajadas", formatHoursAndMinutes(monthWorkedMinutes));

        // Horas extraordinarias trabajadas
        res.put("totalHorasExtra", formatHoursAndMinutes(totalOvertimeMinutes));
        res.put("semanalHorasExtra", formatHoursAndMinutes(weekOvertimeMinutes));
        res.put("mensualHorasExtra", formatHoursAndMinutes(monthOvertimeMinutes));

        // Apartado de tiempo adicional consumido en pausas convertido a horas y minutos
        res.put("totalMinutosAdicionales", totalMinutosAdicionales);
        res.put("totalTiempoAdicionalConsumido", formatHoursAndMinutes(totalMinutosAdicionales));
        res.put("semanalTiempoAdicionalConsumido", formatHoursAndMinutes(weekMinutosAdicionales));
        res.put("mensualTiempoAdicionalConsumido", formatHoursAndMinutes(monthMinutosAdicionales));
        res.put("totalPausaMinutos", totalPausaMinutos);
        res.put("desglosePausas", breakDetails);

        return res;
    }

    private String formatHoursAndMinutes(int totalMinutes) {
        int hours = totalMinutes / 60;
        int minutes = totalMinutes % 60;
        if (hours == 0) {
            return minutes + " min";
        }
        return hours + " h " + (minutes > 0 ? minutes + " min" : "");
    }

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
