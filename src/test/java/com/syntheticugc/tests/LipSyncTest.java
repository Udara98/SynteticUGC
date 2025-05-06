package com.syntheticugc.tests;

import com.syntheticugc.base.BaseTest;
import com.syntheticugc.pages.LipSyncPage;
import org.testng.Assert;
import org.testng.annotations.*;

public class LipSyncTest extends BaseTest {
    private LipSyncPage lipSyncPage;

    @BeforeClass
    public void setup() {
        // Login is handled in BaseTest constructor
        lipSyncPage = new LipSyncPage(driver);
    }

    @BeforeMethod
    public void beforeMethod() {
        // Clear browser cookies and cache before each test
        driver.manage().deleteAllCookies();
        // Refresh the page to ensure clean state
        driver.navigate().refresh();
        // Wait for page to load
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @AfterMethod
    public void afterMethod() {
        // Clear any remaining state after test
        driver.manage().deleteAllCookies();
    }

    @Test
    public void testLipSyncFunctionality() {
        String audioFilePath = "C:\\Users\\udara\\Documents\\Testing\\demo-audio.mp3";
        String videoFilePath = "C:\\Users\\udara\\Documents\\Testing\\video.mp4";
        
        // Perform lip sync
        lipSyncPage.performLipSync(audioFilePath, videoFilePath);
        
        // Verify video output is displayed
        Assert.assertTrue(lipSyncPage.isVideoOutputDisplayed(), "Video output should be displayed after lip sync");
        
        System.out.println("Lip sync process completed successfully");
    }
} 