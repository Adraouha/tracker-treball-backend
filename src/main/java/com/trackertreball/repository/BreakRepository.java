package com.trackertreball.repository;

import com.trackertreball.model.BreakRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio de acceso a datos para la entidad {@link BreakRecord}.
 * <p>
 * Permite consultar, registrar y cerrar los descansos laborales asociados a una jornada activa.
 * </p>
 *
 * @author Hassan Adraou
 * @version 1.0.0
 * @since 2026-11-02
 * @see com.trackertreball.model.BreakRecord
 * @see org.springframework.data.jpa.repository.JpaRepository
 */
@Repository
public interface BreakRepository extends JpaRepository<BreakRecord, Long> {

    /**
     * Recupera todas las pausas y descansos asociados a una jornada laboral especifica.
     *
     * @param attendanceId Identificador de la jornada laboral.
     * @return Lista de descansos vinculados a la jornada.
     */
    List<BreakRecord> findByAttendanceId(Long attendanceId);

    /**
     * Localiza la pausa que se encuentra actualmente en curso (sin hora de fin registrada).
     *
     * @param attendanceId Identificador de la jornada laboral.
     * @return {@link Optional} con el descanso activo mas reciente.
     */
    Optional<BreakRecord> findFirstByAttendanceIdAndEndTimeIsNullOrderByStartTimeDesc(Long attendanceId);
}
