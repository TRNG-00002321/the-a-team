package com.revature.integration_tests.report;

import com.revature.utils.TestDatabaseUtil;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.junit.jupiter.api.*;

import java.sql.SQLException;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

public class TestGenerateCsvReport {
  static RequestSpecification requestSpec;
  static ResponseSpecification responseSpec;

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
  }

  @AfterAll
  public static void tearDown() throws SQLException {
    RestAssured.reset();
  }

  @BeforeEach
  void resetDatabase() {
    TestDatabaseUtil.resetAndSeed();
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
    assertEquals(6, lines.length);

    assertTrue(csv.contains("1,employee1,50.0"));
    assertTrue(csv.contains("2,employee1,200.0"));
    assertTrue(csv.contains("3,employee1,30.0"));
    assertTrue(csv.contains("4,employee2,75.0"));
    assertTrue(csv.contains("5,employee2,450.0"));
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
    assertEquals(3, lines.length);

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

    int employeeId = 1;

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
    assertEquals(4, lines.length);

    assertTrue(csv.contains(",employee" + employeeId + ","));;
    assertFalse(csv.contains(",employee2,"));
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
    assertEquals(4, lines.length);

    assertTrue(csv.contains(",2025-01-05,"));;
    assertTrue(csv.contains(",2025-01-06,"));;
    assertFalse(csv.contains(",2025-01-07,"));;
    assertFalse(csv.contains(",2025-01-09,"));
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
    assertTrue(csv.contains("1,"));;
    assertFalse(csv.contains(",2,"));
  }
}
