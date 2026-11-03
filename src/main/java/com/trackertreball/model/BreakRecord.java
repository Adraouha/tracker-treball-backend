package com.trackertreball.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad que registra las interrupciones, pausas y descansos laborales diarios.
 * <p>
 * Gestiona el tiempo estipulado para cada tipo de descanso, calcula los minutos adicionales
 * o de exceso consumidos por el empleado y genera mensajes de notificación automáticos.
 * </p>
 *
 * @author Hassan Adraou
 * @version 2.0.0
 * @since 2026-11-02
 */
@Entity
@Table(name = "break_records")
public class BreakRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "attendance_id", nullable = false)
    private Long attendanceId;

    @Column(name = "break_type", nullable = false, length = 30)
    private String breakType = "PAUSA";

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "duration_minutes")
    private Integer durationMinutes = 0;

    @Column(name = "tiempo_estipulado_minutos", nullable = false)
    private Integer tiempoEstipuladoMinutos = 15;

    @Column(name = "minutos_adicionales", nullable = false)
    private Integer minutosAdicionales = 0;

    @Column(name = "mensaje_notificacion", length = 255)
    private String mensajeNotificacion;

    public BreakRecord() {}

    public BreakRecord(Long attendanceId, String breakType, LocalDateTime startTime, Integer tiempoEstipuladoMinutos) {
        this.attendanceId = attendanceId;
        this.breakType = breakType;
        this.startTime = startTime;
        this.tiempoEstipuladoMinutos = tiempoEstipuladoMinutos != null ? tiempoEstipuladoMinutos : 15;
        this.durationMinutes = 0;
        this.minutosAdicionales = 0;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getAttendanceId() { return attendanceId; }
    public void setAttendanceId(Long attendanceId) { this.attendanceId = attendanceId; }

    public String getBreakType() { return breakType; }
    public void setBreakType(String breakType) { this.breakType = breakType; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }

    public Integer getTiempoEstipuladoMinutos() { return tiempoEstipuladoMinutos; }
    public void setTiempoEstipuladoMinutos(Integer tiempoEstipuladoMinutos) { this.tiempoEstipuladoMinutos = tiempoEstipuladoMinutos; }

    public Integer getMinutosAdicionales() { return minutosAdicionales; }
    public void setMinutosAdicionales(Integer minutosAdicionales) { this.minutosAdicionales = minutosAdicionales; }

    public String getMensajeNotificacion() { return mensajeNotificacion; }
    public void setMensajeNotificacion(String mensajeNotificacion) { this.mensajeNotificacion = mensajeNotificacion; }
}
