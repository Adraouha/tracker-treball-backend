package com.trackertreball.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad JPA que almacena las credenciales de acceso seguro de los usuarios en Tracker Treball.
 *
 * @author Hassan Adraou
 * @version 1.0.0
 * @since 2026-11-03
 */
@Entity
@Table(name = "user_credentials")
public class UserCredential {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employee_id", nullable = false, unique = true)
    private Long employeeId;

    @Column(nullable = false, unique = true, length = 80)
    private String username;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "password_plain_hint", length = 100)
    private String passwordPlainHint;

    @Column(name = "must_change_password", nullable = false)
    private boolean mustChangePassword = false;

    @Column(nullable = false)
    private boolean activo = true;

    @Column(name = "ultimo_acceso")
    private LocalDateTime ultimoAcceso;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public UserCredential() {}

    public UserCredential(Long employeeId, String username, String passwordHash, String passwordPlainHint) {
        this.employeeId = employeeId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.passwordPlainHint = passwordPlainHint;
        this.mustChangePassword = false;
        this.activo = true;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getPasswordPlainHint() { return passwordPlainHint; }
    public void setPasswordPlainHint(String passwordPlainHint) { this.passwordPlainHint = passwordPlainHint; }

    public boolean isMustChangePassword() { return mustChangePassword; }
    public void setMustChangePassword(boolean mustChangePassword) { this.mustChangePassword = mustChangePassword; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public LocalDateTime getUltimoAcceso() { return ultimoAcceso; }
    public void setUltimoAcceso(LocalDateTime ultimoAcceso) { this.ultimoAcceso = ultimoAcceso; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

