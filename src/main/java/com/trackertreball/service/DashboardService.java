package com.trackertreball.service;

import com.trackertreball.model.AttendanceRecord;
import com.trackertreball.model.BreakRecord;
import com.trackertreball.model.Employee;
import com.trackertreball.model.Project;
import com.trackertreball.model.SafetyAlert;
import com.trackertreball.repository.BreakRepository;
import com.trackertreball.repository.EmployeeRepository;
import com.trackertreball.repository.ProjectRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Servicio de orquestacion para el Smart Work Assistant del Dashboard.
 *
 * @author Hassan Adraou
 * @version 2.1.0
 * @since 2026-11-02
 */
@Service
public class DashboardService {

    private final EmployeeRepository employeeRepository;
    private final AttendanceService attendanceService;
    private final BreakRepository breakRepository;
    private final ProjectRepository projectRepository;
    private final PrlService prlService;

    public DashboardService(EmployeeRepository employeeRepository,
                            AttendanceService attendanceService,
                            BreakRepository breakRepository,
                            ProjectRepository projectRepository,
                            PrlService prlService) {
        this.employeeRepository = employeeRepository;
        this.attendanceService = attendanceService;
        this.breakRepository = breakRepository;
        this.projectRepository = projectRepository;
        this.prlService = prlService;
    }

    public Map<String, Object> getSmartWorkAssistantData(Long employeeId) {
        Map<String, Object> data = new HashMap<>();

        // 1. Datos del Empleado (Hero)
        Employee employee = employeeRepository.findById(employeeId)
                .orElse(new Employee("Hassan Adraou", "hassan.adraou@test.es", "Tecnología", "Administrador General & Tech Lead", "Barcelona"));

        String centro = employee.getCentroTrabajo() != null ? employee.getCentroTrabajo() : "Barcelona";

        Map<String, Object> hero = new HashMap<>();
        hero.put("name", employee.getNombre());
        hero.put("city", centro);
        hero.put("temperature", "17°C");
        hero.put("weatherCondition", "Soleado con brisa");
        hero.put("destination", "Centro de Trabajo " + centro);
        hero.put("commuteMinutes", 20);
        hero.put("trafficStatus", "FLUIDO");
        data.put("hero", hero);

        // 2. Estado de la Jornada
        Optional<AttendanceRecord> attendanceOpt = attendanceService.getTodayAttendance(employeeId);
        Map<String, Object> shift = new HashMap<>();
        shift.put("officialSchedule", "09:00 -> 17:30");
        shift.put("expectedExit", "17:30");

        if (attendanceOpt.isPresent()) {
            AttendanceRecord att = attendanceOpt.get();
            boolean isOpen = "OPEN".equalsIgnoreCase(att.getStatus());
            boolean isClosed = "CLOSED".equalsIgnoreCase(att.getStatus());

            shift.put("hasAttendance", true);
            shift.put("shiftStarted", true);
            shift.put("status", att.getStatus());
            shift.put("clockInTime", att.getClockInTime() != null ? att.getClockInTime().toLocalTime().toString().substring(0, 5) : null);
            shift.put("clockOutTime", att.getClockOutTime() != null ? att.getClockOutTime().toLocalTime().toString().substring(0, 5) : null);
            shift.put("isFinished", isClosed);

            Optional<BreakRecord> openBreak = breakRepository
                    .findFirstByAttendanceIdAndEndTimeIsNullOrderByStartTimeDesc(att.getId());
            shift.put("isOnBreak", openBreak.isPresent());
            if (openBreak.isPresent()) {
                shift.put("currentBreakType", openBreak.get().getBreakType());
                shift.put("breakLimitMinutes", openBreak.get().getTiempoEstipuladoMinutos());
            }

            if (isOpen && att.getClockInTime() != null) {
                long totalElapsedSeconds = Duration.between(att.getClockInTime(), LocalDateTime.now()).getSeconds();
                long totalBreakSeconds = breakRepository.findByAttendanceId(att.getId()).stream()
                        .mapToLong(b -> b.getDurationMinutes() != null ? (long) b.getDurationMinutes() * 60 : 0)
                        .sum();
                long netSeconds = Math.max(0, totalElapsedSeconds - totalBreakSeconds);
                shift.put("workedSeconds", netSeconds);
                shift.put("workedMinutes", (int) (netSeconds / 60));
            } else {
                int workedMin = att.getWorkedMinutes() != null ? att.getWorkedMinutes() : 0;
                shift.put("workedSeconds", workedMin * 60);
                shift.put("workedMinutes", workedMin);
            }
        } else {
            shift.put("hasAttendance", false);
            shift.put("shiftStarted", false);
            shift.put("status", "NO_INICIADA");
            shift.put("clockInTime", null);
            shift.put("clockOutTime", null);
            shift.put("isOnBreak", false);
            shift.put("isFinished", false);
            shift.put("workedSeconds", 0);
            shift.put("workedMinutes", 0);
        }
        data.put("shift", shift);

        // 3. Recordatorios Inteligentes (Timeline)
        List<Map<String, String>> reminders = new ArrayList<>();
        reminders.add(Map.of("type", "INFO", "title_ca", "Pausa ergonómica visual", "sub_ca", "Pausa recomendada cada 2 horas de trabajo en pantalla (PRL)"));
        reminders.add(Map.of("type", "SUCCESS", "title_ca", "Centro " + centro + " operativo", "sub_ca", "Registro horario verificado conforme RDL 8/2019"));
        data.put("reminders", reminders);

        // 4. Proyecto Activo
        List<Project> activeProjects = projectRepository.findByStatus("IN_PROGRESS");
        if (!activeProjects.isEmpty()) {
            Project p = activeProjects.get(0);
            Map<String, Object> projData = new HashMap<>();
            projData.put("name", p.getName());
            projData.put("sprintProgress", p.getSprintProgress());
            projData.put("teamMembers", p.getTeamMembers());
            projData.put("dedicatedHours", p.getDedicatedHours());
            data.put("project", projData);
        }

        // 5. Alertas de Prevencion de Riesgos Laborales (PRL)
        List<SafetyAlert> alerts = prlService.getActiveAlerts();
        data.put("safetyAlerts", alerts);

        return data;
    }
}
