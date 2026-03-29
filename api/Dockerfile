# Stage 1: Build the Spring Boot fat jar
FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /workspace

# Copy Gradle wrapper and root build files
COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .
COPY gradle.properties .

# Stub the ui module so Gradle resolves settings.gradle.kts without errors
COPY ui/build.gradle.kts ui/

# Copy API module
COPY api/build.gradle.kts api/
COPY api/src api/src

# Build (skip UI module — it has no Gradle build)
RUN chmod +x gradlew && ./gradlew :api:bootJar --no-daemon -x test

# Stage 2: Minimal JRE runtime image
FROM eclipse-temurin:17-jre-jammy
RUN groupadd --system app && useradd --system --gid app app
WORKDIR /app

COPY --from=build --chown=app:app /workspace/api/build/libs/*.jar app.jar

USER app
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
