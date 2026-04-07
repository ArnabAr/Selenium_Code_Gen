package com.saucedemo.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Page Object for the SauceDemo Checkout: Overview page.
 * Handles scraping item total and finishing the order.
 */
public class CheckoutOverviewPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private static final By PAGE_TITLE = By.className("title");
    private static final By SUBTOTAL_LABEL = By.className("summary_subtotal_label");
    private static final By FINISH_BUTTON = By.id("finish");

    public CheckoutOverviewPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    /**
     * Wait until the checkout overview page is fully loaded.
     *
     * @return this CheckoutOverviewPage instance for fluent chaining
     */
    public CheckoutOverviewPage waitForPageLoad() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(SUBTOTAL_LABEL));
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
     * Get the full "Item total" text (e.g., "Item total: $45.98").
     *
     * @return the item total string as displayed on the page
     */
    public String getItemTotalText() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(SUBTOTAL_LABEL)).getText();
    }

    /**
     * Parse and return the item total as a double value.
     *
     * @return the item total amount
     */
    public double getItemTotalAmount() {
        String text = getItemTotalText();
        String amount = text.replaceAll("[^0-9.]", "");
        return Double.parseDouble(amount);
    }

    /**
     * Click the Finish button to complete the order.
     */
    public void clickFinish() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(FINISH_BUTTON));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
    }
}
