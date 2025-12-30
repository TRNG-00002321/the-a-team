package com.revature.integration_tests.report;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

public class TestGenerateCsvReport {
  static RequestSpecification requestSpec;
  static ResponseSpecification responseSpec;
  private static Connection connection;

  @BeforeAll
  public static void setUp() throws SQLException {
    RestAssured.baseURI="http://localhost:5001/";

    requestSpec= new RequestSpecBuilder()
      .setContentType(ContentType.JSON)
      .setAccept(ContentType.JSON)
      .build();
    responseSpec= new ResponseSpecBuilder()
      .expectContentType(ContentType.JSON)
      .expectResponseTime(lessThan(5000L))
      .build();

    String url = "jdbc:sqlite:../employee/expense_manager.db";
    connection = DriverManager.getConnection(url);
  }

  @AfterAll
  public static void tearDown() throws SQLException {
    if (connection != null && !connection.isClosed()) {
      connection.close();
    }
    RestAssured.reset();
  }

  @BeforeEach
  public void setUpDatabase() throws SQLException {
    connection.prepareStatement("""
        INSERT INTO users (id, username, password, role) VALUES
        (997, 'employee997', 'password123', 'Employee'),
        (998, 'employee998', 'password123', 'Employee'),
        (999, 'manager999',  'password123', 'Manager')
      """)
      .executeUpdate();
    connection.prepareStatement("""
        INSERT INTO expenses (id, user_id, amount, description, date) VALUES
        (901, 997, 25.50, 'Lunch with client', '2025-01-05'),
        (902, 997, 120.00,'Hotel stay',        '2025-01-06'),
        (903, 998, 15.75, 'Taxi to office',    '2025-01-07')
      """)
      .executeUpdate();
    connection.prepareStatement("""
        INSERT INTO approvals (id, expense_id, status, reviewer, comment, review_date) VALUES
        (801, 901, 'approved', 999, 'Approved, valid expense', '2025-01-08'),
        (802, 902, 'pending',  NULL, NULL,                   NULL),
        (803, 903, 'denied',   999, 'Receipt missing',         '2025-01-09')
      """)
      .executeUpdate();
  }

  @AfterEach
  void cleanupDatabase() throws SQLException {
    connection.prepareStatement("""
        DELETE FROM approvals
        WHERE id IN (801, 802, 803)
      """)
      .executeUpdate();

    connection.prepareStatement("""
        DELETE FROM expenses
        WHERE id IN (901, 902, 903)
      """)
      .executeUpdate();

    connection.prepareStatement("""
        DELETE FROM users
        WHERE id IN (997, 998, 999)
      """)
      .executeUpdate();
  }

  @DisplayName("Get Expense Report, Logged In")
  @Test
  public void getExpenseReportCsvLoggedIn() {
    String credentials = """
      {
          "username":"manager1",
          "password":"password123"
      }
      """;
    Response authResponse =
      given()
        .spec(requestSpec)
        .body(credentials)
      .when()
        .post("/api/auth/login")
      .then()
        .spec(responseSpec)
        .statusCode(200)
        .extract().response();
    String jwtCookie = authResponse.getCookie("jwt");

    String csv =
      given()
        .spec(requestSpec)
        .cookie("jwt", jwtCookie)
      .when()
        .get("/api/reports/expenses/csv")
      .then()
        .statusCode(200)
        .contentType("text/csv")
        .extract()
        .asString();

    assertTrue(csv.startsWith(
      "Expense ID,Employee,Amount,Description,Date,Status,Reviewer,Comment,Review Date"
    ));

    String[] lines = csv.split("\\R");
    assertEquals(4, lines.length);

    assertTrue(csv.contains("901,employee997,25.5"));
    assertTrue(csv.contains("902,employee997,120.0"));
    assertTrue(csv.contains("903,employee998,15.75"));
  }

