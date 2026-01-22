package com.revature.end_to_end_tests.steps;

import com.revature.end_to_end_tests.context.TestContext;
import com.revature.end_to_end_tests.pages.DashboardPage;
import com.revature.end_to_end_tests.pages.LoginPage;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static org.junit.jupiter.api.Assertions.*;

public class AuthSteps {
    private final TestContext context;
    private LoginPage loginPage;
    private DashboardPage dashboardPage;

    public AuthSteps(TestContext context){
        this.context = context;
    }

    @Given("the application is running")
    public void theApplicationIsRunning() {
        assertNotNull(context.getDriver(), "WebDriver should be initialized");
    }

    @And("the test database is already seeded with users")
    public void theTestDatabaseIsAlreadySeededWithUsers() {
        // Write code here that turns the phrase above into concrete actions
        assertTrue(true);
    }

    @Given("the manager is on the login screen")
    public void theManagerIsOnTheLoginScreen() {
        // Write code here that turns the phrase above into concrete actions
        loginPage = context.getLoginPage();
        assertNotNull(loginPage, "LoginPage should not be null");
    }

    @When("the manager enters username {string}")
    public void theManagerEntersUsername(String username) {
        // Write code here that turns the phrase above into concrete actions
        loginPage.enterUsername(username);
    }

    @And("the manager enters password {string}")
    public void theManagerEntersPassword(String password) {
        // Write code here that turns the phrase above into concrete actions
        loginPage.enterPassword(password);
    }

    @And("the manager clicks the login button")
    public void theManagerClicksTheLoginButton() {
        // Write code here that turns the phrase above into concrete actions
        dashboardPage = loginPage.clickLogin();
    }

    @Then("the manager sees the message: {string}")
    public void theManagerSeesTheMessage(String arg0) {
        // Write code here that turns the phrase above into concrete actions
        WebElement message = loginPage.waitForElement(By.id("login-message"));
        assertTrue(message.getText().contains(arg0));
    }

    @Then("the manager {string} redirected to the manager dashboard")
    public void theManagerIsRedirectedToTheManagerDashboard(String condition) {
        // Write code here that turns the phrase above into concrete actions
        if(condition.equals("is")){
        WebElement title = dashboardPage.waitForElement(
                By.xpath("//h1[normalize-space()='Manager Expense Dashboard']"));

        assertTrue(title.getText().contains("Manager Expense Dashboard"));
        }
        else {
            WebElement title = dashboardPage.waitForElement(
                    By.xpath("//h1[normalize-space()='Manager Expense Portal']"));

            assertTrue(title.getText().contains("Manager Expense Portal"));
        }
    }

    @Then("the manager is not redirected to the dashboard")
    public void theManagerIsNotRedirectedToTheDashboard() {
        // Write code here that turns the phrase above into concrete actions
        assertNotEquals("Manager Expense Dashboard", context.getDriver().getTitle());
    }

    @When("the manager does not input any value for username")
    public void theManagerDoesNotInputAnyValueForUsername() {
        // Write code here that turns the phrase above into concrete actions
        loginPage.enterUsername("");
        loginPage.enterPassword("password123");
    }

    @Then("the username field is selected")
    public void theUsernameFieldIsSelected() {
        // Write code here that turns the phrase above into concrete actions
        WebElement active = context.getDriver().switchTo().activeElement();
        WebElement username = loginPage.waitForElement(By.id("username"));

        assertEquals(username, active);
    }

    @When("the manager does not input any value for the password")
    public void theManagerDoesNotInputAnyValueForThePassword() {
        // Write code here that turns the phrase above into concrete actions
        loginPage.enterUsername("manager1");
        loginPage.enterPassword("");
    }

    @Then("the password field is selected")
    public void thePasswordFieldIsSelected() {
        // Write code here that turns the phrase above into concrete actions
        WebElement active = context.getDriver().switchTo().activeElement();
        WebElement password = loginPage.waitForElement(By.id("password"));

        assertEquals(password, active);
    }

    @Given("the manager is logged in")
    public void theManagerIsLoggedIn() {
        // Write code here that turns the phrase above into concrete actions
        loginPage = context.getLoginPage();
        dashboardPage = loginPage.login("manager1","password123");
    }

    @When("the manager clicks the logout button")
    public void theManagerClicksTheLogoutButton() {
        // Write code here that turns the phrase above into concrete actions
        dashboardPage.logout();
    }

    @Then("the manager is redirected to the login page")
    public void theManagerIsRedirectedToTheLoginPage() {
        // Write code here that turns the phrase above into concrete actions
        assertEquals("Manager Login - Expense Manager", context.getDriver().getTitle());
    }


}
