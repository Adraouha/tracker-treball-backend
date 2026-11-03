package com.trackertreball.repository;

import com.trackertreball.model.WorkCenter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WorkCenterRepository extends JpaRepository<WorkCenter, Long> {
    Optional<WorkCenter> findByNombreIgnoreCase(String nombre);
}

