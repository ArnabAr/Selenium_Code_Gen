package com.saucedemo.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Page Object for the SauceDemo inventory (products) page.
 * Handles product listing interactions and cart badge verification.
 */
public class InventoryPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private static final By INVENTORY_LIST = By.className("inventory_list");
    private static final By ADD_TO_CART_BUTTONS = By.cssSelector("button[data-test^='add-to-cart']");
    private static final By CART_BADGE = By.className("shopping_cart_badge");
    private static final By CART_LINK = By.className("shopping_cart_link");
    private static final By PAGE_TITLE = By.className("title");

    public InventoryPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    /**
     * Wait until the inventory page is fully loaded.
     *
     * @return this InventoryPage instance for fluent chaining
     */
    public InventoryPage waitForPageLoad() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(INVENTORY_LIST));
        return this;
    }

    /**
     * Check whether the inventory page is displayed.
     *
     * @return true if the inventory list is visible
     */
    public boolean isDisplayed() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(INVENTORY_LIST)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
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
     * Click the "Add to Cart" button for the first available product.
     *
     * @return this InventoryPage instance for fluent chaining
     */
    public InventoryPage addFirstItemToCart() {
        WebElement addButton = wait.until(
                ExpectedConditions.elementToBeClickable(ADD_TO_CART_BUTTONS));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", addButton);
        return this;
    }

    /**
     * Get the current cart badge count.
     *
     * @return the number displayed on the cart badge, or 0 if no badge is present
     */
    public int getCartBadgeCount() {
        try {
            WebElement badge = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(CART_BADGE));
            return Integer.parseInt(badge.getText());
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Check whether the cart badge is displayed.
     *
     * @return true if the cart badge is visible
     */
    public boolean isCartBadgeDisplayed() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(CART_BADGE));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Click the cart icon to navigate to the cart page.
     */
    public void clickCartLink() {
        wait.until(ExpectedConditions.elementToBeClickable(CART_LINK)).click();
    }
}
