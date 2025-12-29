package com.revature.integration_tests.expense;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

public class TestGetExpensesPending {

    static RequestSpecification requestSpec;
    static ResponseSpecification responseSpec;

    @BeforeAll
    static void setup(){
        RestAssured.baseURI = "http://localhost:5001/manager.html";

        requestSpec = new RequestSpecBuilder().build();

        responseSpec = new ResponseSpecBuilder()
                .expectContentType(ContentType.TEXT)
                .build();
    }

    @AfterAll
    static void tearDown(){
        RestAssured.reset();
    }

    @Test
    @DisplayName("Test Get Pending Expenses Positive")
    void testGetPendingExpensesPositive(){
        given()
                .spec(requestSpec)
        .when()
                .get("/api/expenses/pending")
        .then()
                .spec(responseSpec)
                .statusCode(200);
    }

}
