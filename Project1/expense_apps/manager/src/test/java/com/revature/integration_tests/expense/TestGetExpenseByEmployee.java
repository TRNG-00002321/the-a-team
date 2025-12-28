package com.revature.integration_tests.expense;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class TestGetExpenseByEmployee {
    @BeforeAll
    static void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 5001;
    }

    private String loginAndGetJwtCookie() {
        return given()
                .contentType("application/json")
                .accept("application/json")
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
    }


    @Test
    void getExpensesByEmployee_success() {
        int employeeId = 1;
        String jwt = loginAndGetJwtCookie();

        given()
                .cookie("jwt", jwt)
                .accept("application/json")
                .pathParam("employeeId", employeeId)
                .when()
                .get("/api/expenses/employee/{employeeId}")
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("employeeId", equalTo(employeeId))
                .body("data", notNullValue());
    }

    @Test
    void getExpensesByEmployee_failure() {
        int invalidEmployeeId = 1000;
        String jwt = loginAndGetJwtCookie();

        given()
                .cookie("jwt", jwt)
                .accept("application/json")
                .pathParam("employeeId", invalidEmployeeId)
                .when()
                .get("/api/expenses/employee/{employeeId}")
                .then()
                .statusCode(200)
                .body("count", equalTo(0));
    }
}
