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
 * @version 1.0.0
 * @since 2026-11-02
 * @see com.trackertreball.model.AttendanceRecord
 * @see org.springframework.data.jpa.repository.JpaRepository
 */
@Repository
public interface AttendanceRepository extends JpaRepository<AttendanceRecord, Long> {

    /**
     * Busca el registro de jornada laboral de un empleado en una fecha concreta.
     *
     * @param employeeId Identificador unico del empleado.
     * @param workDate Fecha de trabajo a consultar.
     * @return {@link Optional} con el registro de jornada del dia si existe.
     */
    Optional<AttendanceRecord> findByEmployeeIdAndWorkDate(Long employeeId, LocalDate workDate);

    /**
     * Obtiene el historial completo de jornadas de un empleado ordenado cronologicamente de forma descendente.
     *
     * @param employeeId Identificador unico del empleado.
     * @return Lista de jornadas laborales ordenadas por fecha mas reciente.
     */
    List<AttendanceRecord> findByEmployeeIdOrderByWorkDateDesc(Long employeeId);
}
