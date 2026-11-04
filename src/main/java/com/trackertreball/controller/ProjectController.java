package com.trackertreball.controller;

import com.trackertreball.model.Project;
import com.trackertreball.repository.ProjectRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controlador REST para la gestion de proyectos empresariales, Sprints y dedicacion horaria.
 *
 * @author Hassan Adraou
 * @version 1.0.0
 * @since 2026-11-03
 */
@RestController
@RequestMapping("/api/v1/projects")
public class ProjectController {

    private final ProjectRepository projectRepository;

    public ProjectController(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @GetMapping
    public ResponseEntity<List<Project>> getAllProjects() {
        return ResponseEntity.ok(projectRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<?> createProject(@RequestBody Map<String, Object> body) {
        Project p = new Project();
        p.setName(body.getOrDefault("name", "Nuevo Proyecto").toString());
        p.setStatus(body.getOrDefault("status", "IN_PROGRESS").toString());
        p.setSprintProgress(body.containsKey("sprintProgress") ? Integer.parseInt(body.get("sprintProgress").toString()) : 0);
        p.setTeamMembers(body.containsKey("teamMembers") ? Integer.parseInt(body.get("teamMembers").toString()) : 1);
        p.setDedicatedHours(body.containsKey("dedicatedHours") ? Integer.parseInt(body.get("dedicatedHours").toString()) : 0);

        Project saved = projectRepository.save(p);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "status", "SUCCESS",
                "project", saved,
                "message", "Proyecto registrado correctamente."
        ));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProject(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        return projectRepository.findById(id).map(p -> {
            if (body.containsKey("name")) p.setName(body.get("name").toString());
            if (body.containsKey("status")) p.setStatus(body.get("status").toString());
            if (body.containsKey("sprintProgress")) p.setSprintProgress(Integer.parseInt(body.get("sprintProgress").toString()));
            if (body.containsKey("teamMembers")) p.setTeamMembers(Integer.parseInt(body.get("teamMembers").toString()));
            if (body.containsKey("dedicatedHours")) p.setDedicatedHours(Integer.parseInt(body.get("dedicatedHours").toString()));

            Project saved = projectRepository.save(p);
            return ResponseEntity.ok(Map.of(
                    "status", "SUCCESS",
                    "project", saved,
                    "message", "Proyecto actualizado correctamente."
            ));
        }).orElse(ResponseEntity.notFound().build());
    }
}

