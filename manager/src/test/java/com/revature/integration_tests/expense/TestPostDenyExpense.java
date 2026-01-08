package com.revature.integration_tests.expense;

import com.revature.utils.TestDatabaseUtil;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestPostDenyExpense {

    private static String managerJwtCookie;

    static RequestSpecification requestSpec;
    static ResponseSpecification responseSpec;

    @BeforeAll
    static void setUp(){
        RestAssured.baseURI="http://localhost:5001";
        requestSpec= new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .build();

        responseSpec = new ResponseSpecBuilder()
                .expectContentType(ContentType.JSON)
                .expectResponseTime(lessThan(5000L))
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

    @Test
    @DisplayName("POST /api/expense/{id}/deny expense with valid login and valid expense")
    void testDenyExpense_AsManager_Positive() {
        // perform login first
        String credentials = """
                 {
                "username" : "manager1" ,
                "password" : "password123" ,
                 "role" : "manager"
                 }
                """;
        Response response1 = given()
                .spec(requestSpec)
                .body(credentials)
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(200)
                .extract().response();
        String jwtCookie = response1.getCookie("jwt");

        // Deny pending expense
        int expenseId = 1;

        Response denyResponse =
                given()
                        .contentType(ContentType.JSON)
                        .cookie("jwt", jwtCookie)
                        //.body("{ \"reason\": \"Policy violation\" }")
                        .when()
                        .post("/api/expenses/" + expenseId + "/deny")
                        .then()
                        .statusCode(200)
                        .extract().response();

        String message = denyResponse.jsonPath().getString("message");
        assertEquals("Expense denied successfully", message);
    }

    @Test
    @DisplayName("POST /api/expense/{id}/deny expense with valid login and non existent expense")
    void testDenyExpense_AsManager_NonexistentExpense(){
        // perform login first
        String credentials = """
                 {
                "username" : "manager1" ,
                "password" : "password123" , 
                "role" : "manager"
               
                 }
                """;
        Response response1 = given()
                .spec(requestSpec)
                .body(credentials)
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(200)
                .extract().response();
        String jwtCookie = response1.getCookie("jwt");

        Response denyNonExResponse =
                given()
                        .cookie("jwt", jwtCookie)
                        .when()
                        .post("/api/expenses/999/deny")
                        .then()
                        .statusCode(404)
                        .extract().response();


    }
    @Test
    @DisplayName("Deny Expense without authentication")
    void testdenyNOAuth(){
        Response deny =
                given()
                        .when()
                        .post("/api/expenses/999/deny")
                        .then()
                        .statusCode(401)
                        .extract().response();

        }
    @Test
    @DisplayName("Invalid role authentication")
    void testEmployeeAuth(){
        String credentials = """
                 {
                "username" : "employee1" ,
                "password" : "password123" , 
                "role" : "employee"
               
                 }
                """;
        Response response1 = given()
                .spec(requestSpec)
                .body(credentials)
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(401)
                .extract().response();
    }
}




