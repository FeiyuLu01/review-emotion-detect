# Multi-stage build - Build stage
FROM maven:3.8.4-openjdk-8 AS build

# Set working directory
WORKDIR /app

# Copy pom.xml
COPY pom.xml .

# Download dependencies
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build application
RUN mvn clean package -DskipTests

# Runtime stage
FROM openjdk:8-jre-alpine

# Install wget for health check
RUN apk add --no-cache wget

# Create non-root user
RUN addgroup -g 1001 -S appgroup && \
    adduser -u 1001 -S appuser -G appgroup

# Set working directory
WORKDIR /app

# Copy JAR file from build stage
COPY --from=build /app/target/review-emotion-detect-backend-1.0-SNAPSHOT.jar app.jar

# Change file ownership
RUN chown appuser:appgroup app.jar

# Switch to non-root user
USER appuser

# Expose port
EXPOSE 8080

# Health check with longer timeout and more retries
HEALTHCHECK --interval=60s --timeout=10s --start-period=180s --retries=5 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/test || exit 1

# Start application with optimized JVM settings and production profile
ENTRYPOINT ["java", "-Xms512m", "-Xmx1024m", "-XX:+UseG1GC", "-Dspring.profiles.active=prod", "-jar", "app.jar"]
