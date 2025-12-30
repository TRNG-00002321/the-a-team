package com.revature.integration_tests.expense;

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

public class TestGetAllExpenses {
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

  @DisplayName("Get All Expense, Logged In")
  @Test
  public void getAllExpensesLoggedIn() {
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
        .get("/api/expenses")
      .then()
        .spec(responseSpec)
        .statusCode(200)
        .body("success", equalTo(true))
        .body("count", equalTo(3))
        .body("data.expense.id", hasItems(901, 902, 903));
  }

  // Maybe bug? Should be Protected
  @DisplayName("Get All Expense, Not Logged In")
  @Test
  public void getAllExpensesNotLoggedIn() {
    given()
      .spec(requestSpec)
    .when()
      .get("/api/expenses")
    .then()
      .spec(responseSpec)
      .statusCode(200)
      .body("success", equalTo(true))
      .body("count", equalTo(3))
      .body("data.expense.id", hasItems(901, 902, 903));
  }
}
