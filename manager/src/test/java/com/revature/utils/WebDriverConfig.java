package com.revature.utils;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.URL;

public class WebDriverConfig {
    public static WebDriver getDriver() {
        String seleniumRemoteUrl = System.getenv("SELENIUM_REMOTE_URL");

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");

        try {
            if (seleniumRemoteUrl != null && !seleniumRemoteUrl.isEmpty()) {
                // Running in Jenkins/CI - use Selenium Grid
                System.out.println("Using Remote WebDriver at: " + seleniumRemoteUrl);
                return new RemoteWebDriver(new URL(seleniumRemoteUrl), options);
            } else {
                // Running locally - use WebDriverManager
                System.out.println("Using local ChromeDriver");
                WebDriverManager.chromedriver().setup();
                return new ChromeDriver(options);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to create WebDriver: " + e.getMessage(), e);
        }
    }
}