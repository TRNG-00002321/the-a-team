package com.revature.end_to_end_tests.hooks;

import com.revature.end_to_end_tests.context.TestContext;
import io.cucumber.java.After;
import io.cucumber.java.Before;

public class Hooks {
    private final TestContext context;

    public Hooks(TestContext context) {
        this.context = context;
    }

    // Before each scenario
    @Before
    public void setUp(){
        context.initializeDriver(true);
        //Reset scenario state
        context.getDriver().manage().deleteAllCookies();
        context.getDriver().navigate().refresh();
        context.getDriver().get("http://localhost:5001/");
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
