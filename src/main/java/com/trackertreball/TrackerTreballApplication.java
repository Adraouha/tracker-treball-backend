package com.trackertreball;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal y punto de entrada de la aplicacion Spring Boot Tracker Treball.
 * <p>
 * Tracker Treball es una plataforma corporativa integral orientada a la gestion inteligente del
 * control horario laboral, seguimiento de sprints y proyectos tecnicos, y administracion preventiva
 * de Riesgos Laborales (PRL) para organizaciones empresariales segun la legislacion vigente (RDL 8/2019).
 * </p>
 * <p>
 * La anotacion {@link SpringBootApplication} encapsula:
 * <ul>
 *   <li>{@code @Configuration}: Permite el registro de beans personalizados y configuraciones de Spring.</li>
 *   <li>{@code @EnableAutoConfiguration}: Inicializa los componentes automaticos de Spring Boot como JPA y MVC.</li>
 *   <li>{@code @ComponentScan}: Realiza el escaneo recursivo de componentes, servicios, repositorios y controladores
 *       dentro del paquete base {@code com.trackertreball}.</li>
 * </ul>
 * </p>
 *
 * @author Hassan Adraou
 * @version 1.0.0
 * @since 2026-11-02
 */
@SpringBootApplication
public class TrackerTreballApplication {

    /**
     * Metodo principal que arranca el contexto de la aplicacion Spring Boot.
     * <p>
     * Inicializa el contenedor IoC (Inversion of Control), levanta el servidor web embebido (Tomcat/Netty)
     * en el puerto configurado (8080) y establece las conexiones al pool de datos MySQL.
     * </p>
     *
     * @param args Argumentos de linea de comandos pasados a la aplicacion en tiempo de arranque.
     */
    public static void main(String[] args) {
        SpringApplication.run(TrackerTreballApplication.class, args);
    }
}
