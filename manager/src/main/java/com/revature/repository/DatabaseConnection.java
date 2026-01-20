package com.revature.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import io.github.cdimascio.dotenv.Dotenv;

/**
 * Database connection utility for SQLite database. Handles connection
 * management for the shared expense manager database.
 */
public class DatabaseConnection {

    private final String databasePath;

    public DatabaseConnection() {
        // Try environment variables first (for Docker), then .env file (for local dev)
        String testMode = System.getenv("TEST_MODE");
        String dbPath = System.getenv("DATABASE_PATH");
        String testDbPath = System.getenv("TEST_DATABASE_PATH");

        // Fallback to .env file if environment variables not set
        if (testMode == null || dbPath == null) {
            try {
                Dotenv dotenv = Dotenv.configure()
                        .ignoreIfMissing()  // Don't throw exception if .env doesn't exist
                        .load();

                testMode = testMode != null ? testMode : dotenv.get("TEST_MODE", "false");
                dbPath = dbPath != null ? dbPath : dotenv.get("DATABASE_PATH");
                testDbPath = testDbPath != null ? testDbPath : dotenv.get("TEST_DATABASE_PATH");
            } catch (Exception e) {
                // If .env doesn't exist and no env vars, use defaults or fail gracefully
                System.err.println("Warning: Could not load .env file: " + e.getMessage());
                if (dbPath == null) {
                    throw new RuntimeException("Database path not configured. Set DATABASE_PATH environment variable.");
                }
            }
        }

        boolean isTestMode = "true".equalsIgnoreCase(testMode);
        String path = isTestMode ? testDbPath : dbPath;

        if (path == null) {
            throw new RuntimeException("Database path not configured. Set DATABASE_PATH or TEST_DATABASE_PATH environment variable.");
        }

        this.databasePath = "jdbc:sqlite:" + path;
        System.out.println("Database configured at: " + this.databasePath);
    }

    public DatabaseConnection(String databasePath) {
        this.databasePath = databasePath;
    }

    /**
     * Get a database connection.
     *
     * @return SQLite database connection
     * @throws SQLException if connection fails
     */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(databasePath);
    }

    public String getDatabasePath() {
        return databasePath;
    }
}