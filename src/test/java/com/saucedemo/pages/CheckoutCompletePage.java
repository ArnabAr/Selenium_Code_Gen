package com.saucedemo.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Page Object for the SauceDemo Checkout: Complete page.
 * Handles verification of order completion.
 */
public class CheckoutCompletePage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private static final By PAGE_TITLE = By.className("title");
    private static final By COMPLETE_HEADER = By.className("complete-header");

    public CheckoutCompletePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    /**
     * Wait until the checkout complete page is fully loaded.
     *
     * @return this CheckoutCompletePage instance for fluent chaining
     */
    public CheckoutCompletePage waitForPageLoad() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(COMPLETE_HEADER));
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
     * Get the completion header text (e.g., "Thank you for your order!").
     *
     * @return the header text
     */
    public String getCompleteHeaderText() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(COMPLETE_HEADER)).getText();
    }

    /**
     * Check whether the completion header is displayed.
     *
     * @return true if the header is visible
     */
    public boolean isCompleteHeaderDisplayed() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(COMPLETE_HEADER)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}
