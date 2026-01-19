# Java Manager App Dockerfile
# Multi-stage build for optimized production image

# Build stage
FROM --platform=linux/amd64 maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /app

# Copy pom.xml and download dependencies (cached layer)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build
COPY src/ ./src/
RUN mvn clean package -DskipTests -B

# Verify static files are in JAR (debugging step - remove later)
RUN echo "=== Checking JAR contents ===" && \
    jar tf target/manager-1.0-SNAPSHOT.jar | grep -E '\.(html|js|css)$' || echo "WARNING: No static files found in JAR!"

# Production stage
FROM --platform=linux/amd64 eclipse-temurin:17-jre-alpine

WORKDIR /app

# Install wget for healthcheck
RUN apk add --no-cache wget

# Create non-root user for security
RUN addgroup -g 1001 appgroup && \
    adduser -u 1001 -G appgroup -D appuser

# Copy the built JAR from builder stage
COPY --from=builder /app/target/manager-1.0-SNAPSHOT.jar app.jar

# Copy test database for standalone testing
COPY --from=builder /app/src/test/resources/db/test_expense_manager.db /app/db/expense_manager.db

# Create directory for shared database (production volume mount)
RUN mkdir -p /data && chown -R appuser:appgroup /app /data

# Switch to non-root user
USER appuser

# Environment variables
ENV JAVA_OPTS="-Xms256m -Xmx512m"
ENV TEST_MODE=false
ENV DATABASE_PATH=/app/db/expense_manager.db

# Expose Javalin port
EXPOSE 5001

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=30s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:5001/health || exit 1

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
