from selenium.webdriver.common.by import By
from selenium.webdriver.support.wait import WebDriverWait

from base_page import BasePage
from end_to_end_test.pages.dashboard_page import DashboardPage


class LoginPage(BasePage):

    def __init__(self, driver):
        self.driver = driver
        self.wait = WebDriverWait(self.driver, 10)

        self.USERNAME_FIELD = (By.ID, "username")
        self.PASSWORD_FIELD = (By.ID, "password")
        self.LOGIN_BUTTON = (By.CSS_SELECTOR, "button[type='submit']")

    def enter_username(self, username):
        self.type(self.USERNAME_FIELD, username)

    def enter_password(self, password):
        self.type(self.PASSWORD_FIELD, password)

    def click_login_button(self):
        self.click(self.LOGIN_BUTTON)
        return DashboardPage(self.driver)

    def login(self, username, password):
        self.enter_username(username)
        self.enter_password(password)
        self.click_login_button()
        return DashboardPage(self.driver)

