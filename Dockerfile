# Use the official image for JDK 22 (once available)
FROM eclipse-temurin:21-jdk as build

# Alternatively, if using OpenJDK (once available)
# FROM openjdk:21-jdk as build

# Set the working directory
WORKDIR /app

# Copy the application JAR file to the container
COPY target/subtitles-generator-0.1.0-SNAPSHOT.jar /app/subtitles-generator-0.1.0-SNAPSHOT.jar

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "/app/subtitles-generator-0.1.0-SNAPSHOT.jar"]