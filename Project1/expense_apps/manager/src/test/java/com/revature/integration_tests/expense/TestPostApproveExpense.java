package com.revature.integration_tests.expense;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.junit.jupiter.api.*;
import org.junit.platform.suite.api.SuiteDisplayName;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class TestPostApproveExpense {

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
        //seed the database
        String url = "jdbc:sqlite:../employee/expense_manager.db";
        //String url = "jdbc:sqlite:C:/Users/alex1/Revature_work/Project_1/debug-thugs-repo/Project1/expense_apps/employee/expense_manager.db";
        connection = DriverManager.getConnection(url);
        System.out.println("Connection to SQLite test database established.");
        //add an expense to approve
        try (Statement statement = connection.createStatement()) {
            String addExpense = """
                    INSERT INTO expenses (id, user_id, amount, description, date) VALUES (99, 1, 200, 'example expense', '2025-12-29')
                    """;
            statement.execute(addExpense);
            String addApproval = """
                    INSERT INTO approvals (id, expense_id, status) VALUES (99, 99, 'pending')
                    """;
            statement.execute(addApproval);
        }
    }

    @AfterAll
    public static void tearDown() throws SQLException {
        RestAssured.reset();
        //delete the expense and approval
        try (Statement statement = connection.createStatement()) {
            statement.execute("DELETE FROM approvals WHERE id=99");
            statement.execute("DELETE FROM expenses WHERE id=99");
        }
        if (connection != null) {
            connection.close();
            System.out.println("Connection to database closed.");
        }
    }

    @DisplayName("Test attempted approval without authentication first")
    @Test
    public void testApproveNoAuth(){
        Response response = given()
                .spec(requestSpec)
        .when()
                .post("/api/expenses/1234567/approve")
        .then()
                .statusCode(401)
                .extract().response();
        assertEquals("Authentication required", response.jsonPath().getString("title"));

        //String responseMessage = response.asString();
        //System.out.println(responseMessage);
    }

    @DisplayName("Test while logged in, attempt approval of expense that does not exist")
    @Test
    public void testApproveNoExpense(){
        String credentials = """
            {
                "username":"manager1",
                "password":"password123"
            }
            """;
        //log in with valid credentials
        Response response1 = given()
                .spec(requestSpec)
                .body(credentials)
                .when()
                .post("/api/auth/login")
                .then()
                .spec(responseSpec)
                .statusCode(200)
                .extract().response();
        String jwtCookie = response1.getCookie("jwt");

        //attempt to approve an expense
        Response response2 = given()
                .spec(requestSpec)
                .cookie("jwt", jwtCookie)
        .when()
                .post("/api/expenses/1234567/approve")
        .then()
                .statusCode(404)
                .extract().response();
        assertEquals("Expense not found or could not be approved", response2.jsonPath().getString("title"));

        //String responseMessage = response2.asString();
        //System.out.println(responseMessage);
    }

    //Expense must be seeded in the database with the corresponding id for test to pass
    @DisplayName("Test approve expense positive test case, expense id exists and expense is pending")
    @Test
    public void testApprovalPositive(){
        int expenseId = 99;
        String credentials = """
            {
                "username":"manager1",
                "password":"password123"
            }
            """;
        //log in with valid credentials
        Response response1 = given()
                .spec(requestSpec)
                .body(credentials)
                .when()
                .post("/api/auth/login")
                .then()
                .spec(responseSpec)
                .statusCode(200)
                .extract().response();
        String jwtCookie = response1.getCookie("jwt");

        //approve an expense that is pending (already seeded in the database)
        Response response2 = given()
                .spec(requestSpec)
                .cookie("jwt", jwtCookie)
        .when()
                .post("/api/expenses/"+ expenseId +"/approve")
        .then()
                .statusCode(200)
                .extract().response();

        String message = response2.jsonPath().getString("message");
        assertEquals("Expense approved successfully", message);
    }

    //database is already seeded with
    @DisplayName("Test attempting to approve an already approved expense while logged in")
    @Test
    @Disabled("Same exact functionality as approving a pending expense, same expected output")
    public void testApprovalAlreadyApproved(){
        int expenseId = 99;
        String credentials = """
            {
                "username":"manager1",
                "password":"password123"
            }
            """;
        //log in with valid credentials
        Response response1 = given()
                .spec(requestSpec)
                .body(credentials)
                .when()
                .post("/api/auth/login")
                .then()
                .spec(responseSpec)
                .statusCode(200)
                .extract().response();
        String jwtCookie = response1.getCookie("jwt");

        //approve an expense that is already approved (seeded)
        Response response2 = given()
                .spec(requestSpec)
                .cookie("jwt", jwtCookie)
                .when()
                .post("/api/expenses/"+ expenseId +"/approve")
                .then()
                .statusCode(200)
                .extract().response();

        String message = response2.jsonPath().getString("message");
        assertEquals("Expense approved successfully", message);
    }
}
