package com.trackertreball.controller;

import com.trackertreball.service.ReminderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controlador REST para recordatorios, clima del centro de trabajo, festivos y avisos de compañeros.
 *
 * @author Hassan Adraou
 * @version 1.0.0
 * @since 2026-11-03
 */
@RestController
@RequestMapping("/api/v1/reminders")
public class ReminderController {

    private final ReminderService reminderService;

    public ReminderController(ReminderService reminderService) {
        this.reminderService = reminderService;
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<?> getRemindersForEmployee(@PathVariable Long employeeId) {
        try {
            Map<String, Object> data = reminderService.getRemindersForEmployee(employeeId);
            return ResponseEntity.ok(data);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("status", "ERROR", "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("status", "ERROR", "message", "Error al recuperar recordatorios."));
        }
    }
}

