package com.saucedemo.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Page Object for the SauceDemo cart page.
 * Handles cart item verification and checkout navigation.
 */
public class CartPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private static final By CART_LIST = By.className("cart_list");
    private static final By CART_ITEMS = By.className("cart_item");
    private static final By PAGE_TITLE = By.className("title");
    private static final By CHECKOUT_BUTTON = By.id("checkout");

    public CartPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    /**
     * Wait until the cart page is fully loaded.
     *
     * @return this CartPage instance for fluent chaining
     */
    public CartPage waitForPageLoad() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(CART_LIST));
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
     * Get the number of items in the cart.
     *
     * @return the count of cart items
     */
    public int getCartItemCount() {
        return driver.findElements(CART_ITEMS).size();
    }

    /**
     * Click the Checkout button to proceed to checkout.
     */
    public void clickCheckout() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(CHECKOUT_BUTTON));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
    }
}
