package com.revature.end_to_end_tests.steps;

import com.revature.end_to_end_tests.context.TestContext;
import com.revature.end_to_end_tests.pages.DashboardPage;
import io.cucumber.java.PendingException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ReportSteps {

    private TestContext context;
    private DashboardPage dashboardPage;

    public ReportSteps(TestContext context) {
        this.context=context;
    }
    private DashboardPage dashboardPage(){
       return context.dashboardPage();
}
    @Given("the manager is on the generate reports screen")
    public void theManagerIsOnTheGenerateReportsScreen() {
        dashboardPage = context.dashboardPage();
        dashboardPage.goToGenerateReportsScreen();
    }

    @When("the manager clicks the all expenses report button")
    public void theManagerClicksTheAllExpensesReportButton() {
        dashboardPage = context.dashboardPage();
        dashboardPage.generateByExpenses();
    }

    @Then("a report is downloaded, and a {string} message is shown")
    public void aReportIsDownloadedAndAMessageIsShown(String expectedMessage) {
        dashboardPage = context.dashboardPage();
        String actualMessage = dashboardPage().getMessageText();
        assertTrue(actualMessage.contains(expectedMessage),
                "Expected message containing '" + expectedMessage + "' but got '" + actualMessage + "'");

    }

    @When("the manager clicks the pending expenses report button")
    public void theManagerClicksThePendingExpensesReportButton() {
        dashboardPage = context.dashboardPage();
        dashboardPage.generateByPending();
    }

    @And("report all input fields are empty")
    public void reportAllInputFieldsAreEmpty() {
        dashboardPage = context.dashboardPage();
        List<WebElement> inputs = dashboardPage.getAllInputs();
        for (WebElement input : inputs) {
            String value = input.getAttribute("value");
            Assertions.assertTrue(value.isEmpty(), "Input field is not empty: " + input.getAttribute("name"));
        }
    }

    @Then("a message containing: {string} is shown")
    public void aMessageContainingIsShown(String expectedMessage) {
        dashboardPage = context.dashboardPage();
        String actualMessage = dashboardPage().getMessageText();
        assertTrue(
                actualMessage.contains(expectedMessage),
                "Expected confirmation message to contain '" + expectedMessage + "' but got '" + actualMessage + "'"
        );

    }

    @When("the manager inputs {string} into the report employee id field")
    public void theManagerInputsIntoTheReportEmployeeIdField(String employeeId) {
        WebDriverWait wait = new WebDriverWait(context.getDriver(), Duration.ofSeconds(10));

        WebElement employeeIdField = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("employee-report-id"))
        );

        employeeIdField.clear();
        employeeIdField.sendKeys(employeeId);
    }

    @When("the manager inputs {string} into the report category field")
    public void theManagerInputsIntoTheReportCategoryField(String categoryText) {
        WebDriverWait wait = new WebDriverWait(context.getDriver(), Duration.ofSeconds(10));

        WebElement categoryField = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("category-report"))
        );
        categoryField.clear();
        categoryField.sendKeys(categoryText);
    }

    @When("the manager inputs {string} into the start date field")
    public void theManagerInputsIntoTheStartDateField(String startDate) {

        WebDriverWait wait = new WebDriverWait(context.getDriver(), Duration.ofSeconds(10));
        WebElement startDateField = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("start-date"))
        );

        // Handle keyword values
        if (startDate.equalsIgnoreCase("today")) {
            startDate = LocalDate.now()
                    .format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        }

        startDateField.clear();
        startDateField.sendKeys(startDate);

        // Ensure value is set before continuing
        wait.until(driver ->
                startDateField.getAttribute("value").length() > 0
        );
    }

    @And("the manager inputs {string} into the end date field")
    public void theManagerInputsIntoTheEndDateField(String endDate) {

        WebDriverWait wait = new WebDriverWait(context.getDriver(), Duration.ofSeconds(10));
        WebElement endDateField = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("end-date"))
        );

        if (endDate.equalsIgnoreCase("today")) {
            endDate = LocalDate.now()
                    .format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        }

        endDateField.clear();
        endDateField.sendKeys(endDate);

        wait.until(driver ->
                endDateField.getAttribute("value").length() > 0
        );
    }

    @When("the manager clicks the {string} report button")
    public void theManagerClicksTheReport(String reportType) {

        // Determine the button ID based on the reportType string
        String buttonId;

        switch (reportType.toLowerCase()) {
            case "employee":
            case "generate employee report":
                buttonId = "generate-employee-report";
                break;
            case "category":
            case "generate category report":
                buttonId = "generate-category-report";
                break;
            case "date":
            case "generate date range report":
                buttonId = "generate-date-range-report";
                break;
            default:
                throw new IllegalArgumentException("Unknown report type: " + reportType);
        }

        // Wait for the button to be clickable and click it
        WebDriverWait wait = new WebDriverWait(context.getDriver(), Duration.ofSeconds(10));
        WebElement generateButton = wait.until(ExpectedConditions.elementToBeClickable(By.id(buttonId)));
        generateButton.click();
      }
    }

