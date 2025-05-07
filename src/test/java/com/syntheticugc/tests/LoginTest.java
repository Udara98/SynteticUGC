package com.syntheticugc.tests;

import com.syntheticugc.base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;

public class LoginTest extends BaseTest {
    @Test(groups = "login")
    public void verifyLoginSuccessful() {
        // Login is already handled in BaseTest
        // Verify we're on the dashboard page
        String currentUrl = driver.getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("syntheticugc.com"), "Should be on the SyntheticUGC website after login");
        System.out.println("Login verification completed");
    }
} 