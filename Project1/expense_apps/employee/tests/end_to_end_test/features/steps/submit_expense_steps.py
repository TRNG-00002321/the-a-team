from behave.api.pending_step import StepNotImplementedError
from behave import given, when, then
from selenium.webdriver.common.by import By
from selenium.webdriver.support.wait import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC

from tests.end_to_end_test.pages.dashboard_page import DashboardPage
from tests.end_to_end_test.pages.base_page import BasePage


def get_dashboard_page(context):
    if not hasattr(context, 'dashboard_page'):
        context.dashboard_page=DashboardPage(context.driver)
    return context.dashboard_page

@given(u'the employee is at the submit expense menu')
def employee_at_submit_expense_page(context):
    return context.go_to_submit_new_expense_screen()


@when(u'the employee inputs a new amount: 123')
def step_enter_amount(context, amount):
    dashboard_page = DashboardPage(context.driver)
    amount_input = dashboard_page.wait_for_element((By.ID, 'amount'))
    amount_input.clear()
    amount_input.send_keys(amount)


@when(u'the employee inputs a new description: example description')
def step_enter_description(context, description):
    dashboard_page = DashboardPage(context.driver)
    description_input = dashboard_page.wait_for_element((By.ID, 'description'))
    description_input.clear()
    description_input.send_keys(description)


@when(u'the employee inputs a new date: 2025-12-30')
def step_enter_date(context, date):
    dashboard_page = DashboardPage(context.driver)
    date_input = dashboard_page.wait_for_element((By.ID, 'date'))
    date_input.clear()
    date_input.send_keys(date)


@when(u'the employee clicks the submit expense button')
def step_click_submit_expense_button(context):
    dashboard_page = DashboardPage(context.driver)
    click_button = dashboard_page.wait_for_clickable((By.ID, 'submit'))
    click_button.click()


@then(u'the employee sees the message: "Expense submitted successfully!"')
def step_successful_expense_submission(context, text):
    dashboard_page = DashboardPage(context.driver)
    message = dashboard_page.get_text((By.ID, 'submit-message'))
    assert text in message, f"Expected '{text}' in '{message}'"


@then(u'the expense is shown with the the amount: 123, description: example description, and the date: 2025-12-30')
def step_expense_is_shown(context, amount, description, date):
    dashboard_page = DashboardPage(context.driver)
    rows = dashboard_page.wait_for_clickable((By.CSS_SELECTOR, "table#expenses tbody tr"))

    found = False
    for row in rows:
        cols = row.find_elements(By.TAG_NAME, "td")
        row_amount = cols[0].text
        row_description = cols[1].text
        row_date = cols[2].text

        if row_amount == amount and row_description == description and row_date == date:
            found = True
            break
    assert found, f"Expense not found: {amount}, {description}, {date}"


@when(u'the employee inputs a new amount: 999')
def step_enter_amount(context, amount):
    dashboard_page = DashboardPage(context.driver)
    amount_input = dashboard_page.wait_for_element((By.ID, 'amount'))
    amount_input.clear()
    amount_input.send_keys(amount)


@when(u'the employee inputs a new description: fix door')
def step_enter_description(context, description):
    dashboard_page = DashboardPage(context.driver)
    description_input = dashboard_page.wait_for_element((By.ID, 'description'))
    description_input.clear()
    description_input.send_keys(description)


@when(u'the employee inputs a new date: 2025-10-10')
def step_enter_date(context, date):
    dashboard_page = DashboardPage(context.driver)
    date_input = dashboard_page.wait_for_element((By.ID, 'date'))
    date_input.clear()
    date_input.send_keys(date)


@then(u'the expense is shown with the the amount: 999, description: fix door, and the date: 2025-10-10')
def step_expense_is_shown(context, amount, description, date):
    dashboard_page = DashboardPage(context.driver)
    rows = dashboard_page.wait_for_clickable((By.CSS_SELECTOR, "table#expenses tbody tr"))

    found = False
    for row in rows:
        cols = row.find_elements(By.TAG_NAME, "td")
        row_amount = cols[0].text
        row_description = cols[1].text
        row_date = cols[2].text

        if row_amount == amount and row_description == description and row_date == date:
            found = True
            break
    assert found, f"Expense not found: {amount}, {description}, {date}"


@when(u'the amount field is empty')
def step_empty_amount_field(context):
    dashboard_page = DashboardPage(context.driver)
    amount_field = dashboard_page.wait_for_element((By.ID, 'amount'))
    value = amount_field.get_attribute("value")
    assert value == "", f"Expected amount field is to be empty, but found '{value}'"



@then(u'the amount field is selected')
def step_empty_field_prompts_for_amount(context):
    dashboard_page = DashboardPage(context.driver)
    amount_field = dashboard_page.wait_for_element((By.ID, 'amount'))
    is_focused = amount_field == context.driver.switch_to.active_element
    assert is_focused, "Amount field is not selected or focused as expected"


@then(u'the employee stays on the submit menu screen')
def step_user_remains_on_submit_menu(context):
    dashboard_page = DashboardPage(context.driver)
    stay_on_page = dashboard_page.is_displayed((By.ID, 'submit-expense-section'))
    assert stay_on_page, "Employee is not on the submit menu"


@when(u'the employee inputs a new amount: "125"')
def step_enter_amount(context, amount):
    dashboard_page = DashboardPage(context.driver)
    amount_input = dashboard_page.wait_for_element((By.ID, 'amount'))
    amount_input.clear()
    amount_input.send_keys(amount)


@when(u'the description field is empty')
def step_empty_description_field(context):
    dashboard_page = DashboardPage(context.driver)
    description_field = dashboard_page.wait_for_element((By.ID, 'description'))
    value = description_field.get_attribute("value")
    assert value == "", f"Expected description field is to be empty, but found '{value}'"


@then(u'the description field is selected')
def step_empty_field_prompts_for_description(context):
    dashboard_page = DashboardPage(context.driver)
    description_field = dashboard_page.wait_for_element((By.ID, 'description'))
    is_focused = description_field == context.driver.switch_to.active_element
    assert is_focused, "Description field is not selected or focused as expected"


@when(u'the employee inputs a new amount: "100"')
def step_enter_amount(context, amount):
    dashboard_page = DashboardPage(context.driver)
    amount_input = dashboard_page.wait_for_element((By.ID, 'amount'))
    amount_input.clear()
    amount_input.send_keys(amount)


@when(u'the employee inputs a new description: "today\'s date"')
def step_enter_description(context, description):
    dashboard_page = DashboardPage(context.driver)
    description_input = dashboard_page.wait_for_element((By.ID, 'description'))
    description_input.clear()
    description_input.send_keys(description)

@then(u'an expense with today\'s date, amount: "100" and description: "today\'s date" is shown')
def step_automatic_date_inception(context, amount,description, date):
    dashboard_page = DashboardPage(context.driver)
    rows = dashboard_page.wait_for_clickable((By.CSS_SELECTOR, "table#expenses tbody tr"))
    expected_date = date.today().strftime("%Y-%m-%d")

    found = False
    for row in rows:
        cols = row.find_elements(By.TAG_NAME, "td")
        row_amount = cols[0].text
        row_description = cols[1].text
        row_date = cols[2].text

        if row_amount == amount and row_description == description and row_date == expected_date:
            found = True
            break
    assert found, f"Expense not found: {amount}, {description}, {date}"
    assert date == expected_date, f"Expected data {expected_date}, but got {date}"

