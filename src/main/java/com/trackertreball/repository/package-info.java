/**
 * Paquete de interfaces de persistencia y repositorios de datos Spring Data JPA.
 * <p>
 * Abstrae el acceso a la base de datos relacional MySQL mediante el patron Repository:
 * <ul>
 *   <li>{@link com.trackertreball.repository.EmployeeRepository}: Consultas y busquedas de empleados.</li>
 *   <li>{@link com.trackertreball.repository.AttendanceRepository}: Auditoria de jornadas laborales diarias.</li>
 *   <li>{@link com.trackertreball.repository.BreakRepository}: Consulta de pausas activas e historicas.</li>
 *   <li>{@link com.trackertreball.repository.ProjectRepository}: Consulta de proyectos por estado de ejecucion.</li>
 *   <li>{@link com.trackertreball.repository.SafetyAlertRepository}: Filtrado de alertas preventivas en vigor.</li>
 *   <li>{@link com.trackertreball.repository.IncidentRepository}: Historico de incidentes reportados.</li>
 * </ul>
 * </p>
 *
 * @author Hassan Adraou
 * @version 1.0.0
 * @since 2026-11-02
 */
package com.trackertreball.repository;
