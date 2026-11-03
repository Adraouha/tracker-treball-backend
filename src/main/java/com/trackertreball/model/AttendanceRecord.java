package com.trackertreball.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidad principal que almacena el registro legal de jornada laboral diaria inmutable.
 * <p>
 * Diseñada para dar cumplimiento al Real Decreto-ley 8/2019 de registro de jornada obligatorio.
 * Almacena la fecha de la jornada, hora exacta de entrada (clock-in), hora de salida (clock-out),
 * modalidad de fichaje (MANUAL, BIOMETRICO, TELETRABAJO), minutos efectivos computados, estado de
 * apertura/cierre y el hash criptografico SHA-256 que garantiza la no alteracion de los datos.
 * </p>
 *
 * @author Hassan Adraou
 * @version 1.0.0
 * @since 2026-11-02
 */
@Entity
@Table(name = "attendance_records")
public class AttendanceRecord {

    /**
     * Identificador unico del registro de asistencia. Clave primaria autoincremental.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Identificador del empleado propietario del registro.
     */
    @Column(name = "employee_id", nullable = false)
    private Long employeeId;

    /**
     * Fecha de calendario laboral a la que corresponde la jornada.
     */
    @Column(name = "work_date", nullable = false)
    private LocalDate workDate;

    /**
     * Marca temporal exacta de inicio de la jornada laboral (entrada).
     */
    @Column(name = "clock_in_time", nullable = false)
    private LocalDateTime clockInTime;

    /**
     * Marca temporal exacta de cierre de la jornada laboral (salida).
     */
    @Column(name = "clock_out_time")
    private LocalDateTime clockOutTime;

    /**
     * Modalidad empleada para registrar el inicio de jornada (ej. MANUAL, BIOMETRIC, REMOTE).
     */
    @Column(nullable = false, length = 30)
    private String modality = "MANUAL";

    /**
     * Minutos totales netos trabajados computados al cerrar la jornada, descontando descansos.
     */
    @Column(name = "worked_minutes", nullable = false)
    private Integer workedMinutes = 0;

    /**
     * Estado actual de la jornada: {@code OPEN} (en curso) o {@code CLOSED} (finalizada).
     */
    @Column(nullable = false, length = 30)
    private String status = "OPEN";

    /**
     * Hash criptografico SHA-256 de sellado de la jornada para auditorias de la Inspeccion de Trabajo.
     */
    @Column(name = "record_hash", length = 64)
    private String recordHash;

    /**
     * Constructor por defecto para JPA.
     */
    public AttendanceRecord() {}

    /**
     * Constructor para inicializar una nueva jornada abierta.
     *
     * @param employeeId Identificador del empleado.
     * @param workDate Fecha de la jornada laboral.
     * @param clockInTime Hora exacta de inicio.
     * @param modality Modalidad de entrada (MANUAL, BIOMETRIC, REMOTE).
     */
    public AttendanceRecord(Long employeeId, LocalDate workDate, LocalDateTime clockInTime, String modality) {
        this.employeeId = employeeId;
        this.workDate = workDate;
        this.clockInTime = clockInTime;
        this.modality = modality;
        this.status = "OPEN";
        this.workedMinutes = 0;
    }

    /**
     * Obtiene el identificador del registro.
     * @return Identificador unico.
     */
    public Long getId() { return id; }

    /**
     * Asigna el identificador del registro.
     * @param id Identificador unico.
     */
    public void setId(Long id) { this.id = id; }

    /**
     * Obtiene el ID del empleado titular.
     * @return ID del empleado.
     */
    public Long getEmployeeId() { return employeeId; }

    /**
     * Asigna el ID del empleado titular.
     * @param employeeId ID del empleado.
     */
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }

    /**
     * Obtiene la fecha de la jornada.
     * @return Fecha LocalDate.
     */
    public LocalDate getWorkDate() { return workDate; }

    /**
     * Asigna la fecha de la jornada.
     * @param workDate Fecha LocalDate.
     */
    public void setWorkDate(LocalDate workDate) { this.workDate = workDate; }

    /**
     * Obtiene la marca temporal de entrada.
     * @return Hora de entrada LocalDateTime.
     */
    public LocalDateTime getClockInTime() { return clockInTime; }

    /**
     * Asigna la marca temporal de entrada.
     * @param clockInTime Hora de entrada LocalDateTime.
     */
    public void setClockInTime(LocalDateTime clockInTime) { this.clockInTime = clockInTime; }

    /**
     * Obtiene la marca temporal de salida.
     * @return Hora de salida LocalDateTime o null si sigue abierta.
     */
    public LocalDateTime getClockOutTime() { return clockOutTime; }

    /**
     * Asigna la marca temporal de salida.
     * @param clockOutTime Hora de salida LocalDateTime.
     */
    public void setClockOutTime(LocalDateTime clockOutTime) { this.clockOutTime = clockOutTime; }

    /**
     * Obtiene la modalidad del fichaje.
     * @return Modalidad en texto.
     */
    public String getModality() { return modality; }

    /**
     * Asigna la modalidad del fichaje.
     * @param modality Modalidad en texto.
     */
    public void setModality(String modality) { this.modality = modality; }

    /**
     * Obtiene los minutos totales trabajados.
     * @return Cantidad de minutos.
     */
    public Integer getWorkedMinutes() { return workedMinutes; }

    /**
     * Asigna los minutos totales trabajados.
     * @param workedMinutes Cantidad de minutos.
     */
    public void setWorkedMinutes(Integer workedMinutes) { this.workedMinutes = workedMinutes; }

    /**
     * Obtiene el estado de la jornada (OPEN, CLOSED).
     * @return Estado de la jornada.
     */
    public String getStatus() { return status; }

    /**
     * Asigna el estado de la jornada (OPEN, CLOSED).
     * @param status Estado de la jornada.
     */
    public void setStatus(String status) { this.status = status; }

    /**
     * Obtiene el hash criptografico de integridad SHA-256.
     * @return Cadena hexadecimal con el hash SHA-256.
     */
    public String getRecordHash() { return recordHash; }

    /**
     * Asigna el hash criptografico de integridad SHA-256.
     * @param recordHash Cadena hexadecimal con el hash SHA-256.
     */
    public void setRecordHash(String recordHash) { this.recordHash = recordHash; }
}
