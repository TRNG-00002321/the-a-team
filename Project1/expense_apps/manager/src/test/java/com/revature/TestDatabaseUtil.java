package com.revature;

import com.revature.repository.DatabaseConnection;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Statement;

public class TestDatabaseUtil {

  private static final DatabaseConnection db = new DatabaseConnection();

  public static void resetAndSeed() {
    executeSql("/sql/reset.sql");
    executeSql("/sql/seed.sql");
  }

  private static void executeSql(String resource) {
    try (
      Connection conn = db.getConnection();
      Statement stmt = conn.createStatement();
      InputStream is = TestDatabaseUtil.class.getResourceAsStream(resource)
    ) {
      if (is == null) {
        throw new RuntimeException("SQL file not found: " + resource);
      }

      String sql = new String(is.readAllBytes(), StandardCharsets.UTF_8);

      for (String statement : sql.split(";")) {
        String trimmed = statement.trim();
        if (!trimmed.isEmpty() && !trimmed.startsWith("--")) {
          stmt.execute(trimmed);
        }
      }

    } catch (Exception e) {
      throw new RuntimeException("Failed executing " + resource, e);
    }
  }
}
