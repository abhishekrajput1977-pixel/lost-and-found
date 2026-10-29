# syntax=docker/dockerfile:1
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /workspace

COPY pom.xml ./
RUN mvn --batch-mode dependency:go-offline

COPY src ./src
RUN mvn --batch-mode --no-transfer-progress -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /workspace/target/campus-guard-1.0.0.jar app.jar

# Mount persistent storage at /data when the hosting provider supports it.
ENV SPRING_PROFILES_ACTIVE=postgres
ENV CAMPUSGUARD_UPLOAD_DIR=/data/uploads

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
