# ==============================================================================
# ETAPA 1: Construccion (Build) con Maven y OpenJDK 17
# ==============================================================================
FROM maven:3.9.6-eclipse-temurin-17-alpine AS build
WORKDIR /app

# Descarga previa de dependencias para acelerar construcciones aprovechando cache
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copia de codigo fuente y compilacion empaquetando el artefacto final sin ejecutar tests
COPY src ./src
RUN mvn clean package -DskipTests

# ==============================================================================
# ETAPA 2: Entorno de ejecucion ligero (Alpine JRE 17)
# ==============================================================================
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Creacion de usuario y grupo de sistema sin privilegios root
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

# Copia del ejecutable generado desde la etapa de compilacion
COPY --from=build /app/target/*.jar app.jar

# Railway expone y asigna dinamicamente la variable de entorno PORT
EXPOSE 8080

# Limite de memoria estricto (-Xmx380m) optimizado para Railway (512 MB)
ENV JAVA_OPTS="-Xms256m -Xmx380m -XX:+UseSerialGC -Djava.security.egd=file:/dev/./urandom"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

