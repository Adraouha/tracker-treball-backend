package com.trackertreball.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad que registra las interrupciones, pausas y descansos laborales diarios.
 * <p>
 * Permite tipificar las pausas segun su naturaleza:
 * <ul>
 *   <li><b>PAUSA / CAFE:</b> Descansos cortos de hidratacion o pausa de cafe.</li>
 *   <li><b>COMIDA:</b> Pausa reglamentaria de almuerzo/comida para jornadas continuadas o partidas.</li>
 *   <li><b>PRL_VISUAL:</b> Descanso ergonomico obligatorio de 5 minutos por cada hora de pantalla (PRL).</li>
 * </ul>
 * Se asocia de manera relacional al identificador de la jornada {@link AttendanceRecord}.
 * </p>
 *
 * @author Hassan Adraou
 * @version 1.0.0
 * @since 2026-11-02
 * @see AttendanceRecord
 */
@Entity
@Table(name = "break_records")
public class BreakRecord {

    /**
     * Identificador unico del registro de pausa. Clave primaria autoincremental.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Identificador de la jornada laboral a la que pertenece esta pausa.
     */
    @Column(name = "attendance_id", nullable = false)
    private Long attendanceId;

    /**
     * Tipo de descanso (ej. PAUSA, COMIDA, PRL_VISUAL).
     */
    @Column(name = "break_type", nullable = false, length = 30)
    private String breakType = "PAUSA";

    /**
     * Marca temporal exacta de inicio del descanso.
     */
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    /**
     * Marca temporal exacta de finalizacion del descanso (null mientras este activa).
     */
    @Column(name = "end_time")
    private LocalDateTime endTime;

    /**
     * Duracion total de la pausa calculada en minutos.
     */
    @Column(name = "duration_minutes")
    private Integer durationMinutes = 0;

    /**
     * Constructor por defecto para JPA.
     */
    public BreakRecord() {}

    /**
     * Constructor para iniciar una nueva pausa laboral.
     *
     * @param attendanceId Identificador de la jornada principal.
     * @param breakType Categoria del descanso (PAUSA, COMIDA, PRL_VISUAL).
     * @param startTime Marca temporal de inicio.
     */
    public BreakRecord(Long attendanceId, String breakType, LocalDateTime startTime) {
        this.attendanceId = attendanceId;
        this.breakType = breakType;
        this.startTime = startTime;
        this.durationMinutes = 0;
    }

    /**
     * Obtiene el identificador unico de la pausa.
     * @return Identificador Long.
     */
    public Long getId() { return id; }

    /**
     * Asigna el identificador unico de la pausa.
     * @param id Identificador Long.
     */
    public void setId(Long id) { this.id = id; }

    /**
     * Obtiene el ID de la jornada vinculada.
     * @return ID de jornada.
     */
    public Long getAttendanceId() { return attendanceId; }

    /**
     * Asigna el ID de la jornada vinculada.
     * @param attendanceId ID de jornada.
     */
    public void setAttendanceId(Long attendanceId) { this.attendanceId = attendanceId; }

    /**
     * Obtiene el tipo de descanso.
     * @return Categoria en texto.
     */
    public String getBreakType() { return breakType; }

    /**
     * Asigna el tipo de descanso.
     * @param breakType Categoria en texto.
     */
    public void setBreakType(String breakType) { this.breakType = breakType; }

    /**
     * Obtiene la hora de inicio de la pausa.
     * @return Fecha y hora de inicio.
     */
    public LocalDateTime getStartTime() { return startTime; }

    /**
     * Asigna la hora de inicio de la pausa.
     * @param startTime Fecha y hora de inicio.
     */
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    /**
     * Obtiene la hora de fin de la pausa.
     * @return Fecha y hora de finalizacion.
     */
    public LocalDateTime getEndTime() { return endTime; }

    /**
     * Asigna la hora de fin de la pausa.
     * @param endTime Fecha y hora de finalizacion.
     */
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    /**
     * Obtiene la duracion de la pausa en minutos.
     * @return Minutos computados.
     */
    public Integer getDurationMinutes() { return durationMinutes; }

    /**
     * Asigna la duracion de la pausa en minutos.
     * @param durationMinutes Minutos computados.
     */
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }
}
