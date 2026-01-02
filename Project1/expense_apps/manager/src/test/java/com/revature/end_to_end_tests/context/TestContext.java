package com.revature.end_to_end_tests.context;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import io.github.bonigarcia.wdm.WebDriverManager;

import java.time.Duration;


public class TestContext {

    private static TestContext instance;
    private WebDriver driver;

    // Page Objects
    private LoginPage loginPage;
    private SecurePage securePage;

    // Scenario data
    private String currentUser;
    private String lastMessage;

    private TestContext() {
        // Private constructor for singleton
    }

    public static synchronized TestContext getInstance() {
        if (instance == null) {
            instance = new TestContext();
        }
        return instance;
    }

    public void initializeDriver(boolean headless) {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        if (headless) {
            options.addArguments("--headless");
        }
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--disable-gpu");

        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
    }

    public WebDriver getDriver() {
        return driver;
    }

    public void quitDriver() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
        // Reset page objects
        loginPage = null;
        securePage = null;
    }

    // Page Object getters (lazy initialization)
    public LoginPage getLoginPage() {
        if (loginPage == null) {
            loginPage = new LoginPage(driver);
        }
        return loginPage;
    }

    public SecurePage getSecurePage() {
        if (securePage == null) {
            securePage = new SecurePage(driver);
        }
        return securePage;
    }


    // Scenario data accessors
    public void setCurrentUser(String user) {
        this.currentUser = user;
    }

    public String getCurrentUser() {
        return currentUser;
    }

    public void setLastMessage(String message) {
        this.lastMessage = message;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void reset() {
        currentUser = null;
        lastMessage = null;
    }
}

