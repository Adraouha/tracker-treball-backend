package com.trackertreball.service;

import com.trackertreball.model.Employee;
import com.trackertreball.model.UserCredential;
import com.trackertreball.repository.EmployeeRepository;
import com.trackertreball.repository.UserCredentialRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Servicio de gestion de autenticacion, credenciales y cambio de contraseña.
 *
 * @author Hassan Adraou
 * @version 1.0.0
 * @since 2026-11-03
 */
@Service
public class AuthService {

    private final UserCredentialRepository credentialRepository;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecureRandom random = new SecureRandom();

    public AuthService(UserCredentialRepository credentialRepository,
                       EmployeeRepository employeeRepository,
                       PasswordEncoder passwordEncoder) {
        this.credentialRepository = credentialRepository;
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Map<String, Object> login(String identifier, String password) {
        if (identifier == null || password == null || identifier.isBlank() || password.isBlank()) {
            throw new IllegalArgumentException("Usuario y contraseña requeridos.");
        }

        Employee employee = null;
        UserCredential credential = null;

        if (identifier.contains("@")) {
            Optional<Employee> empOpt = employeeRepository.findByEmailIgnoreCase(identifier.trim());
            if (empOpt.isPresent()) {
                employee = empOpt.get();
                credential = credentialRepository.findByEmployeeId(employee.getId()).orElse(null);
            }
        } else {
            Optional<UserCredential> credOpt = credentialRepository.findByUsernameIgnoreCase(identifier.trim());
            if (credOpt.isPresent()) {
                credential = credOpt.get();
                employee = employeeRepository.findById(credential.getEmployeeId()).orElse(null);
            }
        }

        if (employee == null || credential == null) {
            throw new IllegalArgumentException("Credenciales no válidas o usuario no encontrado.");
        }

        if (!credential.isActivo() || !employee.isActivo()) {
            throw new IllegalStateException("Esta cuenta de usuario se encuentra desactivada.");
        }

        boolean passwordMatches = passwordEncoder.matches(password, credential.getPasswordHash())
                || password.equals(credential.getPasswordPlainHint());

        if (!passwordMatches) {
            throw new IllegalArgumentException("Contraseña incorrecta.");
        }

        credential.setUltimoAcceso(LocalDateTime.now());
        credentialRepository.save(credential);

        Map<String, Object> userData = new HashMap<>();
        userData.put("id", employee.getId());
        userData.put("name", employee.getNombre());
        userData.put("nombre", employee.getNombre());
        userData.put("email", employee.getEmail());
        userData.put("username", credential.getUsername());
        userData.put("role", employee.getRol() != null ? employee.getRol() : "EMPLOYEE");
        userData.put("rol", employee.getRol() != null ? employee.getRol() : "EMPLOYEE");
        userData.put("puesto", employee.getPuesto());
        userData.put("department", employee.getDepartamento());
        userData.put("departamento", employee.getDepartamento());
        userData.put("centroTrabajo", employee.getCentroTrabajo());
        userData.put("workCenterId", employee.getWorkCenterId());
        userData.put("nss", employee.getNss());
        userData.put("estadoCivil", employee.getEstadoCivil());
        userData.put("domicilio", employee.getDomicilio());
        userData.put("telefono", employee.getTelefono());
        userData.put("fechaNacimiento", employee.getFechaNacimiento() != null ? employee.getFechaNacimiento().toString() : null);
        userData.put("mustChangePassword", credential.isMustChangePassword());

        String[] parts = employee.getNombre().trim().split("\\s+");
        String initials = parts.length >= 2 
            ? ("" + parts[0].charAt(0) + parts[parts.length - 1].charAt(0)).toUpperCase()
            : employee.getNombre().substring(0, Math.min(2, employee.getNombre().length())).toUpperCase();
        userData.put("avatar", initials);

        Map<String, Object> response = new HashMap<>();
        response.put("status", "SUCCESS");
        response.put("token", UUID.randomUUID().toString());
        response.put("user", userData);
        return response;
    }

    @Transactional
    public Map<String, Object> changePassword(Long employeeId, String oldPassword, String newPassword) {
        if (newPassword == null || newPassword.length() < 6) {
            throw new IllegalArgumentException("La nueva contraseña debe tener al menos 6 caracteres.");
        }

        UserCredential credential = credentialRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("No se encontraron credenciales para el empleado #" + employeeId));

        boolean oldMatches = passwordEncoder.matches(oldPassword, credential.getPasswordHash())
                || oldPassword.equals(credential.getPasswordPlainHint());

        if (!oldMatches) {
            throw new IllegalArgumentException("La contraseña actual no es correcta.");
        }

        credential.setPasswordHash(passwordEncoder.encode(newPassword));
        credential.setPasswordPlainHint(null);
        credential.setMustChangePassword(false);
        credentialRepository.save(credential);

        Map<String, Object> res = new HashMap<>();
        res.put("status", "SUCCESS");
        res.put("message", "Contraseña actualizada correctamente.");
        return res;
    }

    @Transactional
    public String createCredentialsForEmployee(Employee employee) {
        String baseUsername = employee.getEmail().split("@")[0].toLowerCase().replaceAll("[^a-z0-9.]", "");
        String username = baseUsername;
        int counter = 1;
        while (credentialRepository.findByUsernameIgnoreCase(username).isPresent()) {
            username = baseUsername + counter++;
        }

        String rawPassword = generateRandomPassword();
        String encoded = passwordEncoder.encode(rawPassword);

        UserCredential credential = new UserCredential(employee.getId(), username, encoded, rawPassword);
        credential.setMustChangePassword(true);
        credentialRepository.save(credential);

        return rawPassword;
    }

    public String generateRandomPassword() {
        String upper = "ABCDEFGHJKLMNPQRSTUVWXYZ";
        String lower = "abcdefghijkmnopqrstuvwxyz";
        String digits = "23456789";
        String special = "!@#$%&*";
        StringBuilder sb = new StringBuilder();
        sb.append("Trk");
        sb.append(upper.charAt(random.nextInt(upper.length())));
        sb.append(lower.charAt(random.nextInt(lower.length())));
        sb.append(digits.charAt(random.nextInt(digits.length())));
        sb.append(digits.charAt(random.nextInt(digits.length())));
        sb.append(special.charAt(random.nextInt(special.length())));
        return sb.toString();
    }
}

