package com.syntheticugc.base;

import com.syntheticugc.pages.LoginPage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import java.time.Duration;

public class BaseTest {
    protected static WebDriver driver;
    protected static WebDriverWait wait;
    protected static LoginPage loginPage;

    @BeforeSuite
    public void setUp() throws InterruptedException {
        try {
            System.out.println("Initializing WebDriver...");
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--start-maximized");
            options.addArguments("--disable-notifications");
            options.addArguments("--disable-popup-blocking");
            options.addArguments("--disable-infobars");
            options.addArguments("--disable-extensions");
            options.addArguments("--disable-gpu");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--remote-allow-origins=*");
            
            driver = new ChromeDriver(options);
            wait = new WebDriverWait(driver, Duration.ofSeconds(30));
            
            loginPage = new LoginPage(driver);
            
            // Navigate to login page
            System.out.println("Navigating to login page...");
            loginPage.navigateToLoginPage();
            
            // Perform login
            System.out.println("Attempting login...");
            loginPage.login("udaraudawatte@gmail.com", "XNkn*8Xg#u*L");
            
            // Wait for successful login
            wait.until(ExpectedConditions.urlContains("syntheticugc.com"));
            System.out.println("Login successful!");
            
            // Additional wait to ensure everything is loaded
            Thread.sleep(3000);
            
        } catch (Exception e) {
            System.err.println("Error during setup: " + e.getMessage());
            e.printStackTrace();
            if (driver != null) {
                driver.quit();
                driver = null;
            }
            throw e;
        }
    }

    @AfterSuite
    public void tearDown() {
        try {
            if (driver != null) {
                System.out.println("Closing WebDriver...");
                driver.quit();
                driver = null;
                loginPage = null;
                wait = null;
            }
        } catch (Exception e) {
            System.err.println("Error during teardown: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 