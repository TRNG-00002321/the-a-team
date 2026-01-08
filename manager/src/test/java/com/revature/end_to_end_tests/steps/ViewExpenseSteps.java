package com.revature.end_to_end_tests.steps;

import com.revature.end_to_end_tests.context.TestContext;
import com.revature.end_to_end_tests.pages.DashboardPage;
import com.revature.end_to_end_tests.pages.LoginPage;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ViewExpenseSteps {
    private final TestContext context;
    private DashboardPage dashboardPage;
    private WebDriver driver;

    public ViewExpenseSteps(TestContext context){
        this.context = context;
        this.driver = context.getDriver();
    }

    @Given("the manager is on the all expenses screen")
    public void theManagerIsOnTheAllExpensesScreen() {
        LoginPage loginPage = new LoginPage(driver);
        dashboardPage = loginPage.login("manager1", "password123");
        dashboardPage.goToAllExpensesScreen();
    }

    @When("the manager clicks the show all button")
    public void theManagerClicksTheShowAllButton() {
        driver.findElement(By.id("show-all-expenses")).click();
    }

    @Then("all expenses are shown")
    public void allExpensesAreShown() {
        List<WebElement> rows = driver.findElements(By.tagName("tr"));
        assertFalse(rows.isEmpty());
    }

    @When("the manager inputs {string} for the employee id")
    public void theManagerInputsForTheEmployeeId(String arg0) {
        WebElement employee_selector = driver.findElement(By.id("employee-filter"));
        employee_selector.clear();
        employee_selector.sendKeys(arg0);
    }

    @And("the manager clicks the filter button")
    public void theManagerClicksTheFilterButton() {
        WebElement filter_button = driver.findElement(By.id("filter-by-employee"));
        filter_button.click();
    }

    @And("the manager clicks the refresh button")
    public void theManagerClicksTheRefreshButton() {
        WebElement refresh_button = driver.findElement(By.id("refresh-all-expenses"));
        refresh_button.click();
    }

    @Then("the manager is shown {string} expenses for user {string}")
    public void theManagerIsShownCountExpensesForUserId(String arg0, String arg1) {
        List<WebElement> rows = driver.findElements(By.xpath("//td[text()='employee" + arg1 + " (ID: " + arg1 + ")']"));
        assertEquals(rows.size(), Integer.parseInt(arg0) + 1);
    }

    @Then("the manager is shown the message: {string}")
    public void theManagerIsShownTheMessage(String arg0) {
        WebElement message = driver.findElement(By.tagName("p"));
        assertEquals(arg0, message.getText());
    }
}
