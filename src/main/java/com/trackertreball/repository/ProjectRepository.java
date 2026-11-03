package com.trackertreball.repository;

import com.trackertreball.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio de acceso a datos para la entidad {@link Project}.
 * <p>
 * Facilita la consulta de proyectos activos, finalizados o en revision dentro de la organizacion.
 * </p>
 *
 * @author Hassan Adraou
 * @version 1.0.0
 * @since 2026-11-02
 * @see com.trackertreball.model.Project
 * @see org.springframework.data.jpa.repository.JpaRepository
 */
@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    /**
     * Recupera todos los proyectos que coinciden con un estado determinado.
     *
     * @param status Estado de ejecucion (IN_PROGRESS, COMPLETED, ON_HOLD).
     * @return Lista de proyectos en dicho estado.
     */
    List<Project> findByStatus(String status);
}
