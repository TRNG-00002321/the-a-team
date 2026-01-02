package com.revature.integration_tests.auth;

import com.revature.utils.TestDatabaseUtil;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestPostApiStatus {
    static RequestSpecification requestSpec;
    static ResponseSpecification responseSpec;

    @BeforeAll
    public static void setUp(){
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
    public static void tearDown(){
        RestAssured.reset();
    }

    @BeforeEach
    void resetDatabase() {
        TestDatabaseUtil.resetAndSeed();
    }

    @DisplayName("Get auth status, no manager has logged in yet")
    @Test
    public void getAuthStatusDefault(){
        given()
                .spec(requestSpec)
        .when()
                .get("/api/auth/status")
        .then()
                .spec(responseSpec)
                .statusCode(200)
                .body("authenticated", equalTo(false));
    }

    @DisplayName("Get auth status, manager is logged in")
    @Test
    public void getAuthStatusLoggedIn(){
        String credentials = """
            {
                "username":"manager1",
                "password":"password123"
            }
            """;
        //log in with valid credentials
        Response response = given()
                .spec(requestSpec)
                .body(credentials)
        .when()
                .post("/api/auth/login")
        .then()
                .spec(responseSpec)
                .statusCode(200)
                .extract().response();
        String message = response.jsonPath().getString("message");
        assertEquals("Login successful", message);
        String jwtCookie = response.getCookie("jwt");

        //check auth status
        given()
                .spec(requestSpec)
                .cookie("jwt", jwtCookie)
        .when()
                .get("/api/auth/status")
        .then()
                .spec(responseSpec)
                .statusCode(200)
                .body("authenticated", equalTo(true));
    }

}
