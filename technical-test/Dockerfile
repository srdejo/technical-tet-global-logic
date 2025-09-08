# Etapa 1: Construcción
FROM gradle:7.5.1-jdk11 AS build
WORKDIR /app

# Copiar solo archivos necesarios para cachear dependencias
COPY build.gradle settings.gradle ./
COPY gradle ./gradle
RUN gradle build -x test --no-daemon || return 0

# Copiar el resto del proyecto
COPY . .

# Construir la aplicación
RUN gradle clean build -x test --no-daemon

# Etapa 2: Imagen de ejecución
FROM openjdk:11-jre-slim
WORKDIR /app

# Copiar el jar generado
COPY --from=build /app/build/libs/*.jar app.jar

# Exponer el puerto
EXPOSE 8080

# Comando de arranque
ENTRYPOINT ["java", "-jar", "app.jar"]
