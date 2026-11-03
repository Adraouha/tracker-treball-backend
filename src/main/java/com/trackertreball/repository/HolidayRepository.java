package com.trackertreball.repository;

import com.trackertreball.model.Holiday;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface HolidayRepository extends JpaRepository<Holiday, Long> {

    @Query("SELECT h FROM Holiday h WHERE (h.workCenterId = :workCenterId OR h.workCenterId IS NULL) AND h.ano = :ano ORDER BY h.fecha ASC")
    List<Holiday> findByWorkCenterOrNational(@Param("workCenterId") Long workCenterId, @Param("ano") Integer ano);

    @Query("SELECT h FROM Holiday h WHERE (h.workCenterId = :workCenterId OR h.workCenterId IS NULL) AND h.fecha >= :today ORDER BY h.fecha ASC LIMIT 1")
    Optional<Holiday> findNextHoliday(@Param("workCenterId") Long workCenterId, @Param("today") LocalDate today);
}

