import unittest
from appium import webdriver
from appium.webdriver.common.appiumby import AppiumBy
from appium.options.android import UiAutomator2Options
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC

capabilities = dict(
    platformName='Android',
    automationName='uiautomator2',
    deviceName='emulator-5554', # change to your actual emulator/physical device name (you can list your available devices via running "appium devices")
    appPackage='com.example.cs458',
    appActivity='.MainActivity',
    language='en',
    locale='US'
)
appium_server_url = 'http://localhost:4723'
capabilities_options = UiAutomator2Options().load_capabilities(capabilities)

class MainActivityTests(unittest.TestCase):
    def setUp(self):
        self.driver = webdriver.Remote(command_executor=appium_server_url, options=capabilities_options)

    def test_successful_submit(self):
        name_field = self.driver.find_element(AppiumBy.ID, "com.example.cs458:id/nameEditText")
        name_field.send_keys("John")

        surname_field = self.driver.find_element(AppiumBy.ID, "com.example.cs458:id/surnameEditText")
        surname_field.send_keys("Doe")

        birth_date_field = self.driver.find_element(AppiumBy.ID, "com.example.cs458:id/birthDateEditText")
        birth_date_field.send_keys("21/05/1990")

        city_field = self.driver.find_element(AppiumBy.ID, "com.example.cs458:id/cityEditText")
        city_field.send_keys("New York")

        gender_radio_button = self.driver.find_element(AppiumBy.ID, "com.example.cs458:id/genderRadioGroup")

        male_radio_button = gender_radio_button.find_elements(AppiumBy.CLASS_NAME, "android.widget.RadioButton")[0]
        male_radio_button.click()

        ai_model_chatgpt_checkbox = self.driver.find_element(AppiumBy.ID, "com.example.cs458:id/checkBoxChatGPT")
        ai_model_chatgpt_checkbox.click()

        wait = WebDriverWait(self.driver, 10)
        chatgpt_defect_field = wait.until(EC.presence_of_element_located((AppiumBy.ID, "com.example.cs458:id/editTextChatGPT")))
        chatgpt_defect_field.send_keys("Limited knowledge base")

        ai_use_case_field = self.driver.find_element(AppiumBy.ID, "com.example.cs458:id/aiUseCaseEditText")
        ai_use_case_field.send_keys("Providing assistance in daily tasks")

        submit_button = self.driver.find_element(AppiumBy.ID, "com.example.cs458:id/submitSurveyButton")
        submit_button.click()

        toast_locator = '//android.widget.Toast'
        wait = WebDriverWait(self.driver, 10)
        success_toast = wait.until(EC.presence_of_element_located((AppiumBy.XPATH, toast_locator)))
        self.assertEqual(success_toast.get_attribute("text"), "Survey Submitted!")

    def test_invalid_city_name(self):

        name_field = self.driver.find_element(AppiumBy.ID, "com.example.cs458:id/nameEditText")
        name_field.send_keys("John")

        surname_field = self.driver.find_element(AppiumBy.ID, "com.example.cs458:id/surnameEditText")
        surname_field.send_keys("Doe")

        birth_date_field = self.driver.find_element(AppiumBy.ID, "com.example.cs458:id/birthDateEditText")
        birth_date_field.send_keys("21/05/1990")

        city_field = self.driver.find_element(AppiumBy.ID, "com.example.cs458:id/cityEditText")
        city_field.send_keys("A")

        gender_radio_button = self.driver.find_element(AppiumBy.ID, "com.example.cs458:id/genderRadioGroup")
        male_radio_button = gender_radio_button.find_elements(AppiumBy.CLASS_NAME, "android.widget.RadioButton")[0]
        male_radio_button.click()

        ai_model_chatgpt_checkbox = self.driver.find_element(AppiumBy.ID, "com.example.cs458:id/checkBoxChatGPT")
        ai_model_chatgpt_checkbox.click()

        wait = WebDriverWait(self.driver, 10)
        chatgpt_defect_field = wait.until(EC.presence_of_element_located((AppiumBy.ID, "com.example.cs458:id/editTextChatGPT")))

        chatgpt_defect_field.send_keys("Limited knowledge base")

        ai_use_case_field = self.driver.find_element(AppiumBy.ID, "com.example.cs458:id/aiUseCaseEditText")
        ai_use_case_field.send_keys("Providing assistance in daily tasks")

        submit_button = self.driver.find_element(AppiumBy.ID, "com.example.cs458:id/submitSurveyButton")
        submit_button.click()

        toast_locator = '//android.widget.Toast'
        wait = WebDriverWait(self.driver, 10)
        success_toast = wait.until(EC.presence_of_element_located((AppiumBy.XPATH, toast_locator)))
        self.assertEqual(success_toast.get_attribute("text"), "City name should be at least 2 characters long")

    def test_submit_button_visibility_with_empty_ai_use_case_field(self):
        # Fill in the required fields except ai_use_case_field
        name_field = self.driver.find_element(AppiumBy.ID, "com.example.cs458:id/nameEditText")
        name_field.send_keys("John")

        surname_field = self.driver.find_element(AppiumBy.ID, "com.example.cs458:id/surnameEditText")
        surname_field.send_keys("Doe")

        birth_date_field = self.driver.find_element(AppiumBy.ID, "com.example.cs458:id/birthDateEditText")
        birth_date_field.send_keys("21/05/1990")

        city_field = self.driver.find_element(AppiumBy.ID, "com.example.cs458:id/cityEditText")
        city_field.send_keys("New York")

        gender_radio_button = self.driver.find_element(AppiumBy.ID, "com.example.cs458:id/genderRadioGroup")
        male_radio_button = gender_radio_button.find_elements(AppiumBy.CLASS_NAME, "android.widget.RadioButton")[0]
        male_radio_button.click()

        ai_model_chatgpt_checkbox = self.driver.find_element(AppiumBy.ID, "com.example.cs458:id/checkBoxChatGPT")
        ai_model_chatgpt_checkbox.click()

        # Leave ai_use_case_field empty
        # ai_use_case_field = self.driver.find_element(AppiumBy.ID, "com.example.cs458:id/aiUseCaseEditText")
        # ai_use_case_field.send_keys("")

        # Check for the visibility of the submit button
        try:
            submit_button = self.driver.find_element(AppiumBy.ID, "com.example.cs458:id/submitSurveyButton")
            is_visible = submit_button.is_displayed()
            self.assertFalse(is_visible, "Submit button should not be visible")
        except NoSuchElementException:
            # If the element is not found, which means the test is true
            pass

    def test_invalid_age(self):
        name_field = self.driver.find_element(AppiumBy.ID, "com.example.cs458:id/nameEditText")
        name_field.send_keys("John")

        surname_field = self.driver.find_element(AppiumBy.ID, "com.example.cs458:id/surnameEditText")
        surname_field.send_keys("Doe")

        birth_date_field = self.driver.find_element(AppiumBy.ID, "com.example.cs458:id/birthDateEditText")
        birth_date_field.send_keys("21/05/2020")

        city_field = self.driver.find_element(AppiumBy.ID, "com.example.cs458:id/cityEditText")
        city_field.send_keys("New York")

        gender_radio_button = self.driver.find_element(AppiumBy.ID, "com.example.cs458:id/genderRadioGroup")
        male_radio_button = gender_radio_button.find_elements(AppiumBy.CLASS_NAME, "android.widget.RadioButton")[0]
        male_radio_button.click()

        ai_model_chatgpt_checkbox = self.driver.find_element(AppiumBy.ID, "com.example.cs458:id/checkBoxChatGPT")
        ai_model_chatgpt_checkbox.click()

        wait = WebDriverWait(self.driver, 10)
        chatgpt_defect_field = wait.until(EC.presence_of_element_located((AppiumBy.ID, "com.example.cs458:id/editTextChatGPT")))

        chatgpt_defect_field.send_keys("Limited knowledge base")
        ai_use_case_field = self.driver.find_element(AppiumBy.ID, "com.example.cs458:id/aiUseCaseEditText")
        ai_use_case_field.send_keys("Providing assistance in daily tasks")

        submit_button = self.driver.find_element(AppiumBy.ID, "com.example.cs458:id/submitSurveyButton")
        submit_button.click()

        toast_locator = '//android.widget.Toast'
        wait = WebDriverWait(self.driver, 10)
        success_toast = wait.until(EC.presence_of_element_located((AppiumBy.XPATH, toast_locator)))
        self.assertEqual(success_toast.get_attribute("text"), "Age must be between 13 and 80 years old")


    def tearDown(self):
        if self.driver:
            self.driver.quit()


if __name__ == '__main__':
    unittest.main()