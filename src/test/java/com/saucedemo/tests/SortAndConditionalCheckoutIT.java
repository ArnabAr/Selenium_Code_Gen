package com.saucedemo.tests;

import com.saucedemo.pages.CartPage;
import com.saucedemo.pages.CheckoutCompletePage;
import com.saucedemo.pages.CheckoutInfoPage;
import com.saucedemo.pages.CheckoutOverviewPage;
import com.saucedemo.pages.InventoryPage;
import com.saucedemo.pages.LoginPage;
import com.saucedemo.utils.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integration test for user story:
 * Sort products by price (high to low), conditionally add items based on sum threshold,
 * proceed through checkout, verify item total, and complete the order.
 *
 * Target: https://www.saucedemo.com/
 */
@DisplayName("Sort and Conditional Checkout - SauceDemo")
public class SortAndConditionalCheckoutIT extends BaseTest {

    private LoginPage loginPage;
    private InventoryPage inventoryPage;
    private CartPage cartPage;
    private CheckoutInfoPage checkoutInfoPage;
    private CheckoutOverviewPage checkoutOverviewPage;
    private CheckoutCompletePage checkoutCompletePage;

    private static final String USERNAME = "standard_user";
    private static final String PASSWORD = "secret_sauce";
    private static final double SUM_THRESHOLD = 80.00;

    @BeforeEach
    void initPages() {
        loginPage = new LoginPage(driver);
        inventoryPage = new InventoryPage(driver);
        cartPage = new CartPage(driver);
        checkoutInfoPage = new CheckoutInfoPage(driver);
        checkoutOverviewPage = new CheckoutOverviewPage(driver);
        checkoutCompletePage = new CheckoutCompletePage(driver);
    }

    @Test
    @DisplayName("Sort high-to-low, conditionally add items, checkout, verify total, and finish")
    void testSortConditionalAddCheckoutAndFinish() {
        // Step 1: Navigate to the SauceDemo login page
        loginPage.open();

        // Step 2: Log in using standard_user and secret_sauce
        loginPage.loginAs(USERNAME, PASSWORD);
        inventoryPage.waitForPageLoad();
        assertTrue(inventoryPage.isDisplayed(), "Inventory page should load after login");

        // Step 3: Select "Price (high to low)" from the sort dropdown
        inventoryPage.sortBy("hilo");

        // Step 4: Capture prices of first two items and calculate their sum
        List<Double> prices = inventoryPage.getAllItemPrices();
        assertTrue(prices.size() >= 2, "At least 2 items should be listed on inventory page");
        double firstPrice = prices.get(0);
        double secondPrice = prices.get(1);
        double sum = firstPrice + secondPrice;

        // Verify sorting: first item should be >= second item (high to low)
        assertTrue(firstPrice >= secondPrice,
                "After sorting high-to-low, first price ($" + firstPrice
                        + ") should be >= second price ($" + secondPrice + ")");

        // Step 5: Conditionally add items based on sum threshold
        double expectedTotal;
        int expectedCartCount;
        if (sum < SUM_THRESHOLD) {
            // Add both items: after adding index 0, its button changes to "Remove"
            // so the second item's add-to-cart button becomes the new index 0
            inventoryPage.addItemToCartByIndex(0);
            inventoryPage.addItemToCartByIndex(0);
            expectedTotal = sum;
            expectedCartCount = 2;
        } else {
            // Only add the first item
            inventoryPage.addItemToCartByIndex(0);
            expectedTotal = firstPrice;
            expectedCartCount = 1;
        }

        // Verify cart badge reflects the correct count
        assertEquals(expectedCartCount, inventoryPage.getCartBadgeCount(),
                "Cart badge should show " + expectedCartCount + " item(s)");

        // Step 6: Navigate to Cart and then Checkout: Information page
        inventoryPage.clickCartLink();
        cartPage.waitForPageLoad();
        assertEquals("Your Cart", cartPage.getPageTitle(), "Should be on Cart page");
        assertEquals(expectedCartCount, cartPage.getCartItemCount(),
                "Cart should contain " + expectedCartCount + " item(s)");

        cartPage.clickCheckout();

        // Step 7: Enter "John", "Doe", and "12345" into checkout fields
        checkoutInfoPage.waitForPageLoad();
        assertEquals("Checkout: Your Information", checkoutInfoPage.getPageTitle(),
                "Should be on Checkout: Your Information page");
        checkoutInfoPage.fillInfo("John", "Doe", "12345");
        checkoutInfoPage.clickContinue();

        // Step 8: Scrape the "Item total" string on Checkout: Overview
        checkoutOverviewPage.waitForPageLoad();
        assertEquals("Checkout: Overview", checkoutOverviewPage.getPageTitle(),
                "Should be on Checkout: Overview page");
        String itemTotalText = checkoutOverviewPage.getItemTotalText();
        double scrapedTotal = checkoutOverviewPage.getItemTotalAmount();

        // Step 9: Assert scraped total matches the mathematical sum
        assertEquals(expectedTotal, scrapedTotal, 0.01,
                "Item total ($" + scrapedTotal + ") should match expected sum ($"
                        + expectedTotal + "). Item total text: " + itemTotalText);

        // Step 10: Click Finish and verify the thank you header
        checkoutOverviewPage.clickFinish();
        checkoutCompletePage.waitForPageLoad();
        assertTrue(checkoutCompletePage.isCompleteHeaderDisplayed(),
                "Thank you header should be displayed after completing the order");

        String headerText = checkoutCompletePage.getCompleteHeaderText();
        assertTrue(headerText.toUpperCase().contains("THANK YOU FOR YOUR ORDER"),
                "Header should contain 'THANK YOU FOR YOUR ORDER' but was: " + headerText);
    }
}
