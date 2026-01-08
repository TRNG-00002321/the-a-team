from datetime import time

from behave.api.pending_step import StepNotImplementedError
from behave import given, when, then
from selenium.webdriver.common.by import By
from selenium.webdriver.support.wait import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from datetime import datetime, date

from tests.end_to_end_test.pages.dashboard_page import DashboardPage
import time



def get_dashboard_page(context):
    if not hasattr(context, 'dashboard_page'):
        context.dashboard_page=DashboardPage(context.driver)
    return context.dashboard_page

@given(u'the employee is at the submit expense menu')
def employee_at_submit_expense_page(context):
    context.dashboard_page = DashboardPage(context.driver)
    return context.dashboard_page.go_to_submit_new_expense_screen()


@when(u'the employee inputs a new amount: "{amount}"')
def step_enter_amount(context, amount):
    context.dashboard_page = DashboardPage(context.driver)
    amount_input = context.dashboard_page.wait_for_element((By.ID, 'amount'))
    amount_input.clear()
    amount_input.send_keys(amount)


@when(u'the employee inputs a new description: "{description}"')
def step_enter_description(context, description):
    context.dashboard_page = DashboardPage(context.driver)
    description_input = context.dashboard_page.wait_for_element((By.ID, 'description'))
    description_input.clear()
    description_input.send_keys(description)


@when(u'the employee inputs a new date: "{date}"')
def step_enter_date(context, date):
    context.dashboard_page = DashboardPage(context.driver)
    year = date[0:4]
    month = date[5:7]
    day = date[8:10]
    new_date = month + "-" + day + "-" + year
    date_field_locator = (By.ID, "date")
    context.dashboard_page.type(date_field_locator, new_date)


@when(u'the employee clicks the submit expense button')
def step_click_submit_expense_button(context):
    context.dashboard_page = DashboardPage(context.driver)
    click_button = context.dashboard_page.wait_for_clickable((By.XPATH, "//button[@type='submit']"))
    click_button.click()


@then(u'the employee sees the message: "{text}"')
def step_successful_expense_submission(context, text):
    context.dashboard_page = DashboardPage(context.driver)
    message = context.dashboard_page.get_text((By.CSS_SELECTOR, '#submit-message > p'))
    assert text in message, f"Expected {text} in {message}"


@then(u'the employee navigates to the expenses screen')
def step_go_to_expenses(context):
    context.dashboard_page = DashboardPage(context.driver)
    return context.dashboard_page.go_to_view_my_expenses_screen()


@then(u'the expense is shown with the amount: "{amount}", description: "{description}", and the date: "{date}"')
def step_expense_is_shown(context, amount, description, date):
     context.dashboard_page = DashboardPage(context.driver)

     context.dashboard_page.wait_for_element((By.XPATH, f"//td[contains(text(), '{date}')]"))
     context.dashboard_page.wait_for_element((By.XPATH, f"//td[contains(text(), '${amount}')]"))
     context.dashboard_page.wait_for_element((By.XPATH, f"//td[contains(text(), '{description}')]"))
     #time.sleep(1)
     rows = context.driver.find_elements(By.TAG_NAME, "tr")
     found = False
     expected_amount = "$" + amount
     for row in rows[1:]:
         row_date = row.find_element(By.CSS_SELECTOR, "td:nth-child(1)").text
         row_amount = row.find_element(By.CSS_SELECTOR, "td:nth-child(2)").text
         row_description = row.find_element(By.CSS_SELECTOR, "td:nth-child(3)").text
         if row_date == date and expected_amount in row_amount and row_description == description:
             found = True
             break
     assert found, f"Expense not found: {amount}, {description}, {date}"

#@when(u'the employee inputs a new amount: 999')
#def step_enter_amount(context, amount):
#    #dashboard_page = DashboardPage(context.driver)
#    amount_input = context.dashboard_page.wait_for_element((By.ID, 'amount'))
#    amount_input.clear()
#    amount_input.send_keys(amount)


#@when(u'the employee inputs a new description: fix door')
#def step_enter_description(context, description):
#    #dashboard_page = DashboardPage(context.driver)
#    description_input = context.dashboard_page.wait_for_element((By.ID, 'description'))
#    description_input.clear()
#    description_input.send_keys(description)


#@when(u'the employee inputs a new date: 2025-10-10')
#def step_enter_date(context, date):
#    #dashboard_page = DashboardPage(context.driver)
#    date_input = context.dashboard_page.wait_for_element((By.ID, 'date'))
#    date_input.clear()
#    date_input.send_keys(date)


