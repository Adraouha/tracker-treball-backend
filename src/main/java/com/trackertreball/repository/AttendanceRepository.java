package com.trackertreball.repository;

import com.trackertreball.model.AttendanceRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio de acceso a datos para la entidad {@link AttendanceRecord}.
 * <p>
 * Gestiona la persistencia de las jornadas laborales obligatorias y su historial cronologico,
 * permitiendo auditar entradas, salidas y computos horarios conforme al RDL 8/2019.
 * </p>
 *
 * @author Hassan Adraou
 * @version 2.0.0
 * @since 2026-11-02
 * @see com.trackertreball.model.AttendanceRecord
 * @see org.springframework.data.jpa.repository.JpaRepository
 */
@Repository
public interface AttendanceRepository extends JpaRepository<AttendanceRecord, Long> {

    /**
     * Busca el primer registro de jornada laboral de un empleado en una fecha concreta ordenado descendentemente.
     * Evita excepciones de no-unicidad en caso de reentradas o turnos partidos.
     */
    Optional<AttendanceRecord> findFirstByEmployeeIdAndWorkDateOrderByClockInTimeDesc(Long employeeId, LocalDate workDate);

    /**
     * Localiza la jornada de un empleado segun fecha y estado (ej. OPEN o CLOSED).
     */
    Optional<AttendanceRecord> findFirstByEmployeeIdAndWorkDateAndStatusOrderByClockInTimeDesc(Long employeeId, LocalDate workDate, String status);

    /**
     * Busca el registro de jornada laboral de un empleado en una fecha concreta (compatibilidad legacy).
     */
    Optional<AttendanceRecord> findByEmployeeIdAndWorkDate(Long employeeId, LocalDate workDate);

    /**
     * Obtiene el historial completo de jornadas de un empleado ordenado cronologicamente de forma descendente.
     */
    List<AttendanceRecord> findByEmployeeIdOrderByWorkDateDesc(Long employeeId);

    /**
     * Obtiene todos los registros de jornada de un empleado.
     */
    List<AttendanceRecord> findByEmployeeId(Long employeeId);
}
