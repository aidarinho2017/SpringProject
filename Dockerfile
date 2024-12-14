#FROM ubuntu:latest
# Start with a base image containing Java runtime
FROM openjdk:17-jdk-slim
LABEL authors="antay"

COPY target/*.jar SpringJwtAuthExample.jar
# Expose the application's port
EXPOSE 8081

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]