from selenium.webdriver.common.by import By

from tests.end_to_end_test.pages.base_page import BasePage


class LoginPage(BasePage):
    USERNAME_FIELD = (By.ID, "username")
    PASSWORD_FIELD = (By.ID, "password")
    LOGIN_BUTTON = (By.CSS_SELECTOR, "button[type='submit']")
    LOGIN_MESSAGE = (By.CSS_SELECTOR, "#login-message p")

    def open(self):
        super().open()

    def enter_username(self, username):
        self.type(self.USERNAME_FIELD, username)

    def enter_password(self, password):
        self.type(self.PASSWORD_FIELD, password)

    def click_login_button(self):
        button = self.wait_for_clickable(self.LOGIN_BUTTON)

        button.click()

        from tests.end_to_end_test.pages.dashboard_page import DashboardPage
        return DashboardPage(self.driver)

    def clear_username(self, username):
        pass

    def login(self, username, password):
        self.enter_username(username)
        self.enter_password(password)
        self.click_login_button()
        from tests.end_to_end_test.pages.dashboard_page import DashboardPage

        return DashboardPage(self.driver)

