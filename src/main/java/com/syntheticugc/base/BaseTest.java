package com.syntheticugc.base;

import com.syntheticugc.pages.LoginPage;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

public class BaseTest {
    protected static WebDriver driver;
    protected static LoginPage loginPage;

    @BeforeSuite
    public void setUp() throws InterruptedException {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();

        // Login once at the start of the test suite
        loginPage = new LoginPage(driver);
        loginPage.navigateToLoginPage();
        Thread.sleep(2000);
        loginPage.login("udaraudawatte@gmail.com", "XNkn*8Xg#u*L");
        Thread.sleep(2000);
    }

    @AfterSuite
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
} 