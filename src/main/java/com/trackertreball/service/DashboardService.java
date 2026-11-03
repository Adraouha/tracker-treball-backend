package com.trackertreball.service;

import com.trackertreball.model.AttendanceRecord;
import com.trackertreball.model.Employee;
import com.trackertreball.model.Project;
import com.trackertreball.model.SafetyAlert;
import com.trackertreball.repository.EmployeeRepository;
import com.trackertreball.repository.ProjectRepository;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Servicio de orquestacion para el asistente inteligente y panel Bento Grid del Dashboard.
 * <p>
 * Agrega informacion multidisciplinar en una sola llamada optimizada:
 * <ul>
 *   <li><b>Hero context:</b> Datos del empleado, ubicacion (Vic Central), clima actual y estado del trayecto/commute.</li>
 *   <li><b>Shift status:</b> Estado reactivo del temporizador de jornada, horario oficial y minutos trabajados.</li>
 *   <li><b>Smart Reminders:</b> Avisos inteligentes de pausas pendientes y ergonomia visual PRL.</li>
 *   <li><b>Active Project:</b> Proyecto en curso asignado, avance de Sprint y horas imputadas.</li>
 *   <li><b>Safety Alerts:</b> Avisos meteorologicos y preventivos de Osona via el modulo PRL.</li>
 * </ul>
 * </p>
 *
 * @author Hassan Adraou
 * @version 1.0.0
 * @since 2026-11-02
 * @see AttendanceService
 * @see PrlService
 */
@Service
public class DashboardService {

    private final EmployeeRepository employeeRepository;
    private final AttendanceService attendanceService;
    private final ProjectRepository projectRepository;
    private final PrlService prlService;

    /**
     * Constructor con inyeccion de los componentes requeridos para alimentar el asistente.
     *
     * @param employeeRepository Repositorio de empleados.
     * @param attendanceService Servicio de control horario y pausas.
     * @param projectRepository Repositorio de proyectos y sprints.
     * @param prlService Servicio de prevencion de riesgos laborales.
     */
    public DashboardService(EmployeeRepository employeeRepository,
                            AttendanceService attendanceService,
                            ProjectRepository projectRepository,
                            PrlService prlService) {
        this.employeeRepository = employeeRepository;
        this.attendanceService = attendanceService;
        this.projectRepository = projectRepository;
        this.prlService = prlService;
    }

    /**
     * Construye el mapa de datos completo para la vista Smart Work Assistant del Dashboard.
     *
     * @param employeeId Identificador unico del empleado que visualiza el panel.
     * @return {@link Map} estructurado con las secciones hero, shift, reminders, project y safety.
     */
    public Map<String, Object> getSmartWorkAssistantData(Long employeeId) {
        Map<String, Object> data = new HashMap<>();

        // 1. Datos del Empleado (Hero)
        Employee employee = employeeRepository.findById(employeeId)
                .orElse(new Employee("Mohamed O.", "mohamed@trackertreball.com", "Enginyeria", "Desenvolupador Senior", "Oficina Central Vic"));

        Map<String, Object> hero = new HashMap<>();
        hero.put("name", employee.getNombre());
        hero.put("city", "Vic, Barcelona");
        hero.put("temperature", "14°C");
        hero.put("weatherCondition", "Parcialment ennuvolat");
        hero.put("destination", employee.getCentroTrabajo());
        hero.put("commuteMinutes", 22);
        hero.put("trafficStatus", "MODERAT");
        data.put("hero", hero);

        // 2. Estado de la Jornada
        Optional<AttendanceRecord> attendanceOpt = attendanceService.getTodayAttendance(employeeId);
        Map<String, Object> shift = new HashMap<>();
        shift.put("officialSchedule", "09:00 -> 17:30");
        shift.put("clockInTime", attendanceOpt.map(a -> a.getClockInTime().toLocalTime().toString().substring(0, 5)).orElse("08:57"));
        shift.put("workedMinutes", attendanceOpt.map(AttendanceRecord::getWorkedMinutes).orElse(204));
        shift.put("expectedExit", "17:30");
        shift.put("status", attendanceOpt.map(AttendanceRecord::getStatus).orElse("OPEN"));
        data.put("shift", shift);

        // 3. Recordatorios Inteligentes (Timeline)
        List<Map<String, String>> reminders = new ArrayList<>();
        reminders.add(Map.of("type", "WARNING", "title_ca", "Pausa pendent de realitzar", "sub_ca", "Portes 3h continuades de treball efectiu"));
        reminders.add(Map.of("type", "INFO", "title_ca", "Revisio ergonomica PRL", "sub_ca", "Recorda ajustar l'alcada de la pantalla"));
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
