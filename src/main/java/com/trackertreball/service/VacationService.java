package com.trackertreball.service;

import com.trackertreball.model.Employee;
import com.trackertreball.model.VacationRequest;
import com.trackertreball.repository.EmployeeRepository;
import com.trackertreball.repository.VacationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Servicio de logica de negocio para la gestion y aprobacion de solicitudes de vacaciones.
 * Permite que supervisores y administradores revisen, aprueben, rechacen o pidan ajustes
 * de cualquier empleado y de si mismos.
 *
 * @author Hassan Adraou
 * @version 1.0.0
 * @since 2026-11-03
 */
@Service
public class VacationService {

    private final VacationRepository vacationRepository;
    private final EmployeeRepository employeeRepository;

    public VacationService(VacationRepository vacationRepository, EmployeeRepository employeeRepository) {
        this.vacationRepository = vacationRepository;
        this.employeeRepository = employeeRepository;
    }

    public Map<String, Object> getVacationsOverview(Long employeeId) {
        return getVacationsOverview(employeeId, null);
    }

    public Map<String, Object> getVacationsOverview(Long employeeId, String callerRole) {
        Employee employee = employeeRepository.findById(employeeId).orElse(null);
        String employeeName = employee != null ? employee.getNombre() : "Empleado";
        String resolvedRole = (callerRole != null && !callerRole.isBlank())
                ? callerRole.trim().toUpperCase()
                : (employee != null && employee.getRol() != null ? employee.getRol().toUpperCase() : "EMPLOYEE");

        List<VacationRequest> myRequests = vacationRepository.findByEmployeeIdOrderByStartDateDesc(employeeId);
        List<VacationRequest> allRequests = vacationRepository.findAllByOrderByCreatedAtDesc();

        List<VacationRequest> teamRequests;
        if ("ADMIN".equals(resolvedRole)) {
            teamRequests = allRequests;
        } else if ("MANAGER".equals(resolvedRole)) {
            String dept = employee != null ? employee.getDepartamento() : null;
            Long centerId = employee != null ? employee.getWorkCenterId() : null;
            List<Long> teamMemberIds = employeeRepository.findAll().stream()
                    .filter(e -> (dept != null && dept.equalsIgnoreCase(e.getDepartamento())) ||
                                 (centerId != null && centerId.equals(e.getWorkCenterId())))
                    .map(Employee::getId)
                    .toList();
            teamRequests = allRequests.stream()
                    .filter(r -> teamMemberIds.contains(r.getEmployeeId()))
                    .toList();
        } else {
            teamRequests = Collections.emptyList();
        }

        int totalDays = 22;
        int usedDays = myRequests.stream()
                .filter(r -> "APPROVED".equalsIgnoreCase(r.getStatus()))
                .mapToInt(VacationRequest::getDaysCount)
                .sum();

        int pendingDays = myRequests.stream()
                .filter(r -> "PENDING".equalsIgnoreCase(r.getStatus()) || "ADJUSTED".equalsIgnoreCase(r.getStatus()))
                .mapToInt(VacationRequest::getDaysCount)
                .sum();

        int remainingDays = Math.max(0, totalDays - usedDays);

        Map<String, Object> balance = new HashMap<>();
        balance.put("total", totalDays);
        balance.put("used", usedDays);
        balance.put("pending", pendingDays);
        balance.put("remaining", remainingDays);

        Map<String, Object> result = new HashMap<>();
        result.put("status", "SUCCESS");
        result.put("balance", balance);
        result.put("myRequests", myRequests);
        result.put("teamRequests", teamRequests);
        result.put("role", resolvedRole);

        return result;
    }

    @Transactional
    public VacationRequest createRequest(Long employeeId, Map<String, Object> payload) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Empleado no encontrado con ID " + employeeId));

        String startStr = (String) payload.get("startDate");
        String endStr = (String) payload.get("endDate");
        int daysCount = payload.containsKey("daysCount") ? Integer.parseInt(payload.get("daysCount").toString()) : 1;
        String type = payload.getOrDefault("type", "Vacaciones Anuales").toString();
        String reason = payload.getOrDefault("reason", "Descanso reglamentario").toString();

        VacationRequest req = new VacationRequest();
        req.setEmployeeId(employeeId);
        req.setEmployeeName(employee.getNombre());
        req.setStartDate(LocalDate.parse(startStr));
        req.setEndDate(LocalDate.parse(endStr));
        req.setDaysCount(daysCount);
        req.setType(type);
        req.setReason(reason);
        req.setStatus("PENDING");
        req.setCreatedAt(LocalDateTime.now());
        req.setUpdatedAt(LocalDateTime.now());

        return vacationRepository.save(req);
    }

    @Transactional
    public VacationRequest updateRequest(Long requestId, Map<String, Object> payload) {
        VacationRequest req = vacationRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Solicitud de vacaciones no encontrada con ID " + requestId));

        if (payload.containsKey("startDate")) {
            req.setStartDate(LocalDate.parse(payload.get("startDate").toString()));
        }
        if (payload.containsKey("endDate")) {
            req.setEndDate(LocalDate.parse(payload.get("endDate").toString()));
        }
        if (payload.containsKey("daysCount")) {
            req.setDaysCount(Integer.parseInt(payload.get("daysCount").toString()));
        }
        if (payload.containsKey("reason")) {
            req.setReason(payload.get("reason").toString());
        }

        req.setStatus("PENDING");
        req.setUpdatedAt(LocalDateTime.now());
        return vacationRepository.save(req);
    }

    @Transactional
    public VacationRequest reviewRequest(Long requestId, String action, String note, String reviewerName) {
        VacationRequest req = vacationRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Solicitud de vacaciones no encontrada con ID " + requestId));

        if ("accept".equalsIgnoreCase(action) || "approve".equalsIgnoreCase(action)) {
            req.setStatus("APPROVED");
        } else if ("reject".equalsIgnoreCase(action) || "deny".equalsIgnoreCase(action)) {
            req.setStatus("REJECTED");
        } else if ("adjust".equalsIgnoreCase(action)) {
            req.setStatus("ADJUSTED");
        }

        req.setReviewerName(reviewerName != null ? reviewerName : "Administrador / Supervisor");
        req.setReviewNote(note);
        req.setUpdatedAt(LocalDateTime.now());

        return vacationRepository.save(req);
    }
}

