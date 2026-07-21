# Stage 1: Build stage
FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder

WORKDIR /build

# Copy Maven descriptor to cache dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build package
COPY src ./src
RUN mvn clean package -DskipTests -B

# Stage 2: Runtime stage
FROM eclipse-temurin:21-jre-alpine

# Set system-wide environment variables
ENV PORT=8081 \
    SPRING_PROFILES_ACTIVE=prod \
    JAVA_OPTS="-XX:+UseG1GC -XX:MaxRAMPercentage=75.0"

# Create a non-root user and group
RUN addgroup -S spring && adduser -S spring -G spring

WORKDIR /app

# Copy the jar from builder stage
COPY --from=builder /build/target/*.jar app.jar

# Ensure the application files are owned by the spring user
RUN chown -R spring:spring /app

# Switch to non-root user
USER spring:spring

# Expose target port
EXPOSE 8081

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
