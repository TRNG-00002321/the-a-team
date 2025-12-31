from abc import ABC
from selenium import webdriver
from selenium.common import NoSuchElementException
from selenium.webdriver.common.by import By
from selenium.webdriver.support.wait import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC

# locators are tuples containing a 'By' object and a string
class BasePage(ABC):
    def __init__(self, driver):
        self.driver = driver
        self.wait = WebDriverWait(self.driver, 10)

    def wait_for_element(self, locator):
        return self.wait.until(EC.presence_of_element_located(locator))

    def wait_for_clickable(self, locator):
        return self.wait.until(EC.element_to_be_clickable(locator))

    def click(self, locator):
        self.wait_for_clickable(locator).click()

    def type(self, locator, text):
        element = self.wait_for_element(locator)
        element.clear()
        element.send_keys(text)

    def get_text(self, locator):
        return self.wait_for_element(locator).text

    def is_displayed(self, locator):
        try:
            element = self.driver.find_element(locator[0], locator[1])
            return element.is_displayed()
        except NoSuchElementException:
            return False

    def get_page_title(self):
        return self.driver.title

    def get_current_url(self):
        return self.driver.current_url