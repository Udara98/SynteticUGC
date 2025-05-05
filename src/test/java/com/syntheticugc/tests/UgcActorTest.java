package com.syntheticugc.tests;

import com.syntheticugc.base.BaseTest;
import com.syntheticugc.pages.UgcActorPage;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class UgcActorTest extends BaseTest {
    private UgcActorPage ugcActorPage;

    @BeforeClass
    public void setup() {
        // Login is handled in BaseTest constructor
        ugcActorPage = new UgcActorPage(driver);
    }

    @Test
    public void testGenerateUgcActor() throws InterruptedException {
        ugcActorPage.navigateToUgcActorPage();
        Thread.sleep(2000);

        String script = "a shot of a nurse, the nurse is speaking causally in front of the camera";
        ugcActorPage.generateActorWithScript(script);
        
        // Verify loading state is displayed
        Assert.assertTrue(ugcActorPage.isLoadingStateDisplayed(), "Loading state should be displayed after clicking generate");
        
        // Wait for video generation to complete
        ugcActorPage.waitForVideoGeneration();
        
        // Verify video output is displayed
        Assert.assertTrue(ugcActorPage.isVideoOutputDisplayed(), "Video output should be displayed after generation");
        
        System.out.println("UGC Actor generation completed successfully");
    }
} 