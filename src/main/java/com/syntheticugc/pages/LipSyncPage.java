package com.syntheticugc.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.io.File;

public class LipSyncPage {
    private final WebDriver driver;
    private final WebDriverWait wait;
    private final JavascriptExecutor jsExecutor;

    // Locators
    private final By lipSyncCard = By.xpath("//li[contains(@class, 'flex-1')]//span[contains(text(), 'Lip Sync')]");
    private final By audioUploadInput = By.id("audio-upload");
    private final By videoUploadInput = By.id("video-upload");
    private final By startLipSyncButton = By.xpath("//button[contains(@class, 'bg-black') and contains(@class, 'px-8') and contains(@class, 'min-w-[180px]')]");
    private final By uploadProgressIndicator = By.xpath("//div[contains(@class, 'upload-progress') or contains(@class, 'progress-bar')]");
    private final By uploadCompleteIndicator = By.xpath("//div[contains(@class, 'upload-complete') or contains(@class, 'success')]");

    public LipSyncPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        this.jsExecutor = (JavascriptExecutor) driver;
    }

    public void clickLipSyncCard() {
        WebElement card = wait.until(ExpectedConditions.presenceOfElementLocated(lipSyncCard));
        jsExecutor.executeScript("arguments[0].scrollIntoView({block: 'center'});", card);
        card.click();
    }

    private void waitForUploadToComplete() {
        try {
            // Wait for progress indicator to appear
            wait.until(ExpectedConditions.presenceOfElementLocated(uploadProgressIndicator));
            System.out.println("Upload progress started...");

            // Wait for progress indicator to disappear or complete indicator to appear
            wait.until(ExpectedConditions.or(
                ExpectedConditions.invisibilityOfElementLocated(uploadProgressIndicator),
                ExpectedConditions.presenceOfElementLocated(uploadCompleteIndicator)
            ));
            
            System.out.println("Upload completed");
            Thread.sleep(2000); // Additional wait to ensure UI updates
        } catch (Exception e) {
            System.out.println("Warning: Could not detect upload completion: " + e.getMessage());
        }
    }

    public void uploadAudioFile(String filePath) {
        File audioFile = new File(filePath);
        if (!audioFile.exists()) {
            throw new RuntimeException("Audio file not found at: " + filePath);
        }
        
        System.out.println("Uploading audio file: " + filePath);
        WebElement audioInput = wait.until(ExpectedConditions.presenceOfElementLocated(audioUploadInput));
        audioInput.sendKeys(audioFile.getAbsolutePath());
        waitForUploadToComplete();
    }

    public void uploadVideoFile(String filePath) {
        File videoFile = new File(filePath);
        if (!videoFile.exists()) {
            throw new RuntimeException("Video file not found at: " + filePath);
        }
        
        System.out.println("Uploading video file: " + filePath);
        WebElement videoInput = wait.until(ExpectedConditions.presenceOfElementLocated(videoUploadInput));
        videoInput.sendKeys(videoFile.getAbsolutePath());
        waitForUploadToComplete();
    }

    public void clickStartLipSync() {
        try {
            // Wait for the button to be present first
            WebElement button = wait.until(ExpectedConditions.presenceOfElementLocated(startLipSyncButton));
            System.out.println("Button found, scrolling into view...");
            
            // Scroll the button into view
            jsExecutor.executeScript("arguments[0].scrollIntoView({block: 'center'});", button);
            Thread.sleep(2000); // Give more time for scrolling
            
            // Wait for the button to be clickable
            button = wait.until(ExpectedConditions.elementToBeClickable(startLipSyncButton));
            System.out.println("Button is clickable, attempting to click...");
            
            // Try JavaScript click if regular click fails
            try {
                button.click();
            } catch (ElementClickInterceptedException e) {
                System.out.println("Regular click failed, trying JavaScript click...");
                jsExecutor.executeScript("arguments[0].click();", button);
            }
            
            System.out.println("Button click successful");
        } catch (Exception e) {
            System.out.println("Error details: " + e.getMessage());
            throw new RuntimeException("Failed to click Start Lip Sync button: " + e.getMessage(), e);
        }
    }

    public void performLipSync(String audioFilePath, String videoFilePath) {
        try {
            clickLipSyncCard();
            uploadAudioFile(audioFilePath);
            uploadVideoFile(videoFilePath);
            clickStartLipSync();
        } catch (Exception e) {
            throw new RuntimeException("Failed to perform lip sync: " + e.getMessage(), e);
        }
    }
} 