package com.saucedemo.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Page Object for the SauceDemo Checkout: Your Information page.
 * Handles entering first name, last name, and postal code.
 */
public class CheckoutInfoPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private static final By FIRST_NAME = By.id("first-name");
    private static final By LAST_NAME = By.id("last-name");
    private static final By POSTAL_CODE = By.id("postal-code");
    private static final By CONTINUE_BUTTON = By.id("continue");
    private static final By PAGE_TITLE = By.className("title");

    public CheckoutInfoPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    /**
     * Wait until the checkout info page is fully loaded.
     *
     * @return this CheckoutInfoPage instance for fluent chaining
     */
    public CheckoutInfoPage waitForPageLoad() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(FIRST_NAME));
        return this;
    }

    /**
     * Get the page title text.
     *
     * @return the title text displayed on the page
     */
    public String getPageTitle() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(PAGE_TITLE)).getText();
    }

    /**
     * Fill in the checkout information fields.
     *
     * @param firstName the first name
     * @param lastName  the last name
     * @param postalCode the postal/zip code
     * @return this CheckoutInfoPage instance for fluent chaining
     */
    public CheckoutInfoPage fillInfo(String firstName, String lastName, String postalCode) {
        setReactInputValue(FIRST_NAME, firstName);
        setReactInputValue(LAST_NAME, lastName);
        setReactInputValue(POSTAL_CODE, postalCode);
        return this;
    }

    /**
     * Set a React controlled input's value using the native input value setter
     * to ensure React's internal state is updated.
     */
    private void setReactInputValue(By locator, String value) {
        WebElement field = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        ((JavascriptExecutor) driver).executeScript(
                "var nativeInputValueSetter = Object.getOwnPropertyDescriptor("
                        + "window.HTMLInputElement.prototype, 'value').set;"
                        + "nativeInputValueSetter.call(arguments[0], arguments[1]);"
                        + "arguments[0].dispatchEvent(new Event('input', { bubbles: true }));",
                field, value);
    }

    /**
     * Click the Continue button to proceed to the checkout overview.
     */
    public void clickContinue() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(CONTINUE_BUTTON));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
    }
}
