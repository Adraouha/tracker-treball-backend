package com.trackertreball.repository;

import com.trackertreball.model.VacationRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio Spring Data JPA para la entidad {@link VacationRequest}.
 *
 * @author Hassan Adraou
 * @version 1.0.0
 * @since 2026-11-03
 */
@Repository
public interface VacationRepository extends JpaRepository<VacationRequest, Long> {

    List<VacationRequest> findByEmployeeIdOrderByStartDateDesc(Long employeeId);

    List<VacationRequest> findAllByOrderByCreatedAtDesc();
}

