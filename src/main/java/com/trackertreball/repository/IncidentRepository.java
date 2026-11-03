package com.trackertreball.repository;

import com.trackertreball.model.WorkIncident;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio de acceso a datos para la entidad {@link WorkIncident}.
 * <p>
 * Permite registrar y consultar incidentes de seguridad laboral y condiciones peligrosas notificadas.
 * </p>
 *
 * @author Hassan Adraou
 * @version 1.0.0
 * @since 2026-11-02
 * @see com.trackertreball.model.WorkIncident
 * @see org.springframework.data.jpa.repository.JpaRepository
 */
@Repository
public interface IncidentRepository extends JpaRepository<WorkIncident, Long> {

    /**
     * Recupera todos los incidentes reportados por un empleado en particular.
     *
     * @param reporterEmployeeId Identificador del empleado informador.
     * @return Lista de incidentes notificados por dicho empleado.
     */
    List<WorkIncident> findByReporterEmployeeId(Long reporterEmployeeId);
}
