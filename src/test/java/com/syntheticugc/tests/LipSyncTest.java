package com.syntheticugc.tests;

import com.syntheticugc.base.BaseTest;
import com.syntheticugc.pages.LipSyncPage;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class LipSyncTest extends BaseTest {
    private LipSyncPage lipSyncPage;

    @BeforeClass
    public void setup() {
        // Login is handled in BaseTest constructor
        lipSyncPage = new LipSyncPage(driver);
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