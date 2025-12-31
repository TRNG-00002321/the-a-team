from selenium.webdriver.common.by import By
from selenium.webdriver.support.wait import WebDriverWait

from base_page import BasePage
from login_page import LoginPage


class DashboardPage(BasePage):
    def __init__(self, driver):
        self.driver = driver
        self.wait = WebDriverWait(driver, 10)

        self.SUBMIT_NEW_EXPENSES_BUTTON = (By.ID, "show-submit")
        self.VIEW_MY_EXPENSES_BUTTON = (By.ID, "show-expenses")
        self.LOGOUT_BUTTON = (By.ID, "logout-btn")

    def go_to_submit_new_expense_screen(self):
        self.click(self.SUBMIT_NEW_EXPENSES_BUTTON)

    def go_to_view_my_expenses_screen(self):
        self.click(self.VIEW_MY_EXPENSES_BUTTON)

    def logout(self):
        self.click(self.LOGOUT_BUTTON)
        return LoginPage(self.driver)