package com.trackertreball.repository;

import com.trackertreball.model.SafetyAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio de acceso a datos para la entidad {@link SafetyAlert}.
 * <p>
 * Gestiona el almacenamiento de avisos meteorologicos y alertas de seguridad de Proteccion Civil y PRL.
 * </p>
 *
 * @author Hassan Adraou
 * @version 1.0.0
 * @since 2026-11-02
 * @see com.trackertreball.model.SafetyAlert
 * @see org.springframework.data.jpa.repository.JpaRepository
 */
@Repository
public interface SafetyAlertRepository extends JpaRepository<SafetyAlert, Long> {

    /**
     * Obtiene la lista de alertas de prevencion de riesgos laborales que estan actualmente vigentes.
     *
     * @return Lista de alertas activas.
     */
    List<SafetyAlert> findByActiveTrue();
}
