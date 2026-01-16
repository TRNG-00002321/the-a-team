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
        //Dotenv dotenv = Dotenv.load();

        boolean testMode = System.getenv("TEST_MODE").equalsIgnoreCase("true");

        String path = testMode ? System.getenv("TEST_DATABASE_PATH") : System.getenv("DATABASE_PATH");

        if (path == null) {
            throw new RuntimeException("Database path not configured");
        }

        this.databasePath = "jdbc:sqlite:" + path;
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