# Lightweight production image
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Install curl for healthcheck
RUN apk add --no-cache curl

# Create non-root user
RUN addgroup -g 1001 appgroup && \
    adduser -u 1001 -G appgroup -D appuser

# Copy pre-built JAR from Jenkins workspace
COPY target/*.jar app.jar

# Copy test DB (used when running in test mode)
COPY src/test/resources/db/test_expense_manager.db /app/db/test_expense_manager.db

# Create volume mount directory
RUN mkdir -p /data && chown -R appuser:appgroup /app /data

USER appuser

ENV JAVA_OPTS="-Xms256m -Xmx512m"
ENV DATABASE_PATH=/app/db/test_expense_manager.db

EXPOSE 5001

HEALTHCHECK --interval=30s --timeout=10s --start-period=30s \
  CMD curl -f http://localhost:5001/health || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]