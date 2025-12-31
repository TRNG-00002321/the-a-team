package com.revature.end_to_end_tests.hooks;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

public class Hooks {
    private static WebDriver driver;

    // Before each scenario
    @Before
    public void setUp(Scenario scenario){

    }

    /**
     * Runs only for scenarios tagged with @database.
     * Sets up database connection and test data.
     */
    @Before("@database")
    public void setUpDatabase(Scenario scenario) {
        System.out.println("Setting up database for: " + scenario.getName());
        // TODO: Implement database setup
        // 1. Connect to test database
        // 2. Clear test data
        // 3. Insert required fixtures
    }

    /**
     * Runs only for scenarios tagged with @database.
     * Cleans up database after test.
     */
    @After("@database")
    public void tearDownDatabase(Scenario scenario) {
        System.out.println("Cleaning up database after: " + scenario.getName());
        // TODO: Implement database cleanup
        // 1. Delete test data
        // 2. Close connection
    }

    // After each scenario
    @After
    public void tearDown(Scenario scenario) {

    }


    /**
     * Captures screenshot and attaches to Cucumber report.
     */
    private void captureScreenshot(Scenario scenario) {
        // TODO: Implement screenshot capture
        // 1. Take screenshot as byte array
        // 2. Attach to scenario with name

        final byte[] screenshot = ((TakesScreenshot) driver)
                .getScreenshotAs(OutputType.BYTES);
        scenario.attach(screenshot, "image/png", scenario.getName());
    }
}
