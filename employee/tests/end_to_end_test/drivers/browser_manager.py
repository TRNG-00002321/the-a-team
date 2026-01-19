import os
from selenium import webdriver
from selenium.webdriver.chrome.service import Service as ChromeService
from selenium.webdriver.firefox.service import Service as FirefoxService
from webdriver_manager.chrome import ChromeDriverManager
from webdriver_manager.firefox import GeckoDriverManager

def create_driver(browser: str, headless: bool):
    selenium_remote_url = os.getenv("SELENIUM_REMOTE_URL")
    
    # Add debug output
    print(f"DEBUG: SELENIUM_REMOTE_URL = '{selenium_remote_url}'")
    print(f"DEBUG: Browser = {browser}")
    print(f"DEBUG: Headless = {headless}")
    
    # -----------------------------
    # REMOTE DRIVER (CI / Docker)
    # -----------------------------
    if selenium_remote_url:  # This check should work if the env var is set
        print(f"DEBUG: Using REMOTE driver at {selenium_remote_url}")
        
        if browser == "firefox":
            options = webdriver.FirefoxOptions()
            if headless:
                options.add_argument("-headless")
            return webdriver.Remote(
                command_executor=selenium_remote_url,
                options=options
            )
        
        # Default: Chrome
        options = webdriver.ChromeOptions()
        if headless:
            options.add_argument("--headless")
        options.add_argument("--no-sandbox")
        options.add_argument("--disable-dev-shm-usage")
        options.add_experimental_option(
            "prefs",
            {
                "profile.password_manager_leak_detection": False,
                "credentials_enable_service": False,
                "profile.password_manager_enabled": False,
            }
        )
        return webdriver.Remote(
            command_executor=selenium_remote_url,
            options=options
        )
    
    # -----------------------------
    # LOCAL DRIVER (DEV MACHINE)
    # -----------------------------
    print("DEBUG: Using LOCAL driver")
    
    if browser == "firefox":
        options = webdriver.FirefoxOptions()
        if headless:
            options.add_argument("-headless")
        service = FirefoxService(GeckoDriverManager().install())
        return webdriver.Firefox(service=service, options=options)
    
    # Default: Chrome
    options = webdriver.ChromeOptions()
    if headless:
        options.add_argument("--headless")
    options.add_experimental_option(
        "prefs",
        {
            "profile.password_manager_leak_detection": False,
            "credentials_enable_service": False,
            "profile.password_manager_enabled": False,
        }
    )
    service = ChromeService(ChromeDriverManager().install())
    return webdriver.Chrome(service=service, options=options)