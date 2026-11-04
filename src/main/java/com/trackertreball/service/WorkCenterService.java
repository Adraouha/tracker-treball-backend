package com.trackertreball.service;

import com.trackertreball.model.Holiday;
import com.trackertreball.model.WorkCenter;
import com.trackertreball.repository.HolidayRepository;
import com.trackertreball.repository.WorkCenterRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Servicio de gestion de Centros de Trabajo y Festivos laborales asociados.
 *
 * @author Hassan Adraou
 * @version 1.0.0
 * @since 2026-11-03
 */
@Service
public class WorkCenterService {

    private final WorkCenterRepository workCenterRepository;
    private final HolidayRepository holidayRepository;

    public WorkCenterService(WorkCenterRepository workCenterRepository, HolidayRepository holidayRepository) {
        this.workCenterRepository = workCenterRepository;
        this.holidayRepository = holidayRepository;
    }

    public List<WorkCenter> getAllWorkCenters() {
        return workCenterRepository.findAll();
    }

    public Optional<WorkCenter> getById(Long id) {
        return workCenterRepository.findById(id);
    }

    public List<Holiday> getHolidaysByWorkCenter(Long workCenterId, Integer ano) {
        int year = (ano != null) ? ano : 2026;
        List<Holiday> holidays = holidayRepository.findByWorkCenterOrNational(workCenterId, year);
        if (holidays.isEmpty() && year != 2026) {
            holidays = holidayRepository.findByWorkCenterOrNational(workCenterId, 2026);
        }
        return holidays;
    }

    public Optional<Holiday> getNextHoliday(Long workCenterId) {
        return holidayRepository.findNextHoliday(workCenterId, LocalDate.now());
    }
}

