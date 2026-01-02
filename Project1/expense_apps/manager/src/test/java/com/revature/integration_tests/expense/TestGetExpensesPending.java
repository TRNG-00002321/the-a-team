package com.revature.integration_tests.expense;

import com.revature.TestDatabaseUtil;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class TestGetExpensesPending {

    static RequestSpecification requestSpec;
    static ResponseSpecification responseSpec;
    static String jwtCookie;

    @BeforeAll
    static void setup(){
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 5001;

        //Get JWT cookie authorization
        jwtCookie =
                given()
                        .contentType(ContentType.JSON)
                        .body("""
                      {
                        "username": "manager1",
                        "password": "password123"
                      }
                      """)
                        .when()
                        .post("/api/auth/login")
                        .then()
                        .statusCode(200)
                        .extract()
                        .cookie("jwt");

        requestSpec = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .addCookie("jwt", jwtCookie)
                .build();

        responseSpec = new ResponseSpecBuilder()
                .expectContentType(ContentType.JSON)
                .build();
    }

    @AfterAll
    static void tearDown(){
        RestAssured.reset();
    }

    @BeforeEach
    void resetDatabase() {
        TestDatabaseUtil.resetAndSeed();
    }

    //MI-219
    @Test
    @DisplayName("Test API: Get Pending Expenses Positive")
    void testGetPendingExpensesPositive(){
        given()
                .spec(requestSpec)
        .when()
                .get("/api/expenses/pending")
        .then()
                .spec(responseSpec)
                .contentType("application/json")
                .body("data", notNullValue())
                .statusCode(200);
    }

    //MI-220
    @Test
    @DisplayName("Test API: Get Pending Expenses Unauthorized Request")
    void getPendingExpenses_withoutJwt_shouldReturn401() {
        given()
                .baseUri("http://localhost")
                .port(5001)
        .when()
                .get("/api/expenses/pending")
        .then()
                .statusCode(anyOf(is(401), is(403)))
                .contentType("text/plain");
    }
}
