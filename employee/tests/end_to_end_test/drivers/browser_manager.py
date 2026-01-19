import os
import sys
from selenium import webdriver
from selenium.webdriver.chrome.service import Service as ChromeService
from selenium.webdriver.firefox.service import Service as FirefoxService
from webdriver_manager.chrome import ChromeDriverManager
from webdriver_manager.firefox import GeckoDriverManager

def create_driver(browser: str, headless: bool):
    selenium_remote_url = os.getenv("SELENIUM_REMOTE_URL")
    
    # Force output to stderr (not buffered)
    sys.stderr.write(f"\n{'='*60}\n")
    sys.stderr.write(f"DEBUG create_driver() called\n")
    sys.stderr.write(f"SELENIUM_REMOTE_URL = '{selenium_remote_url}'\n")
    sys.stderr.write(f"Type: {type(selenium_remote_url)}\n")
    sys.stderr.write(f"Is None? {selenium_remote_url is None}\n")
    sys.stderr.write(f"Bool value? {bool(selenium_remote_url)}\n")
    sys.stderr.write(f"Browser = {browser}\n")
    sys.stderr.write(f"Headless = {headless}\n")
    sys.stderr.write(f"{'='*60}\n\n")
    sys.stderr.flush()
    
    # Explicit check with whitespace stripping
    if selenium_remote_url is not None and selenium_remote_url.strip():
        sys.stderr.write(f">>> USING REMOTE SELENIUM GRID at {selenium_remote_url}\n")
        sys.stderr.flush()
        
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
    
    # LOCAL DRIVER
    sys.stderr.write(">>> USING LOCAL CHROMEDRIVER (webdriver-manager)\n")
    sys.stderr.flush()
    
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