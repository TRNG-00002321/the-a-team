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
import static org.hamcrest.Matchers.lessThan;

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

  @BeforeEach
  void resetDatabase() {
    TestDatabaseUtil.resetAndSeed();
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
