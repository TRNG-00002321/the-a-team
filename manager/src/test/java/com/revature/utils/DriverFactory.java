package com.revature.utils;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.URL;
import java.util.Map;

public class DriverFactory {

    public static WebDriver createDriver(String browser, boolean headless) {
        // Check if running in CI/Jenkins with Selenium Grid
        String seleniumRemoteUrl = System.getenv("SELENIUM_REMOTE_URL");
        boolean useRemoteDriver = seleniumRemoteUrl != null && !seleniumRemoteUrl.isEmpty();

        WebDriver driver;

        switch (browser.toLowerCase()) {

            case "chrome" -> {
                ChromeOptions options = new ChromeOptions();
                if (headless) {
                    options.addArguments("--headless=new");
                }

                // Common Chrome arguments for stability (especially in Docker)
                options.addArguments("--no-sandbox");
                options.addArguments("--disable-dev-shm-usage");
                options.addArguments("--disable-gpu");
                options.addArguments("--window-size=1920,1080");

                options.setExperimentalOption(
                        "prefs",
                        Map.of("profile.password_manager_leak_detection", false)
                );

                if (useRemoteDriver) {
                    System.out.println("Creating Remote ChromeDriver at: " + seleniumRemoteUrl);
                    driver = createRemoteDriver(seleniumRemoteUrl, options);
                } else {
                    System.out.println("Creating local ChromeDriver");
                    WebDriverManager.chromedriver().setup();
                    driver = new ChromeDriver(options);
                }
            }

            case "firefox" -> {
                FirefoxOptions options = new FirefoxOptions();
                if (headless) {
                    options.addArguments("-headless");
                }

                if (useRemoteDriver) {
                    System.out.println("Creating Remote FirefoxDriver at: " + seleniumRemoteUrl);
                    driver = createRemoteDriver(seleniumRemoteUrl, options);
                } else {
                    System.out.println("Creating local FirefoxDriver");
                    WebDriverManager.firefoxdriver().setup();
                    driver = new FirefoxDriver(options);
                }
            }

            case "edge" -> {
                EdgeOptions options = new EdgeOptions();
                if (headless) {
                    options.addArguments("--headless=new");
                }

                options.addArguments("--no-sandbox");
                options.addArguments("--disable-dev-shm-usage");

                if (useRemoteDriver) {
                    System.out.println("Creating Remote EdgeDriver at: " + seleniumRemoteUrl);
                    driver = createRemoteDriver(seleniumRemoteUrl, options);
                } else {
                    System.out.println("Creating local EdgeDriver");
                    WebDriverManager.edgedriver().setup();
                    driver = new EdgeDriver(options);
                }
            }

            default -> throw new IllegalArgumentException(
                    "Unsupported browser: " + browser
            );
        }

        driver.manage().window().maximize();
        return driver;
    }

    /**
     * Creates a RemoteWebDriver instance for Selenium Grid execution.
     */
    private static WebDriver createRemoteDriver(String remoteUrl, Object options) {
        try {
            return new RemoteWebDriver(new URL(remoteUrl), (org.openqa.selenium.Capabilities) options);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create RemoteWebDriver at " + remoteUrl + ": " + e.getMessage(), e);
        }
    }
}
