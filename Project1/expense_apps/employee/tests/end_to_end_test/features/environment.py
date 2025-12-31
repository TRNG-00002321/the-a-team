"""
Complete Behave environment hooks implementation.

Hook execution order:
- before_all
  - before_tag (for each tag)
    - before_feature
      - before_scenario
        - before_step
          [step execution]
        - after_step
      - after_scenario
    - after_feature
  - after_tag
- after_all
"""

from selenium import webdriver
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.chrome.options import Options
from webdriver_manager.chrome import ChromeDriverManager
import os
from datetime import datetime

#implement multi browser functionality?

def before_all(context):
    """
    Execute once before all tests.
    Use for global setup like configuration loading.
    """
    pass


def after_all(context):
    """
    Execute once after all tests.
    Use for final cleanup and reporting.
    """
    pass


def before_scenario(context, scenario):
    """
    Execute before each scenario.
    Initialize WebDriver and page objects.
    """
    pass

    print(f"\n  🧪 Scenario: {scenario.name}")

    # Determine if headless mode
    headless = context.config.userdata.get('headless', 'false').lower() == 'true'

    # Check for @headless tag
    if 'headless' in scenario.effective_tags:
        headless = True

    # Initialize WebDriver
    options = Options()
    if headless:
        options.add_argument('--headless')
        options.add_argument('--no-sandbox')
        options.add_argument('--disable-dev-shm-usage')

    options.add_argument('--window-size=1920,1080')

    service = Service(ChromeDriverManager().install())
    context.driver = webdriver.Chrome(service=service, options=options)
    context.driver.implicitly_wait(10)

    # Store scenario start time
    context.scenario_start_time = datetime.now()


def after_scenario(context, scenario):
    """
    Execute after each scenario.
    Handle cleanup and failure screenshots.
    """
    pass

    duration = datetime.now() - context.scenario_start_time

    # Take screenshot on failure
    if scenario.status == 'failed':
        _capture_screenshot(context, scenario)
        print(f"     ❌ FAILED ({duration.total_seconds():.2f}s)")
    else:
        print(f"     ✅ PASSED ({duration.total_seconds():.2f}s)")

    # Cleanup WebDriver
    if hasattr(context, 'driver') and context.driver:
        context.driver.quit()


def _capture_screenshot(context, scenario):
    """
    Capture screenshot and attach to report.
    """
    pass

    try:
        # Generate filename
        timestamp = datetime.now().strftime('%Y%m%d_%H%M%S')
        scenario_name = scenario.name.replace(' ', '_')[:50]
        filename = f"screenshots/{timestamp}_{scenario_name}.png"

        # Save screenshot
        context.driver.save_screenshot(filename)
        print(f"     📸 Screenshot saved: {filename}")

        # Attach to scenario for reporting
        with open(filename, 'rb') as f:
            screenshot_data = f.read()

        # Attach for Allure or other reporters
        if hasattr(context, '_runner'):
            context._runner.current_match.attach(
                screenshot_data,
                mime_type='image/png',
                name=f'{scenario_name}_failure'
            )

    except Exception as e:
        print(f"     ⚠️  Failed to capture screenshot: {e}")