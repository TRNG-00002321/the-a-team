package com.revature.integration_tests.auth;

import com.revature.utils.TestDatabaseUtil;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;

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

    @BeforeEach
    void resetDatabase() {
        TestDatabaseUtil.resetAndSeed();
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
    @ParameterizedTest(name = "username: {0} and password: {1}")

    @CsvSource({"invalid,invalid",
                 "manager1,password",
                "username,password123",
                "'',password123",
                "manager1,''"})
    @DisplayName("Test API: Manager Login Invalid Login")
    void testAuthLogin_Negative(String username, String password){
        Map<String, String> jsonBody = new HashMap<>();
        jsonBody.put("username", username);
        jsonBody.put("password", password);
        given()
                .contentType(ContentType.JSON)
                .body(jsonBody)
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(401);
    }
}
