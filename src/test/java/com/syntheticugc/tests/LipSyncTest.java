package com.syntheticugc.tests;

import com.syntheticugc.base.BaseTest;
import com.syntheticugc.pages.LipSyncPage;
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
    public void testLipSyncFunctionality() throws InterruptedException {
        String audioFilePath = "C:\\Users\\udara\\Documents\\Testing\\demo-audio.mp3";
        String videoFilePath = "C:\\Users\\udara\\Documents\\Testing\\video.mp4";
        
        lipSyncPage.performLipSync(audioFilePath, videoFilePath);
        Thread.sleep(2000); // Wait for the process to start
        
        System.out.println("Lip sync process started successfully");
    }
} 