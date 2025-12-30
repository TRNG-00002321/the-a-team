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

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class TestGetAllExpensesNegative {
  static RequestSpecification requestSpec;
  static ResponseSpecification responseSpec;
  private static Connection connection;

  @BeforeAll
  public static void setUp() {
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
  public static void tearDown() {
    RestAssured.reset();
  }

  @DisplayName("Get All Expense, Logged In, Empty")
  @Test
  public void getAllExpensesLoggedInEmpty() {
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
      .body("count", equalTo(0));
}
}
