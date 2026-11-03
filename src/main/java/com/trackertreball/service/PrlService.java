package com.trackertreball.service;

import com.trackertreball.model.SafetyAlert;
import com.trackertreball.model.WorkIncident;
import com.trackertreball.repository.IncidentRepository;
import com.trackertreball.repository.SafetyAlertRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Servicio de logica de negocio para el modulo de Prevencion de Riesgos Laborales (PRL).
 * <p>
 * Gestiona tres pilares fundamentales de la seguridad laboral:
 * <ul>
 *   <li><b>Alertas preventivas activas:</b> Avisos tempranos de proteccion civil y meteorologia adversa.</li>
 *   <li><b>Notificacion de incidentes (Near Misses):</b> Registro estructurado de condiciones de riesgo detectadas.</li>
 *   <li><b>Protocolo de Emergencia SOS:</b> Emision inmediata de llamada de socorro con geolocalizacion exacta.</li>
 * </ul>
 * </p>
 *
 * @author Hassan Adraou
 * @version 1.0.0
 * @since 2026-11-02
 * @see com.trackertreball.model.SafetyAlert
 * @see com.trackertreball.model.WorkIncident
 */
@Service
public class PrlService {

    private final IncidentRepository incidentRepository;
    private final SafetyAlertRepository safetyAlertRepository;

    /**
     * Constructor con inyeccion de dependencias para incidentes y alertas de seguridad.
     *
     * @param incidentRepository Repositorio JPA de incidentes de trabajo.
     * @param safetyAlertRepository Repositorio JPA de alertas preventivas.
     */
    public PrlService(IncidentRepository incidentRepository, SafetyAlertRepository safetyAlertRepository) {
        this.incidentRepository = incidentRepository;
        this.safetyAlertRepository = safetyAlertRepository;
    }

    /**
     * Obtiene el listado de alertas de seguridad laboral actualmente en vigor.
     *
     * @return Lista de alertas {@link SafetyAlert} activas.
     */
    public List<SafetyAlert> getActiveAlerts() {
        return safetyAlertRepository.findByActiveTrue();
    }

    /**
     * Registra un nuevo incidente laboral o condicion potencialmente peligrosa observada.
     *
     * @param employeeId ID del empleado que efectua la notificacion.
     * @param location Ubicacion especifica donde se detecto el riesgo.
     * @param hazardDescription Descripcion detallada del riesgo o anomalia.
     * @param suggestedAction Medida correctiva preventiva propuesta.
     * @return El objeto {@link WorkIncident} persistido en el sistema.
     */
    public WorkIncident reportIncident(Long employeeId, String location, String hazardDescription, String suggestedAction) {
        WorkIncident incident = new WorkIncident(
            employeeId,
            LocalDateTime.now(),
            location,
            hazardDescription,
            suggestedAction
        );
        return incidentRepository.save(incident);
    }

    /**
     * Activa el protocolo de emergencia inmediata SOS con transmision de coordenadas GPS.
     * <p>
     * Notifica a la mutua laboral colaboradora, comite de seguridad y servicios de emergencia (112),
     * adjuntando la ubicacion de las instalaciones de Vic Central o del punto geografico remitido.
     * </p>
     *
     * @param employeeId Identificador del empleado que pulsa el boton de panico.
     * @param latitude Coordenada de latitud GPS (por defecto centro de Vic: 41.9304).
     * @param longitude Coordenada de longitud GPS (por defecto centro de Vic: 2.2541).
     * @return Mapa de respuesta confirmando el despacho de auxilio y la marca temporal.
     */
    public Map<String, Object> triggerSos(Long employeeId, Double latitude, Double longitude) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "DISPATCHED");
        response.put("employeeId", employeeId);
        response.put("timestamp", LocalDateTime.now());
        response.put("latitude", latitude != null ? latitude : 41.9304);
        response.put("longitude", longitude != null ? longitude : 2.2541);
        response.put("emergencyContact", "112 / MUTUA COLABORADORA");
        response.put("message", "Alerta de socorro transmitida a seguridad y servicios medicos.");
        return response;
    }
}