  @DisplayName("Get Pending Expense Report, Logged In")
  @Test
  public void getPendingExpenseReportCsvLoggedIn() {
    String credentials = """
      {
          "username":"manager1",
          "password":"password123"
      }
      """;
    Response authResponse =
      given()
        .spec(requestSpec)
        .body(credentials)
      .when()
        .post("/api/auth/login")
      .then()
        .spec(responseSpec)
        .statusCode(200)
        .extract().response();
    String jwtCookie = authResponse.getCookie("jwt");

    String csv =
      given()
        .spec(requestSpec)
        .cookie("jwt", jwtCookie)
      .when()
        .get("/api/reports/expenses/pending/csv")
      .then()
        .statusCode(200)
        .contentType("text/csv")
        .extract()
        .asString();

    assertTrue(csv.startsWith(
      "Expense ID,Employee,Amount,Description,Date,Status,Reviewer,Comment,Review Date"
    ));

    String[] lines = csv.split("\\R");
    assertEquals(2, lines.length);

    assertTrue(csv.contains(",pending,"));
    assertFalse(csv.contains(",approved,"));
    assertFalse(csv.contains(",denied,"));
  }

  @DisplayName("Get Employee Expense Report, Logged In")
  @Test
  public void getEmployeeExpenseReportCsvLoggedIn() {
    String credentials = """
      {
          "username":"manager1",
          "password":"password123"
      }
      """;
    Response authResponse =
      given()
        .spec(requestSpec)
        .body(credentials)
      .when()
        .post("/api/auth/login")
      .then()
        .spec(responseSpec)
        .statusCode(200)
        .extract().response();
    String jwtCookie = authResponse.getCookie("jwt");

    int employeeId = 997;

    String csv =
      given()
        .spec(requestSpec)
        .cookie("jwt", jwtCookie)
      .when()
        .get("/api/reports/expenses/employee/" + employeeId + "/csv")
      .then()
        .statusCode(200)
        .contentType("text/csv")
        .extract()
        .asString();

    assertTrue(csv.startsWith(
      "Expense ID,Employee,Amount,Description,Date,Status,Reviewer,Comment,Review Date"
    ));

    String[] lines = csv.split("\\R");
    assertEquals(3, lines.length);

    assertTrue(csv.contains(",employee" + employeeId + ","));;
    assertFalse(csv.contains(",employee998,"));
  }

  @DisplayName("Get Date Range Expense Report, Logged In")
  @Test
  public void getDateRangeExpenseReportCsvLoggedIn() {
    String credentials = """
      {
          "username":"manager1",
          "password":"password123"
      }
      """;
    Response authResponse =
      given()
        .spec(requestSpec)
        .body(credentials)
      .when()
        .post("/api/auth/login")
      .then()
        .spec(responseSpec)
        .statusCode(200)
        .extract().response();
    String jwtCookie = authResponse.getCookie("jwt");

    String csv =
      given()
        .spec(requestSpec)
        .cookie("jwt", jwtCookie)
        .queryParam("startDate", "2025-01-05")
        .queryParam("endDate", "2025-01-06")
      .when()
        .get("/api/reports/expenses/daterange/csv")
      .then()
        .statusCode(200)
        .contentType("text/csv")
        .extract()
        .asString();

    assertTrue(csv.startsWith(
      "Expense ID,Employee,Amount,Description,Date,Status,Reviewer,Comment,Review Date"
    ));

    String[] lines = csv.split("\\R");
    assertEquals(3, lines.length);

    assertTrue(csv.contains(",2025-01-05,"));
    assertTrue(csv.contains(",2025-01-06,"));;
    assertFalse(csv.contains(",2025-01-07,"));
  }

  @DisplayName("Get Category Expense Report, Logged In")
  @Test
  public void getCategoryExpenseReportCsvLoggedIn() {
    String credentials = """
      {
          "username":"manager1",
          "password":"password123"
      }
      """;
    Response authResponse =
      given()
        .spec(requestSpec)
        .body(credentials)
      .when()
        .post("/api/auth/login")
      .then()
        .spec(responseSpec)
        .statusCode(200)
        .extract().response();
    String jwtCookie = authResponse.getCookie("jwt");

    String category = "Hotel";

    String csv =
      given()
        .spec(requestSpec)
        .cookie("jwt", jwtCookie)
      .when()
        .get("/api/reports/expenses/category/" + category + "/csv")
      .then()
        .statusCode(200)
        .contentType("text/csv")
        .extract()
        .asString();

    assertTrue(csv.startsWith(
      "Expense ID,Employee,Amount,Description,Date,Status,Reviewer,Comment,Review Date"
    ));

    String[] lines = csv.split("\\R");
    assertEquals(2, lines.length);

    assertTrue(csv.contains(",Hotel stay,"));
    assertTrue(csv.contains("902,"));;
    assertFalse(csv.contains(",901,"));
    assertFalse(csv.contains(",903,"));
  }
}
