package com.revature.integration_tests.auth;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

public class TestAuthLogin {
    @BeforeAll
    static void setup(){
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 5001;
    }

    @AfterAll
    static void tearDown(){
        RestAssured.reset();
    }

    //MI-221
    @Test
    @DisplayName("Test API: Manager Login Positive")
    void testAuthLogin_Positive(){
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
                .statusCode(200);
    }

    //MI-222
    @Test
    @DisplayName("Test API: Manager Login Invalid Login")
    void testAuthLogin_Negative(){
        given()
                .contentType(ContentType.JSON)
                .body("""
                      {
                        "username": "invalid",
                        "password": "invalid"
                      }
                      """)
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(401);
    }
}
