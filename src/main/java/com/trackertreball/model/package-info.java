/**
 * Paquete de entidades del dominio y modelos relacionales mapeados con JPA / Hibernate.
 * <p>
 * Representa las tablas nucleares de la base de datos:
 * <ul>
 *   <li>{@link com.trackertreball.model.Employee}: Datos de empleados de la plantilla corporativa.</li>
 *   <li>{@link com.trackertreball.model.AttendanceRecord}: Fichajes diarios de entrada/salida y hash SHA-256.</li>
 *   <li>{@link com.trackertreball.model.BreakRecord}: Pausas, comidas y descansos visuales obligatorios.</li>
 *   <li>{@link com.trackertreball.model.Project}: Proyectos, miembros asignados y avance de Sprints.</li>
 *   <li>{@link com.trackertreball.model.SafetyAlert}: Alertas meteorologicas y de proteccion civil de PRL.</li>
 *   <li>{@link com.trackertreball.model.WorkIncident}: Reportes preventivos de incidentes y Near Misses.</li>
 * </ul>
 * </p>
 *
 * @author Hassan Adraou
 * @version 1.0.0
 * @since 2026-11-02
 */
package com.trackertreball.model;
