package com.syntheticugc.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class LoginPage {
    private final WebDriver driver;
    private final WebDriverWait wait;
    private final JavascriptExecutor jsExecutor;

    // Updated locators with correct CSS selectors
    private final By emailInput = By.cssSelector("input[placeholder='you@example.com']");
    private final By passwordInput = By.cssSelector("input[type='password']");
    private final By loginButton = By.cssSelector("button[type='submit']");
    private final By loadingIndicator = By.cssSelector("[class*='loading'], [class*='spinner']");
    private final By errorMessage = By.cssSelector("[class*='error'], [class*='alert']");

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        this.jsExecutor = (JavascriptExecutor) driver;
    }

    public void navigateToLoginPage() {
        try {
            System.out.println("Navigating to auth page...");
            driver.get("https://www.syntheticugc.com/auth");
            
            // Wait for page to be fully loaded
            wait.until(webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));
            System.out.println("Page is fully loaded");
            
            // Add a small delay to ensure the page is fully rendered
            Thread.sleep(2000);
            
            // Wait for email input to be visible
            wait.until(ExpectedConditions.visibilityOfElementLocated(emailInput));
            System.out.println("Email input field is ready");
            
        } catch (Exception e) {
            System.out.println("Error navigating to login page: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to navigate to login page: " + e.getMessage(), e);
        }
    }

    public void login(String email, String password) {
        try {
            System.out.println("Starting login process...");
            
            // Find and fill email input
            WebElement emailField = wait.until(ExpectedConditions.visibilityOfElementLocated(emailInput));
            emailField.clear();
            Thread.sleep(500);
            emailField.sendKeys(email);
            System.out.println("Email entered: " + email);
            
            // Find and fill password input
            WebElement passwordField = wait.until(ExpectedConditions.visibilityOfElementLocated(passwordInput));
            passwordField.clear();
            Thread.sleep(500);
            passwordField.sendKeys(password);
            System.out.println("Password entered");
            
            // Find and click login button
            WebElement loginBtn = wait.until(ExpectedConditions.elementToBeClickable(loginButton));
            
            // Scroll button into view and click
            jsExecutor.executeScript("arguments[0].scrollIntoView({block: 'center'});", loginBtn);
            Thread.sleep(1000);
            jsExecutor.executeScript("arguments[0].click();", loginBtn);
            System.out.println("Login button clicked");
            
            // Wait for loading indicator to appear and disappear
            try {
                wait.until(ExpectedConditions.presenceOfElementLocated(loadingIndicator));
                System.out.println("Loading indicator appeared");
                wait.until(ExpectedConditions.invisibilityOfElementLocated(loadingIndicator));
                System.out.println("Loading indicator disappeared");
            } catch (TimeoutException e) {
                System.out.println("No loading indicator found or it disappeared quickly");
            }
            
            // Check for error messages
            try {
                WebElement error = driver.findElement(errorMessage);
                if (error.isDisplayed()) {
                    throw new RuntimeException("Login failed: " + error.getText());
                }
            } catch (NoSuchElementException e) {
                System.out.println("No error message found");
            }
            
            // Wait for URL to change to dashboard
            wait.until(ExpectedConditions.urlContains("syntheticugc.com"));
            System.out.println("Successfully navigated to dashboard");
            
            // Additional wait to ensure everything is loaded
            Thread.sleep(3000);
            
        } catch (Exception e) {
            System.out.println("Error during login: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to login: " + e.getMessage(), e);
        }
    }
} 