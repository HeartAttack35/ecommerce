FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

# Copiar archivos de configuración Gradle
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# Dar permisos de ejecución al wrapper
RUN chmod +x gradlew

# Descargar dependencias (capa cacheada)
RUN ./gradlew dependencies --no-daemon || true

# Copiar código fuente y construir el JAR
COPY src src
RUN ./gradlew bootJar -x test --no-daemon

# ─── Imagen final ligera ──────────────────────────────────────────────────────
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8089

ENTRYPOINT ["java", "-jar", "app.jar"]
