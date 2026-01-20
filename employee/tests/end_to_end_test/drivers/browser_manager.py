import os
from selenium import webdriver

def create_driver(browser_name: str, headless: bool = False):
    browser = browser_name.lower()
    remote_url = os.getenv("SELENIUM_REMOTE_URL")
    running_in_docker = os.path.exists("/.dockerenv")

    # -------------------------------------------------
    # Docker + Selenium Grid (CI / Compose)
    # -------------------------------------------------
    if running_in_docker:
        if not remote_url:
            raise RuntimeError(
                "SELENIUM_REMOTE_URL must be set when running in Docker."
            )

        if browser == "chrome":
            options = webdriver.ChromeOptions()
            if headless:
                options.add_argument("--headless=new")
            return webdriver.Remote(command_executor=remote_url, options=options)

        elif browser == "firefox":
            options = webdriver.FirefoxOptions()
            if headless:
                options.add_argument("-headless")
            return webdriver.Remote(command_executor=remote_url, options=options)

        elif browser == "edge":
            options = webdriver.EdgeOptions()
            if headless:
                options.add_argument("--headless=new")
            return webdriver.Remote(command_executor=remote_url, options=options)

        else:
            raise ValueError(f"Unsupported browser: {browser}")

    # -------------------------------------------------
    # Local developer execution (NO Docker)
    # -------------------------------------------------
    from selenium.webdriver.chrome.service import Service as ChromeService
    from selenium.webdriver.firefox.service import Service as FirefoxService
    from selenium.webdriver.edge.service import Service as EdgeService
    from webdriver_manager.chrome import ChromeDriverManager
    from webdriver_manager.firefox import GeckoDriverManager
    from webdriver_manager.microsoft import EdgeChromiumDriverManager

    if browser == "chrome":
        options = webdriver.ChromeOptions()
        if headless:
            options.add_argument("--headless=new")
        return webdriver.Chrome(
            service=ChromeService(ChromeDriverManager().install()),
            options=options
        )

    elif browser == "firefox":
        options = webdriver.FirefoxOptions()
        if headless:
            options.add_argument("-headless")
        return webdriver.Firefox(
            service=FirefoxService(GeckoDriverManager().install()),
            options=options
        )

    elif browser == "edge":
        options = webdriver.EdgeOptions()
        if headless:
            options.add_argument("--headless=new")
        return webdriver.Edge(
            service=EdgeService(EdgeChromiumDriverManager().install()),
            options=options
        )

    else:
        raise ValueError(f"Unsupported browser: {browser}")
