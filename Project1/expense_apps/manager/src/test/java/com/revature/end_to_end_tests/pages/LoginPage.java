package com.revature.end_to_end_tests.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class LoginPage extends BasePage{
    protected WebDriver driver;
    protected WebDriverWait wait;
    protected static final int DEFAULT_TIMEOUT = 10;

    @FindBy(id="username")
    private WebElement usernameField;
    @FindBy(id="password")
    private WebElement passwordField;
    @FindBy(css="button[type='submit']")
    private WebElement loginButton;

    public LoginPage(WebDriver driver){
        super(driver);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT));
        PageFactory.initElements(driver, this);
    }

    public void enterUsername(String username) {
        usernameField.clear();
        usernameField.sendKeys(username);
    }

    public void enterPassword(String password) {
        passwordField.clear();
        passwordField.sendKeys(password);
    }

    public DashboardPage clickLogin() {
        loginButton.click();
        return new DashboardPage(this.driver);
    }

    public LoginPage clickLoginExpectingError(){
        loginButton.click();
        return this;
    }

    public DashboardPage login(String username, String password){
        enterUsername(username);
        enterPassword(password);
        return clickLogin();
    }


}
