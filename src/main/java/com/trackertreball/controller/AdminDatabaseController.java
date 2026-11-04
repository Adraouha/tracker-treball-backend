package com.trackertreball.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Controlador REST para el Gestor Visual de Base de Datos (Visor / Adminer Integrado).
 * Permite al rol ADMIN realizar operaciones directas de consulta y CRUD sobre las tablas
 * de la base de datos sin requerir herramientas externas de escritorio como DBeaver.
 *
 * @author Hassan Adraou
 * @version 1.0.0
 * @since 2026-11-04
 */
@RestController
@RequestMapping("/api/v1/admin/tables")
public class AdminDatabaseController {

    private final JdbcTemplate jdbcTemplate;

    // Lista blanca estricta de tablas accesibles
    private static final Map<String, String> ALLOWED_TABLES = new LinkedHashMap<>();
    static {
        ALLOWED_TABLES.put("employees", "Directorio y Ficha de Empleados");
        ALLOWED_TABLES.put("work_centers", "Centros de Trabajo y Sedes");
        ALLOWED_TABLES.put("work_center_holidays", "Calendario de Festivos Oficiales");
        ALLOWED_TABLES.put("vacation_requests", "Solicitudes y Balances de Vacaciones");
        ALLOWED_TABLES.put("medical_leaves", "Bajas Médicas e Incapacidades");
        ALLOWED_TABLES.put("projects", "Proyectos y Horas Asignadas");
        ALLOWED_TABLES.put("attendance_records", "Registros de Fichajes de Jornada");
        ALLOWED_TABLES.put("break_records", "Pausas, Descansos y Minutos Adicionales");
        ALLOWED_TABLES.put("safety_alerts", "Alertas y Notificaciones PRL");
    }

    private static final Pattern SAFE_IDENTIFIER = Pattern.compile("^[a-zA-Z0-9_]+$");

    public AdminDatabaseController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private void checkManagerOrAdminAccess(String callerRole) {
        if (callerRole == null || 
            (!"ADMIN".equalsIgnoreCase(callerRole.trim()) && !"MANAGER".equalsIgnoreCase(callerRole.trim()))) {
            throw new SecurityException("Acceso restringido. Se requiere rol MANAGER o ADMIN para acceder al gestor visual de base de datos.");
        }
    }

    private void checkAdminDeleteAccess(String callerRole) {
        if (callerRole == null || !"ADMIN".equalsIgnoreCase(callerRole.trim())) {
            throw new SecurityException("Acceso denegado: El rol MANAGER tiene permisos completos de consulta, creación y modificación de registros en la base de datos, pero la eliminación física definitiva está reservada exclusivamente al Director General / Administrador.");
        }
    }

    private String validateTable(String tableName) {
        if (tableName == null || !ALLOWED_TABLES.containsKey(tableName.toLowerCase())) {
            throw new IllegalArgumentException("Tabla '" + tableName + "' no autorizada o no encontrada en el gestor visual.");
        }
        return tableName.toLowerCase();
    }

    @GetMapping
    public ResponseEntity<?> listTables(@RequestHeader(value = "X-User-Role", required = false) String callerRole) {
        try {
            checkManagerOrAdminAccess(callerRole);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        }

        List<Map<String, String>> tables = new ArrayList<>();
        ALLOWED_TABLES.forEach((table, desc) -> {
            Map<String, String> item = new HashMap<>();
            item.put("name", table);
            item.put("description", desc);
            tables.add(item);
        });
        return ResponseEntity.ok(Map.of("status", "SUCCESS", "tables", tables));
    }

