"""
cross_browser_automated.py
Automated driver setup for all major browsers
"""
import os
from selenium import webdriver
from selenium.webdriver.chrome.service import Service as ChromeService
from selenium.webdriver.firefox.service import Service as FirefoxService
from selenium.webdriver.edge.service import Service as EdgeService
from webdriver_manager.chrome import ChromeDriverManager
from webdriver_manager.firefox import GeckoDriverManager
from webdriver_manager.microsoft import EdgeChromiumDriverManager


def create_driver(browser_name: str, headless: bool = False):
    browser = browser_name.lower()
    remote_url = os.getenv("SELENIUM_REMOTE_URL")

    if browser == "chrome":
        options = webdriver.ChromeOptions()
        if headless:
            options.add_argument("--headless=new")

        options.add_experimental_option(
            "prefs",
            {
                "profile.password_manager_leak_detection": False,
                "credentials_enable_service": False,
                "profile.password_manager_enabled": False,
            }
        )

        if remote_url:
            return webdriver.Remote(
                command_executor=remote_url,
                options=options
            )
        else:
            service = ChromeService(ChromeDriverManager().install())
            return webdriver.Chrome(service=service, options=options)

    elif browser == "firefox":
        options = webdriver.FirefoxOptions()
        if headless:
            options.add_argument("-headless")

        if remote_url:
            return webdriver.Remote(
                command_executor=remote_url,
                options=options
            )
        else:
            service = FirefoxService(GeckoDriverManager().install())
            return webdriver.Firefox(service=service, options=options)

    elif browser == "edge":
        options = webdriver.EdgeOptions()
        if headless:
            options.add_argument("--headless=new")

        if remote_url:
            return webdriver.Remote(
                command_executor=remote_url,
                options=options
            )
        else:
            service = EdgeService(EdgeChromiumDriverManager().install())
            return webdriver.Edge(service=service, options=options)

    else:
        raise ValueError(f"Unsupported browser: {browser_name}")
