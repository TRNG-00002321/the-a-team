package com.revature.integration_tests.auth;

import com.revature.TestDatabaseUtil;
import io.restassured.RestAssured;
import io.restassured.http.Cookie;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class TestLogout {
    @BeforeAll
    static void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 5001;
    }

    @AfterAll
    public static void tearDown(){
        RestAssured.reset();
    }

    @BeforeEach
    void resetDatabase() {
        TestDatabaseUtil.resetAndSeed();
    }

    // MI-212
    @Test
    void testLogout() {
        Response loginResponse =
                given()
                        .contentType("application/json")
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
                        .response();

        Cookie sessionCookie = loginResponse.getDetailedCookie("jwt");

        assert sessionCookie != null;

        given()
                .cookie(sessionCookie)
                .when()
                .post("/api/auth/logout")
                .then()
                .statusCode(200)
                .body("message", equalTo("Logged out successfully"));

        given()
                .cookie(sessionCookie)
                .when()
                .post("/api/auth/logout")
                .then()
                .statusCode(200)
                .cookie("jwt", equalTo(""));
    }
}
