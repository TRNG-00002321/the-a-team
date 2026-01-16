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

# Production stage
FROM --platform=linux/amd64 eclipse-temurin:17-jre-alpine

WORKDIR /app

# Create non-root user for security
RUN addgroup -g 1001 appgroup &&  adduser -u 1001 -G appgroup -D appuser

# Copy the built JAR from builder stage
COPY --from=builder /app/target/app.jar app.jar
# Create directory for database (shared volume mount point)
RUN mkdir -p /data && chown appuser:appgroup /data

# Switch to non-root user
USER appuser

# Environment variables
ENV JAVA_OPTS="-Xms256m -Xmx512m"
ENV DATABASE_PATH=/data/expense_manager.db
ENV TEST_MODE=true

ENV DATABASE_PATH=../employee/expense_manager.db
ENV TEST_DATABASE_PATH=src/test/resources/db/test_expense_manager.db

# Expose Javalin port
EXPOSE 5001

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=30s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:5001/health || exit 1

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
