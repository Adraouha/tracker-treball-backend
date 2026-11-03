package com.trackertreball.controller;

import com.trackertreball.model.Holiday;
import com.trackertreball.model.WorkCenter;
import com.trackertreball.service.WorkCenterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para consultar los centros de trabajo oficiales y sus calendarios laborales.
 *
 * @author Hassan Adraou
 * @version 1.0.0
 * @since 2026-11-03
 */
@RestController
@RequestMapping("/api/v1/work-centers")
public class WorkCenterController {

    private final WorkCenterService workCenterService;

    public WorkCenterController(WorkCenterService workCenterService) {
        this.workCenterService = workCenterService;
    }

    @GetMapping
    public ResponseEntity<List<WorkCenter>> getAllWorkCenters() {
        return ResponseEntity.ok(workCenterService.getAllWorkCenters());
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkCenter> getById(@PathVariable Long id) {
        return workCenterService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/holidays")
    public ResponseEntity<List<Holiday>> getHolidays(@PathVariable Long id, @RequestParam(required = false) Integer ano) {
        return ResponseEntity.ok(workCenterService.getHolidaysByWorkCenter(id, ano));
    }
}

