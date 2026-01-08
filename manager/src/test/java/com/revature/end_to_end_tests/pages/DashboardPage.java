package com.revature.end_to_end_tests.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class DashboardPage extends BasePage {

    @FindBy(id = "show-pending")
    private WebElement pendingExpensesButon;
    @FindBy(id = "show-all-expenses")
    private WebElement allExpensesButton;
    @FindBy(id = "show-reports")
    private WebElement generateReportsButton;
    @FindBy(id = "logout-btn")
    private WebElement logoutButton;
    @FindBy(id = "generate-all-expenses-report")
    private WebElement AllExpensesReport;
    @FindBy(id = "generate-pending-report")
    private WebElement PendingReport;

    private final By flashMessage = By.id("report-message");

    private By reportInputs = By.cssSelector("div.report-section input[type='text']");

    private final By reportMessage = By.id("report-message");


    public DashboardPage(WebDriver driver) {
        super(driver);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT));
        PageFactory.initElements(driver, this);
    }

    // DEFINED ACTIONS
    public void goToPendingExpensesScreen() {
        wait.until(ExpectedConditions.visibilityOf(pendingExpensesButon));
        pendingExpensesButon.click();
    }

    public void goToAllExpensesScreen() {
        wait.until(ExpectedConditions.visibilityOf(allExpensesButton));
        allExpensesButton.click();
    }


    public LoginPage logout() {
        wait.until(ExpectedConditions.visibilityOf(logoutButton));
        logoutButton.click();
        return new LoginPage(this.driver);
    }

    public void goToGenerateReportsScreen() {
        wait.until(ExpectedConditions.visibilityOf(generateReportsButton));
        generateReportsButton.click();
    }

    // REPORT GENERATION
    public void generateByExpenses() {
        wait.until(ExpectedConditions.visibilityOf(AllExpensesReport));
        AllExpensesReport.click();
    }

    public void generateByPending() {
        wait.until(ExpectedConditions.visibilityOf(PendingReport));
        PendingReport.click();
    }

    public String getFlashMessage() {
        return getText(flashMessage);
    }

    public List<WebElement> getAllInputs() {
        return driver.findElements(reportInputs);
    }

    public String getMessageText() {
        return getText(reportMessage);
    }

}


