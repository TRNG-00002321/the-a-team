package com.revature.end_to_end_tests.hooks;

import com.revature.end_to_end_tests.context.TestContext;
import com.revature.utils.DriverFactory;
import com.revature.utils.TestDatabaseUtil;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.github.cdimascio.dotenv.Dotenv;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Hooks {
    private TestContext context;

    public Hooks(){
        context = TestContext.getInstance();
    }

    // Before each scenario
    @Before
    public void setUp(){
        context.initializeDriver(true);
        System.out.println("Set up db context");
    }
    @After
    public void tearDown(){
        context.quitDriver();
    }

    /**
     * Captures screenshot and attaches to Cucumber report.
     */
    /*
    private void captureScreenshot(Scenario scenario) {
        // TODO: Implement screenshot capture
        // 1. Take screenshot as byte array
        // 2. Attach to scenario with name

        final byte[] screenshot = ((TakesScreenshot) driver)
                .getScreenshotAs(OutputType.BYTES);
        scenario.attach(screenshot, "image/png", scenario.getName());
    }
    */
}