    @GetMapping("/{tableName}")
    public ResponseEntity<?> getTableData(
            @PathVariable String tableName,
            @RequestHeader(value = "X-User-Role", required = false) String callerRole,
            @RequestParam(defaultValue = "100") int limit,
            @RequestParam(required = false) String search) {

        try {
            checkManagerOrAdminAccess(callerRole);
            String table = validateTable(tableName);

            // Obtener columnas
            String colSql = "SELECT COLUMN_NAME, DATA_TYPE, IS_NULLABLE, COLUMN_KEY " +
                            "FROM INFORMATION_SCHEMA.COLUMNS " +
                            "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = ? " +
                            "ORDER BY ORDINAL_POSITION";
            List<Map<String, Object>> columns = jdbcTemplate.queryForList(colSql, table);

            // Obtener filas
            String rowSql = "SELECT * FROM " + table + " ORDER BY id DESC LIMIT " + Math.min(limit, 500);
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(rowSql);

            return ResponseEntity.ok(Map.of(
                    "status", "SUCCESS",
                    "table", table,
                    "description", ALLOWED_TABLES.get(table),
                    "columns", columns,
                    "rows", rows,
                    "totalCount", rows.size()
            ));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{tableName}")
    public ResponseEntity<?> insertRow(
            @PathVariable String tableName,
            @RequestHeader(value = "X-User-Role", required = false) String callerRole,
            @RequestBody Map<String, Object> data) {

        try {
            checkManagerOrAdminAccess(callerRole);
            String table = validateTable(tableName);

            // Remove id if present and empty/null
            data.remove("id");

            if (data.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "No se han proporcionado campos para insertar."));
            }

            StringBuilder cols = new StringBuilder();
            StringBuilder placeholders = new StringBuilder();
            List<Object> values = new ArrayList<>();

            for (Map.Entry<String, Object> entry : data.entrySet()) {
                String key = entry.getKey();
                if (!SAFE_IDENTIFIER.matcher(key).matches()) {
                    return ResponseEntity.badRequest().body(Map.of("error", "Nombre de columna inválido: " + key));
                }
                if (cols.length() > 0) {
                    cols.append(", ");
                    placeholders.append(", ");
                }
                cols.append("`").append(key).append("`");
                placeholders.append("?");
                values.add(entry.getValue());
            }

            String sql = "INSERT INTO " + table + " (" + cols + ") VALUES (" + placeholders + ")";
            jdbcTemplate.update(sql, values.toArray());

            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "status", "SUCCESS",
                    "message", "Registro añadido con éxito a la tabla '" + table + "'."
            ));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{tableName}/{id}")
    public ResponseEntity<?> updateRow(
            @PathVariable String tableName,
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Role", required = false) String callerRole,
            @RequestBody Map<String, Object> data) {

        try {
            checkManagerOrAdminAccess(callerRole);
            String table = validateTable(tableName);

            data.remove("id");
            if (data.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "No se han proporcionado campos para actualizar."));
            }

            StringBuilder setClause = new StringBuilder();
            List<Object> values = new ArrayList<>();

            for (Map.Entry<String, Object> entry : data.entrySet()) {
                String key = entry.getKey();
                if (!SAFE_IDENTIFIER.matcher(key).matches()) {
                    return ResponseEntity.badRequest().body(Map.of("error", "Nombre de columna inválido: " + key));
                }
                if (setClause.length() > 0) {
                    setClause.append(", ");
                }
                setClause.append("`").append(key).append("` = ?");
                values.add(entry.getValue());
            }

            values.add(id);
            String sql = "UPDATE " + table + " SET " + setClause + " WHERE id = ?";
            int updated = jdbcTemplate.update(sql, values.toArray());

            if (updated > 0) {
                return ResponseEntity.ok(Map.of(
                        "status", "SUCCESS",
                        "message", "Registro #" + id + " actualizado correctamente en '" + table + "'."
                ));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Registro con ID #" + id + " no encontrado."));
            }
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{tableName}/{id}")
    public ResponseEntity<?> deleteRow(
            @PathVariable String tableName,
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Role", required = false) String callerRole) {

        try {
            checkAdminDeleteAccess(callerRole);
            String table = validateTable(tableName);

            String sql = "DELETE FROM " + table + " WHERE id = ?";
            int deleted = jdbcTemplate.update(sql, id);

            if (deleted > 0) {
                return ResponseEntity.ok(Map.of(
                        "status", "SUCCESS",
                        "message", "Registro #" + id + " eliminado permanentemente de '" + table + "'."
                ));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Registro con ID #" + id + " no encontrado."));
            }
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }
}