#@then(u'the expense is shown with the amount: 999, description: fix door, and the date: 2025-10-10')
#def step_expense_is_shown(context, amount, description, date):
#    #dashboard_page = DashboardPage(context.driver)
#    rows = context.dashboard_page.wait_for_clickable((By.CSS_SELECTOR, "table#expenses tbody tr"))

#    found = False
#    for row in rows:
#        cols = row.find_elements(By.TAG_NAME, "td")
#        row_amount = cols[0].text
#        row_description = cols[1].text
#        row_date = cols[2].text

#        if row_amount == amount and row_description == description and row_date == date:
#            found = True
#            break
#    assert found, f"Expense not found: {amount}, {description}, {date}"


@when(u'the amount field is empty')
def step_empty_amount_field(context):
    #dashboard_page = DashboardPage(context.driver)
    context.dashboard_page = DashboardPage(context.driver)
    amount_field = context.dashboard_page.wait_for_element((By.ID, 'amount'))
    value = amount_field.get_attribute("value")
    assert value == "", f"Expected amount field is to be empty, but found '{value}'"



@then(u'the amount field is selected')
def step_empty_field_prompts_for_amount(context):
    #dashboard_page = DashboardPage(context.driver)
    context.dashboard_page = DashboardPage(context.driver)
    amount_field = context.dashboard_page.wait_for_element((By.ID, 'amount'))
    is_focused = amount_field == context.driver.switch_to.active_element
    assert is_focused, "Amount field is not selected or focused as expected"


@then(u'the employee stays on the submit menu screen')
def step_user_remains_on_submit_menu(context):
    #dashboard_page = DashboardPage(context.driver)
    context.dashboard_page = DashboardPage(context.driver)
    stay_on_page = context.dashboard_page.is_displayed((By.ID, 'submit-expense-section'))
    assert stay_on_page, "Employee is not on the submit menu"


#@when(u'the employee inputs a new amount: "125"')
#def step_enter_amount(context, amount):
#    #dashboard_page = DashboardPage(context.driver)
#    amount_input = context.dashboard_page.wait_for_element((By.ID, 'amount'))
#    amount_input.clear()
#    amount_input.send_keys(amount)


@when(u'the description field is empty')
def step_empty_description_field(context):
    #dashboard_page = DashboardPage(context.driver)
    context.dashboard_page = DashboardPage(context.driver)
    description_field = context.dashboard_page.wait_for_element((By.ID, 'description'))
    value = description_field.get_attribute("value")
    assert value == "", f"Expected description field is to be empty, but found '{value}'"


@then(u'the description field is selected')
def step_empty_field_prompts_for_description(context):
    #dashboard_page = DashboardPage(context.driver)
    context.dashboard_page = DashboardPage(context.driver)
    description_field = context.dashboard_page.wait_for_element((By.ID, 'description'))
    is_focused = description_field == context.driver.switch_to.active_element
    assert is_focused, "Description field is not selected or focused as expected"


#@when(u'the employee inputs a new amount: "100"')
#def step_enter_amount(context, amount):
#    #dashboard_page = DashboardPage(context.driver)
#    amount_input = context.dashboard_page.wait_for_element((By.ID, 'amount'))
#    amount_input.clear()
#    amount_input.send_keys(amount)


#@when(u'the employee inputs a new description: "today\'s date"')
#def step_enter_description(context, description):
#    #dashboard_page = DashboardPage(context.driver)
#    description_input = context.dashboard_page.wait_for_element((By.ID, 'description'))
#    description_input.clear()
#    description_input.send_keys(description)

@then(u'an expense with today\'s date, amount: "{amount}" and description: "{description}" is shown')
def step_automatic_date_inception(context, amount, description):
    #dashboard_page = DashboardPage(context.driver)
    context.dashboard_page = DashboardPage(context.driver)

    context.dashboard_page.wait_for_element((By.XPATH, f"//td[contains(text(), '${amount}')]"))
    context.dashboard_page.wait_for_element((By.XPATH, f"//td[contains(text(), '{description}')]"))
    expected_date = date.today().strftime("%Y-%m-%d")

    rows = context.driver.find_elements(By.TAG_NAME, "tr")
    found = False
    expected_amount = "$" + amount
    for row in rows[1:]:
        row_date = row.find_element(By.CSS_SELECTOR, "td:nth-child(1)").text
        print(row_date)
        row_amount = row.find_element(By.CSS_SELECTOR, "td:nth-child(2)").text
        print(row_amount)
        row_description = row.find_element(By.CSS_SELECTOR, "td:nth-child(3)").text
        print(row_description)
        if expected_date in row_date and expected_amount in row_amount and row_description == description:
            found = True
            break
    assert found, f"Expense not found: {amount}, {description}, {expected_date}"


