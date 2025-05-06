package com.syntheticugc.base;

import com.syntheticugc.pages.LoginPage;
import com.syntheticugc.utils.EmailUtil;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BaseTest {
    protected static WebDriver driver;
    protected static LoginPage loginPage;

    @BeforeSuite
    public void setUp() throws InterruptedException {
        WebDriverManager.chromedriver().setup();
        
        // Configure Chrome options
        ChromeOptions options = new ChromeOptions();
        
        // Check if running in CI environment (GitHub Actions)
        boolean isCI = System.getenv("CI") != null && System.getenv("CI").equals("true");
        
        if (isCI) {
            // CI-specific options
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--headless");
            options.addArguments("--disable-gpu");
            options.addArguments("--window-size=1920,1080");
            options.addArguments("--remote-allow-origins=*");
        } else {
            // Local environment options
            options.addArguments("--start-maximized");
            options.addArguments("--remote-allow-origins=*");
        }
        
        // Create Chrome driver with options
        driver = new ChromeDriver(options);
        
        if (!isCI) {
            driver.manage().window().maximize();
        }

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

        // Send test report via email
        try {
            String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm").format(new Date());
            String subject = "SyntheticUGC Test Report - " + timestamp;
            String body = "Please find attached the test execution report for SyntheticUGC tests.\n\n" +
                         "This is an automated email sent after test execution.";

            // Get all XML report files from the surefire-reports directory
            File reportsDir = new File("target/surefire-reports");
            File[] reportFiles = reportsDir.listFiles((dir, name) -> name.endsWith(".xml"));

            if (reportFiles != null && reportFiles.length > 0) {
                EmailUtil.sendTestReport(subject, body, reportFiles);
            } else {
                System.err.println("No test report files found in target/surefire-reports directory");
            }
        } catch (Exception e) {
            System.err.println("Failed to send test report: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 