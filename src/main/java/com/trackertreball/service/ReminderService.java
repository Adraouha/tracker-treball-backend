package com.trackertreball.service;

import com.trackertreball.model.Employee;
import com.trackertreball.model.Holiday;
import com.trackertreball.model.WorkCenter;
import com.trackertreball.repository.EmployeeRepository;
import com.trackertreball.repository.HolidayRepository;
import com.trackertreball.repository.WorkCenterRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Servicio de alertas, avisos y recordatorios personalizados para el empleado.
 * Incluye proxima pausa, festivo cercano, clima del centro de trabajo, cumpleaños de compañeros y reuniones.
 *
 * @author Hassan Adraou
 * @version 1.0.0
 * @since 2026-11-03
 */
@Service
public class ReminderService {

    private final EmployeeRepository employeeRepository;
    private final HolidayRepository holidayRepository;
    private final WorkCenterRepository workCenterRepository;

    public ReminderService(EmployeeRepository employeeRepository,
                           HolidayRepository holidayRepository,
                           WorkCenterRepository workCenterRepository) {
        this.employeeRepository = employeeRepository;
        this.holidayRepository = holidayRepository;
        this.workCenterRepository = workCenterRepository;
    }

    public Map<String, Object> getRemindersForEmployee(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId).orElse(null);
        if (employee == null) {
            throw new IllegalArgumentException("Empleado #" + employeeId + " no encontrado.");
        }

        Long workCenterId = employee.getWorkCenterId();
        WorkCenter workCenter = null;
        if (workCenterId != null) {
            workCenter = workCenterRepository.findById(workCenterId).orElse(null);
        } else if (employee.getCentroTrabajo() != null) {
            workCenter = workCenterRepository.findByNombreIgnoreCase(employee.getCentroTrabajo()).orElse(null);
        }

        LocalDate today = LocalDate.now();
        Map<String, Object> reminders = new HashMap<>();

        // 1. Proximo festivo
        Optional<Holiday> nextHolidayOpt = holidayRepository.findNextHoliday(
                workCenter != null ? workCenter.getId() : null, today);
        if (nextHolidayOpt.isPresent()) {
            Holiday h = nextHolidayOpt.get();
            reminders.put("proximoFestivo", Map.of(
                    "nombre", h.getNombre(),
                    "fecha", h.getFecha().toString(),
                    "tipo", h.getTipo(),
                    "diasRestantes", java.time.temporal.ChronoUnit.DAYS.between(today, h.getFecha())
            ));
        } else {
            reminders.put("proximoFestivo", Map.of(
                    "nombre", "Sin festivos próximos programados",
                    "fecha", "",
                    "tipo", "GENERAL",
                    "diasRestantes", 0
            ));
        }

        // 2. Tiempo y clima del centro de trabajo
        String ciudad = workCenter != null ? workCenter.getCiudad() : "Barcelona";
        reminders.put("tiempoYClima", getWeatherForCity(ciudad));

        // 3. Proxima pausa recomendada
        reminders.put("proximaPausa", Map.of(
                "tipo", "Pausa Visual PRL (5 min)",
                "horaSugerida", "11:30",
                "descripcion", "Recomendación ergonómica por fatiga visual tras 2 horas continuadas."
        ));

        // 4. Cumpleaños de compañeros (proximos 7 dias)
        List<Employee> allEmployees = employeeRepository.findAll();
        List<Map<String, Object>> upcomingBirthdays = new ArrayList<>();
        for (Employee col : allEmployees) {
            if (col.getFechaNacimiento() != null && col.isActivo() && !col.getId().equals(employee.getId())) {
                LocalDate bdayThisYear = col.getFechaNacimiento().withYear(today.getYear());
                if (bdayThisYear.isBefore(today)) {
                    bdayThisYear = bdayThisYear.plusYears(1);
                }
                long daysUntil = java.time.temporal.ChronoUnit.DAYS.between(today, bdayThisYear);
                if (daysUntil >= 0 && daysUntil <= 7) {
                    Map<String, Object> bdayItem = new HashMap<>();
                    bdayItem.put("nombre", col.getNombre());
                    bdayItem.put("puesto", col.getPuesto());
                    bdayItem.put("departamento", col.getDepartamento());
                    bdayItem.put("dias", daysUntil);
                    bdayItem.put("esHoy", daysUntil == 0);
                    bdayItem.put("mensaje", daysUntil == 0 
                            ? "¡Hoy es el cumpleaños de " + col.getNombre() + "! 🎉" 
                            : col.getNombre() + " cumple años en " + daysUntil + " día(s)");
                    upcomingBirthdays.add(bdayItem);
                }
            }
        }
        reminders.put("cumpleanosCompaneros", upcomingBirthdays);

        // 5. Proxima reunion
        reminders.put("proximaReunion", Map.of(
                "titulo", "Sincronización Semanal de Proyecto & PRL",
                "fechaHora", "Mañana a las 10:00",
                "sala", "Sala Virtual 2 / Centro " + ciudad,
                "organizador", "Albert Guiteras"
        ));

        return reminders;
    }

    private Map<String, Object> getWeatherForCity(String ciudad) {
        String c = (ciudad != null) ? ciudad.toLowerCase() : "barcelona";
        int temp = 17;
        String desc = "Soleado con brisa";
        String icono = "sunny";

        if (c.contains("madrid")) {
            temp = 15;
            desc = "Cielos despejados";
            icono = "sunny";
        } else if (c.contains("bilbao")) {
            temp = 13;
            desc = "Llovizna suave intermitente";
            icono = "rainy";
        } else if (c.contains("sevilla")) {
            temp = 22;
            desc = "Cálido y muy soleado";
            icono = "sunny";
        } else if (c.contains("valencia")) {
            temp = 19;
            desc = "Agradable y templado";
            icono = "partly_cloudy";
        } else if (c.contains("parís") || c.contains("extranjero")) {
            temp = 12;
            desc = "Nublado con claros";
            icono = "cloudy";
        }

        Map<String, Object> w = new HashMap<>();
        w.put("ciudad", ciudad);
        w.put("temperatura", temp);
        w.put("temperaturaStr", temp + "°C");
        w.put("descripcion", desc);
        w.put("icono", icono);
        return w;
    }
}

