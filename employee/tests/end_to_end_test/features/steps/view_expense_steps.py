from behave import given, when, then
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import Select
from selenium.webdriver.support.ui import WebDriverWait

STATUS_FILTER = (By.ID, "status-filter")          # <select>
EXPENSE_ROWS = (By.CSS_SELECTOR, "table tbody tr")

# HELPER FUNCTIONS

def _table_matches_status(driver, expected_status):
    rows = driver.find_elements(By.CSS_SELECTOR, "table tbody tr")

    for row in rows:
        cells = row.find_elements(By.TAG_NAME, "td")

        # Skip spacer / empty rows
        if len(cells) < 4:
            continue

        actual_status = cells[3].text.strip().upper()

        if actual_status != expected_status.upper():
            return False

    return True

def _has_at_least_one_data_row(driver):
    rows = driver.find_elements(By.CSS_SELECTOR, "table tbody tr")

    for row in rows:
        cells = row.find_elements(By.TAG_NAME, "td")
        if len(cells) >= 4:
            return True

    return False

def _select_status(context, status):
    select_el = context.dashboard_page.wait_for_element(STATUS_FILTER)
    select = Select(select_el)

    select.select_by_visible_text(status)

    # Special case: "All" shows mixed statuses
    if status.lower() == "all":
        WebDriverWait(context.driver, 5).until(
            lambda d: _has_at_least_one_data_row(d)
        )
    else:
        WebDriverWait(context.driver, 5).until(
            lambda d: _table_matches_status(d, status)
        )

#STEP DEFINITIONS

@when(u'the filter by status option is selected for all')
def step_impl(context):
    _select_status(context, "All")


@then(u'all expenses are shown')
def step_impl(context):
    rows = context.driver.find_elements(*EXPENSE_ROWS)
    assert len(rows) > 0, "No expenses are displayed"


@when(u'the filter by status option {status} is selected')
def step_impl(context, status):
    _select_status(context, status)


@then(u'all expenses only with status: {status} are shown')
def step_impl(context, status):
    rows = context.driver.find_elements(*EXPENSE_ROWS)
    assert len(rows) > 0, "No expenses are displayed"

    #Check each row in the table
    for row in rows:
        cells = row.find_elements(By.TAG_NAME, "td")

        # Skip header, spacer, or invalid rows
        if len(cells) < 4:
            continue

        # Status column is the 4th column (index 3)
        actual_status = cells[3].text.strip()

        assert actual_status.lower() == status.lower(), (
            f"Expected status '{status}', but found '{actual_status}'"
        )

