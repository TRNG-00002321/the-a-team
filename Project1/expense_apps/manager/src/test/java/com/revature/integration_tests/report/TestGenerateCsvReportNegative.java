package com.revature.integration_tests.report;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.jupiter.api.Assertions.*;

public class TestGenerateCsvReportNegative {
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
  }

  @AfterAll
  public static void tearDown() throws SQLException {
    RestAssured.reset();
  }

  @DisplayName("Get Expense Report, Logged In, Empty")
  @Test
  public void getExpenseReportCsvLoggedInEmpty() {
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
    assertEquals(1, lines.length);
  }

  @DisplayName("Get Pending Expense Report, Logged In, Empty")
  @Test
  public void getPendingExpenseReportCsvLoggedInEmpty() {
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
    assertEquals(1, lines.length);
  }

  @DisplayName("Get Employee Expense Report, Logged In, Empty")
  @Test
  public void getEmployeeExpenseReportCsvLoggedInEmpty() {
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
    assertEquals(1, lines.length);
  }

  @DisplayName("Get Date Range Expense Report, Logged In, Empty")
  @Test
  public void getDateRangeExpenseReportCsvLoggedInEmpty() {
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
    assertEquals(1, lines.length);
  }

  @DisplayName("Get Category Expense Report, Logged In, Empty")
  @Test
  public void getCategoryExpenseReportCsvLoggedInEmpty() {
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
    assertEquals(1, lines.length);
  }

  @DisplayName("Get Expense Report, Not Logged in")
  @Test
  public void getExpenseReportCsvNotLoggedIn() {
    given()
      .spec(requestSpec)
    .when()
      .get("/api/reports/expenses/csv")
    .then()
      .statusCode(401);
  }

  @DisplayName("Get Date Range Expense Report, Missing Date Params")
  @Test
  public void getDateRangeReportMissingDate() {
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

    given()
      .spec(requestSpec)
      .cookie("jwt", jwtCookie)
      .queryParam("endDate", "2025-01-10")
    .when()
      .get("/api/reports/expenses/daterange/csv")
    .then()
      .statusCode(400);
  }

  @DisplayName("Get Date Range Expense Report, Invalid Date Params")
  @Test
  public void getDateRangeReportInvalidDate() {
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

    given()
      .spec(requestSpec)
      .cookie("jwt", jwtCookie)
      .queryParam("startDate", "01-01-2025")
      .queryParam("endDate", "01-10-2025")
    .when()
      .get("/api/reports/expenses/daterange/csv")
    .then()
      .statusCode(400);
  }

  // Bug? Should be 400 error
  @DisplayName("Get Employee Expense Report, Invalid EmployeeID")
  @Test
  public void getEmployeeReportInvalidId() {
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

    String employeeId = "abc";

    given()
      .spec(requestSpec)
      .cookie("jwt", jwtCookie)
    .when()
      .get("/api/reports/expenses/employee/" + employeeId + "/csv")
    .then()
      .statusCode(500);
  }

  @DisplayName("Get Category Expense Report, Missing Category")
  @Test
  public void getCategoryReportMissingCategory() {
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

    given()
      .spec(requestSpec)
      .cookie("jwt", jwtCookie)
    .when()
      .get("/api/reports/expenses/category/ /csv")
    .then()
      .statusCode(400);
  }
}
