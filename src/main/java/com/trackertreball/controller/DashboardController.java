package com.trackertreball.controller;

import com.trackertreball.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controlador REST que sirve los datos agregados para el Bento Grid del Dashboard interactivo.
 * <p>
 * Agrupa la informacion de asistencia, recordatorios inteligentes, proyectos y meteorologia
 * en una sola peticion para optimizar el rendimiento y tiempo de respuesta en clientes web.
 * </p>
 *
 * @author Hassan Adraou
 * @version 1.0.0
 * @since 2026-11-02
 * @see com.trackertreball.service.DashboardService
 */
@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * Inyecta el servicio de orquestacion del dashboard.
     *
     * @param dashboardService Servicio que construye la vista Bento Grid.
     */
    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    /**
     * Endpoint para obtener el conjunto completo de datos del Smart Work Assistant.
     * <p>
     * <b>Ruta HTTP:</b> {@code GET /api/v1/dashboard/assistant?employeeId={id}}
     * </p>
     *
     * @param employeeId Identificador unico del empleado.
     * @return {@link ResponseEntity} con codigo HTTP 200 OK y el mapa de datos completo (hero, shift, reminders, project, safety).
     */
    @GetMapping("/assistant")
    public ResponseEntity<Map<String, Object>> getSmartAssistant(
            @RequestParam(defaultValue = "1") Long employeeId) {
        return ResponseEntity.ok(dashboardService.getSmartWorkAssistantData(employeeId));
    }
}
