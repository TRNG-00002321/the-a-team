package com.revature.end_to_end_tests.context;

import com.revature.end_to_end_tests.pages.DashboardPage;
import com.revature.end_to_end_tests.pages.LoginPage;
import com.revature.utils.DriverFactory;
import com.revature.utils.TestDatabaseUtil;
import io.github.cdimascio.dotenv.Dotenv;
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
    private DashboardPage dashboardPage;

    private TestContext() {
        // Private constructor for singleton
    }

    public static synchronized TestContext getInstance() {
        if (instance == null) {
            instance = new TestContext();
        }
        System.out.println("returning instance of context");
        return instance;
    }

    public void initializeDriver(boolean headless) {
        TestDatabaseUtil.resetAndSeed();
        Dotenv dotenv = Dotenv.load();
        String browser = dotenv.get("BROWSER", "chrome");
        driver = DriverFactory.createDriver(browser, headless);
        System.out.println("Set up db");
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
        dashboardPage = null;
    }

    // Page Object getters (lazy initialization)
    public LoginPage getLoginPage() {
        if (loginPage == null) {
            loginPage = new LoginPage(driver);
        }
        System.out.println("returning new login page");
        return loginPage;
    }

    public DashboardPage dashboardPage() {
        if (dashboardPage == null) {
            dashboardPage = new DashboardPage(driver);
        }
        System.out.println("returning new dashboard page");
        return dashboardPage;
    }

}
