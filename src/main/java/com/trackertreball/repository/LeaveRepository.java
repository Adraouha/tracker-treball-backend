package com.trackertreball.repository;

import com.trackertreball.model.MedicalLeave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio JPA para la gestion de bajas medicas.
 *
 * @author Hassan Adraou
 * @version 1.0.0
 * @since 2026-11-03
 */
@Repository
public interface LeaveRepository extends JpaRepository<MedicalLeave, Long> {

    List<MedicalLeave> findAllByOrderByFechaInicioDesc();

    List<MedicalLeave> findByEmployeeIdOrderByFechaInicioDesc(Long employeeId);
}

