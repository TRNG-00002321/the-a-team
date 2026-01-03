package com.revature.end_to_end_tests.context;

import com.revature.end_to_end_tests.pages.DashboardPage;
import com.revature.end_to_end_tests.pages.LoginPage;
import com.revature.utils.DriverFactory;
import com.revature.utils.TestDatabaseUtil;
import io.github.cdimascio.dotenv.Dotenv;
import org.openqa.selenium.WebDriver;


public class TestContext {

    private WebDriver driver;

    // Page Objects
    private LoginPage loginPage;
    private DashboardPage dashboardPage;

    public TestContext() {}

    public void initializeDriver(boolean headless) {
        TestDatabaseUtil.resetAndSeed();
        Dotenv dotenv = Dotenv.load();
        String browser = dotenv.get("BROWSER", "chrome");
        driver = DriverFactory.createDriver(browser, headless);
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
        return loginPage;
    }

    public DashboardPage dashboardPage() {
        if (dashboardPage == null) {
            dashboardPage = new DashboardPage(driver);
        }
        return dashboardPage;
    }
}
