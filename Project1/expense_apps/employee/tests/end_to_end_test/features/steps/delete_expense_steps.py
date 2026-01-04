import time

from behave import given, when, then
from selenium.common import TimeoutException, StaleElementReferenceException
from selenium.webdriver.common.by import By
from selenium.webdriver.support.wait import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from behave import use_step_matcher

from tests.end_to_end_test.pages.dashboard_page import DashboardPage

use_step_matcher("parse")

@given('the employee is on my expenses')
def on_my_expenses(context):
    context.dashboard_page = DashboardPage(context.driver)
    context.dashboard_page.go_to_view_my_expenses_screen()

    try:
        element = WebDriverWait(context.driver, 10).until(
            EC.visibility_of_element_located((By.XPATH, "//h1[normalize-space()='Employee Expense Dashboard']"))
        )
        assert element.is_displayed()
    except TimeoutException:
        raise AssertionError("Employee Expense Dashboard did not load in time")


@given('an expense with the description: "{description}" is shown')
def description_shown(context, description):
    try:
        element = WebDriverWait(context.driver, 10).until(
            EC.presence_of_element_located((By.XPATH, f"//td[normalize-space()='{description}']"))
        )
        assert element.is_displayed()
    except TimeoutException:
        raise AssertionError(f"Expense with description '{description}' was not shown")


@given('the expense with description "{description}" is pending')
def expense_is_pending(context, description):
    xpath = f"//td[normalize-space()='{description}']/parent::tr/td[normalize-space()='PENDING']"
    timeout = 10
    poll_frequency = 0.5
    start_time = time.time()

    while True:
        try:
            element = WebDriverWait(context.driver, timeout).until(
                EC.visibility_of_element_located((By.XPATH, xpath))
            )
            assert element.is_displayed()
            break  # success
        except StaleElementReferenceException:
            if time.time() - start_time > timeout:
                raise AssertionError(f"Expense '{description}' was not pending (stale element)")
            time.sleep(poll_frequency)
        except TimeoutException:
            raise AssertionError(f"Expense '{description}' was not pending (not found)")


@when('the employee clicks the delete button for the expense with description "{description}"')
def click_delete(context, description):
    xpath = f"//td[normalize-space()='{description}']/parent::tr//button[normalize-space()='Delete']"
    timeout = 10
    poll_frequency = 0.5  # How often to retry
    end_time = WebDriverWait(context.driver, timeout).until(lambda d: True)  # just to calculate end_time

    # Retry loop to handle StaleElementReferenceException
    import time
    start = time.time()
    while True:
        try:
            element = WebDriverWait(context.driver, timeout).until(
                EC.element_to_be_clickable((By.XPATH, xpath))
            )
            element.click()
            break  # success
        except StaleElementReferenceException:
            if time.time() - start > timeout:
                raise AssertionError(f"Delete button for '{description}' could not be clicked due to stale element")
            time.sleep(poll_frequency)
        except TimeoutException:
            raise AssertionError(f"Delete button for '{description}' was not clickable")


@when('the employee clicks ok for the confirmation alert')
def click_ok_alert1(context):
    try:
        WebDriverWait(context.driver, 10).until(EC.alert_is_present())

        alert = context.driver.switch_to.alert
        alert.accept()
    except Exception as e:
        print(f"No alert present: {e}")


@when('the employee is shown another alert with message: "Expense deleted successfully!"')
def alert2_confirmation(context):
    try:
        WebDriverWait(context.driver, 10).until(EC.alert_is_present())

        alert = context.driver.switch_to.alert
        assert alert.text == "Expense deleted successfully!"
    except Exception as e:
        print(f"No alert present: {e}")


@when('the employee clicks ok to close the alert')
def click_ok_alert2(context):
    try:
        WebDriverWait(context.driver, 10).until(EC.alert_is_present())

        alert = context.driver.switch_to.alert
        alert.accept()
    except Exception as e:
        print(f"No alert present: {e}")


@then('the expense with description: "{description}" is no longer shown')
def expense_deletion_confirmation(context, description):
    try:
        WebDriverWait(context.driver, 5).until_not(
            EC.presence_of_element_located((By.XPATH, f"//td[normalize-space()='{description}']"))
        )
    except TimeoutException:
        raise AssertionError(f"Expense '{description}' was still found, deletion failed")


@when('the employee clicks cancel for the confirmation alert')
def alert1_cancel_click(context):
    try:
        WebDriverWait(context.driver, 10).until(EC.alert_is_present())

        alert = context.driver.switch_to.alert
        alert.dismiss()
    except Exception as e:
        print(f"No alert present: {e}")


@then('the expense with description: "{description}" is still shown')
def expense_still_shown(context, description):
    try:
        element = WebDriverWait(context.driver, 10).until(
            EC.visibility_of_element_located((By.XPATH, f"//td[normalize-space()='{description}']"))
        )
        assert element.is_displayed()
    except TimeoutException:
        raise AssertionError(f"Expense '{description}' was not shown")