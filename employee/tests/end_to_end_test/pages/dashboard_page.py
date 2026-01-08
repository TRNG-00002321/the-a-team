from selenium.webdriver.common.by import By

from tests.end_to_end_test.pages.base_page import BasePage

class DashboardPage(BasePage):
    SUBMIT_NEW_EXPENSES_BUTTON = (By.ID, "show-submit")
    VIEW_MY_EXPENSES_BUTTON = (By.ID, "show-expenses")
    LOGOUT_BUTTON = (By.ID, "logout-btn")
    TITLE = (By.CSS_SELECTOR, "h1")
    EXPENSE_HEADING = (By.XPATH, "//h3[text()='My Expenses']")
    TABLE = (By.CSS_SELECTOR, "table")

    def go_to_submit_new_expense_screen(self):
        self.click(self.SUBMIT_NEW_EXPENSES_BUTTON)

    def go_to_view_my_expenses_screen(self):
        self.click(self.VIEW_MY_EXPENSES_BUTTON)

    def click_logout_button(self):
        button = self.wait_for_clickable(self.LOGOUT_BUTTON)

        # Ensure element is actually interactable
        self.driver.execute_script(
            "arguments[0].scrollIntoView({block:'center'});", button
        )

        self.wait.until(lambda d: button.is_enabled())

        button.click()

        from tests.end_to_end_test.pages.login_page import LoginPage
        return LoginPage(self.driver)