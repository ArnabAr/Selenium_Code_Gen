package com.saucedemo.tests;

import com.saucedemo.pages.InventoryPage;
import com.saucedemo.pages.LoginPage;
import com.saucedemo.utils.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integration test for user story:
 * "As a user, I want to log into the application, navigate to the inventory page,
 * and add an item to my cart so that I can purchase it."
 *
 * Acceptance Criteria:
 * 1. Users can log in with valid credentials.
 * 2. Error message appears on an invalid login.
 * 3. Users can click "Add to Cart" on the inventory page,
 *    and the cart badge increments by 1.
 *
 * Target: https://www.saucedemo.com/
 */
@DisplayName("Login and Add to Cart - SauceDemo")
public class LoginAndAddToCartIT extends BaseTest {

    private LoginPage loginPage;
    private InventoryPage inventoryPage;

    private static final String VALID_USERNAME = "standard_user";
    private static final String VALID_PASSWORD = "secret_sauce";
    private static final String INVALID_USERNAME = "invalid_user";
    private static final String INVALID_PASSWORD = "wrong_password";

    @BeforeEach
    void initPages() {
        loginPage = new LoginPage(driver);
        inventoryPage = new InventoryPage(driver);
    }

    @Test
    @DisplayName("AC1: Users can log in with valid credentials")
    void testValidLogin() {
        loginPage.open();
        loginPage.loginAs(VALID_USERNAME, VALID_PASSWORD);

        inventoryPage.waitForPageLoad();
        assertTrue(inventoryPage.isDisplayed(),
                "Inventory page should be displayed after successful login");
        assertEquals("Products", inventoryPage.getPageTitle(),
                "Page title should be 'Products' after login");
    }

    @Test
    @DisplayName("AC2: Error message appears on an invalid login")
    void testInvalidLoginShowsError() {
        loginPage.open();
        loginPage.loginAs(INVALID_USERNAME, INVALID_PASSWORD);

        assertTrue(loginPage.isErrorMessageDisplayed(),
                "Error message should be displayed for invalid credentials");

        String errorText = loginPage.getErrorMessageText();
        assertTrue(errorText.contains("Username and password do not match"),
                "Error message should indicate credential mismatch, but was: " + errorText);
    }

    @Test
    @DisplayName("AC3: Add to Cart increments cart badge by 1")
    void testAddToCartIncrementsBadge() {
        loginPage.open();
        loginPage.loginAs(VALID_USERNAME, VALID_PASSWORD);
        inventoryPage.waitForPageLoad();

        int initialCount = inventoryPage.getCartBadgeCount();
        inventoryPage.addFirstItemToCart();

        assertTrue(inventoryPage.isCartBadgeDisplayed(),
                "Cart badge should be visible after adding an item");
        assertEquals(initialCount + 1, inventoryPage.getCartBadgeCount(),
                "Cart badge count should increment by 1 after adding an item");
    }
}
