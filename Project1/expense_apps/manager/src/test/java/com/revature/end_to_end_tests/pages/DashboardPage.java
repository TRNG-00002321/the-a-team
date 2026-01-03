package com.revature.end_to_end_tests.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class DashboardPage extends BasePage{

    @FindBy(id="show-pending")
    private WebElement pendingExpensesButon;
    @FindBy(id="show-all-expenses")
    private WebElement allExpensesButton;
    @FindBy(id="show-reports")
    private WebElement generateReportsButton;
    @FindBy(id="logout-btn")
    private WebElement logoutButton;

    public DashboardPage(WebDriver driver){
        super(driver);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT));
        PageFactory.initElements(driver, this);
    }

    public void goToPendingExpensesScreen(){
        wait.until(ExpectedConditions.elementToBeClickable(pendingExpensesButon));
        pendingExpensesButon.click();
    }

    public void goToAllExpensesScreen(){
        wait.until(ExpectedConditions.elementToBeClickable(allExpensesButton));
        allExpensesButton.click();
    }

    public void goToGenerateReportsScreen(){
        wait.until(ExpectedConditions.elementToBeClickable(generateReportsButton));
        generateReportsButton.click();
    }

    public LoginPage logout(){
        wait.until(ExpectedConditions.elementToBeClickable(logoutButton));
        logoutButton.click();
        return new LoginPage(this.driver);
    }
}
