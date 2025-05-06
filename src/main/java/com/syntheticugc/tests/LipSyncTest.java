package com.syntheticugc.tests;

import com.syntheticugc.base.BaseTest;
import com.syntheticugc.pages.LipSyncPage;
import org.testng.Assert;
import org.testng.annotations.*;

import java.io.File;

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
    public void testLipSyncFunctionality() throws InterruptedException {
        // Get the test resources directory
        String resourcesDir = new File("src/test/resources").getAbsolutePath();
        String audioFilePath = resourcesDir + File.separator + "demo-audio.mp3";
        String videoFilePath = resourcesDir + File.separator + "video.mp4";
        
        // Enhanced file verification
        System.out.println("Checking test files...");
        System.out.println("Resources directory: " + resourcesDir);
        
        File audioFile = new File(audioFilePath);
        File videoFile = new File(videoFilePath);
        
        // Detailed audio file checks
        System.out.println("\nAudio file details:");
        System.out.println("Path: " + audioFilePath);
        System.out.println("Exists: " + audioFile.exists());
        if (audioFile.exists()) {
            System.out.println("Size: " + audioFile.length() + " bytes");
            System.out.println("Can read: " + audioFile.canRead());
            System.out.println("Can write: " + audioFile.canWrite());
            System.out.println("Is file: " + audioFile.isFile());
            System.out.println("Is directory: " + audioFile.isDirectory());
        }
        
        // Detailed video file checks
        System.out.println("\nVideo file details:");
        System.out.println("Path: " + videoFilePath);
        System.out.println("Exists: " + videoFile.exists());
        if (videoFile.exists()) {
            System.out.println("Size: " + videoFile.length() + " bytes");
            System.out.println("Can read: " + videoFile.canRead());
            System.out.println("Can write: " + videoFile.canWrite());
            System.out.println("Is file: " + videoFile.isFile());
            System.out.println("Is directory: " + videoFile.isDirectory());
        }
        
        if (!audioFile.exists()) {
            throw new RuntimeException("Audio file not found at: " + audioFilePath + 
                "\nPlease place demo-audio.mp3 in src/test/resources directory");
        }
        
        if (!videoFile.exists()) {
            throw new RuntimeException("Video file not found at: " + videoFilePath + 
                "\nPlease place video.mp4 in src/test/resources directory");
        }
        
        try {
            // Navigate to the lip sync page
            System.out.println("\nNavigating to lip sync page...");
            lipSyncPage.clickLipSyncCard();
            
            // Wait for page load
            Thread.sleep(2000);
            
            // Print current URL for debugging
            System.out.println("Current URL: " + driver.getCurrentUrl());
            
            // Perform lip sync
            System.out.println("\nStarting lip sync process...");
            lipSyncPage.performLipSync(audioFilePath, videoFilePath);
            
            // Verify video output is displayed
            Assert.assertTrue(lipSyncPage.isVideoOutputDisplayed(), "Video output should be displayed after lip sync");
            
            System.out.println("Lip sync process completed successfully");
        } catch (Exception e) {
            System.out.println("Error during lip sync test: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
} 