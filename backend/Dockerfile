# Etapa 1: Build con Maven y OpenJDK 17
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: Imagen liviana de ejecución
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/CitasBack-1.0.0-SPRINT1.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
