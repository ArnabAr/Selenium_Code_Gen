package com.saucedemo.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Page Object for the SauceDemo login page.
 * Encapsulates all login-related interactions and locators.
 */
public class LoginPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private static final By USERNAME_INPUT = By.id("user-name");
    private static final By PASSWORD_INPUT = By.id("password");
    private static final By LOGIN_BUTTON = By.id("login-button");
    private static final By ERROR_MESSAGE = By.cssSelector("[data-test='error']");

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    /**
     * Navigate to the SauceDemo login page.
     *
     * @return this LoginPage instance for fluent chaining
     */
    public LoginPage open() {
        driver.get("https://www.saucedemo.com/");
        wait.until(ExpectedConditions.visibilityOfElementLocated(USERNAME_INPUT));
        return this;
    }

    /**
     * Enter the username into the username field.
     *
     * @param username the username to enter
     * @return this LoginPage instance for fluent chaining
     */
    public LoginPage enterUsername(String username) {
        WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(USERNAME_INPUT));
        usernameField.clear();
        usernameField.sendKeys(username);
        return this;
    }

    /**
     * Enter the password into the password field.
     *
     * @param password the password to enter
     * @return this LoginPage instance for fluent chaining
     */
    public LoginPage enterPassword(String password) {
        WebElement passwordField = wait.until(ExpectedConditions.visibilityOfElementLocated(PASSWORD_INPUT));
        passwordField.clear();
        passwordField.sendKeys(password);
        return this;
    }

    /**
     * Click the login button.
     */
    public void clickLogin() {
        wait.until(ExpectedConditions.elementToBeClickable(LOGIN_BUTTON)).click();
    }

    /**
     * Perform a complete login with the given credentials.
     *
     * @param username the username
     * @param password the password
     */
    public void loginAs(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLogin();
    }

    /**
     * Check whether the error message container is displayed.
     *
     * @return true if the error message is visible
     */
    public boolean isErrorMessageDisplayed() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(ERROR_MESSAGE)).isDisplayed();
    }

    /**
     * Retrieve the text content of the error message.
     *
     * @return the error message text
     */
    public String getErrorMessageText() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(ERROR_MESSAGE)).getText();
    }
}
