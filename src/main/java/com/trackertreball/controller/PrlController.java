package com.trackertreball.controller;

import com.trackertreball.model.SafetyAlert;
import com.trackertreball.model.WorkIncident;
import com.trackertreball.service.PrlService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controlador REST para el centro de Prevencion de Riesgos Laborales (PRL Help Center).
 * <p>
 * Proporciona endpoints para la seguridad y salud en el trabajo:
 * <ul>
 *   <li><b>GET /alerts:</b> Consulta alertas preventivas y meteorologicas en vigor.</li>
 *   <li><b>POST /incidents:</b> Notifica riesgos laborales, accidentes leves o incidentes observados.</li>
 *   <li><b>POST /sos:</b> Dispara el protocolo de socorro y envio de auxilio geolocalizado.</li>
 * </ul>
 * </p>
 *
 * @author Hassan Adraou
 * @version 1.0.0
 * @since 2026-11-02
 * @see com.trackertreball.service.PrlService
 * @see com.trackertreball.model.SafetyAlert
 * @see com.trackertreball.model.WorkIncident
 */
@RestController
@RequestMapping("/api/v1/prl")
public class PrlController {

    private final PrlService prlService;

    /**
     * Inyecta el servicio de prevencion de riesgos laborales.
     *
     * @param prlService Servicio de PRL y emergencias.
     */
    public PrlController(PrlService prlService) {
        this.prlService = prlService;
    }

    /**
     * Endpoint para obtener la lista de avisos preventivos y alertas de seguridad activos.
     * <p>
     * <b>Ruta HTTP:</b> {@code GET /api/v1/prl/alerts}
     * </p>
     *
     * @return {@link ResponseEntity} con codigo HTTP 200 OK y la lista de alertas {@link SafetyAlert}.
     */
    @GetMapping("/alerts")
    public ResponseEntity<List<SafetyAlert>> getAlerts() {
        return ResponseEntity.ok(prlService.getActiveAlerts());
    }

    /**
     * Endpoint para registrar un incidente laboral o condicion insegura detectada por un trabajador.
     * <p>
     * <b>Ruta HTTP:</b> {@code POST /api/v1/prl/incidents}
     * </p>
     *
     * @param payload Mapa JSON con los datos del incidente: employeeId, location, hazardDescription y suggestedAction.
     * @return {@link ResponseEntity} con codigo HTTP 201 Created y el {@link WorkIncident} registrado.
     */
    @PostMapping("/incidents")
    public ResponseEntity<WorkIncident> reportIncident(@RequestBody Map<String, Object> payload) {
        Long employeeId = payload.get("employeeId") != null ? Long.valueOf(payload.get("employeeId").toString()) : 1L;
        String location = payload.getOrDefault("location", "No especificado").toString();
        String description = payload.getOrDefault("hazardDescription", "Incidente reportado").toString();
        String action = payload.getOrDefault("suggestedAction", "").toString();

        WorkIncident incident = prlService.reportIncident(employeeId, location, description, action);
        return ResponseEntity.status(HttpStatus.CREATED).body(incident);
    }

    /**
     * Endpoint para disparar una llamada de emergencia SOS geolocalizada.
     * <p>
     * <b>Ruta HTTP:</b> {@code POST /api/v1/prl/sos}
     * </p>
     *
     * @param payload Mapa JSON con coordenadas (latitude, longitude) y employeeId.
     * @return {@link ResponseEntity} con codigo HTTP 200 OK y confirmacion del despacho de auxilio.
     */
    @PostMapping("/sos")
    public ResponseEntity<Map<String, Object>> triggerSos(@RequestBody Map<String, Object> payload) {
        Long employeeId = payload.get("employeeId") != null ? Long.valueOf(payload.get("employeeId").toString()) : 1L;
        Double lat = payload.get("latitude") != null ? Double.valueOf(payload.get("latitude").toString()) : 41.9304;
        Double lng = payload.get("longitude") != null ? Double.valueOf(payload.get("longitude").toString()) : 2.2541;

        Map<String, Object> response = prlService.triggerSos(employeeId, lat, lng);
        return ResponseEntity.ok(response);
    }
}
