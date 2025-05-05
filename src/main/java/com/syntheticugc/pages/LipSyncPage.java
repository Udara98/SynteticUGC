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
    private final By uploadProgressIndicator = By.xpath("//div[contains(@class, 'progress') or contains(@class, 'uploading')]");
    private final By uploadCompleteIndicator = By.xpath("//div[contains(@class, 'success') or contains(@class, 'complete')]");
    private final By videoOutputDiv = By.xpath("//div[contains(@class, 'relative') and contains(@class, 'aspect-[9/16]') and contains(@class, 'max-h-[600px]')]");
    private final By videoElement = By.tagName("video");
    private final By creationPlaceholder = By.xpath("//p[contains(text(), 'Your creation will appear here')]");

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
            System.out.println("Waiting for upload to start...");
            
            // Wait for either progress indicator or complete indicator
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(5));
            try {
                shortWait.until(ExpectedConditions.or(
                    ExpectedConditions.presenceOfElementLocated(uploadProgressIndicator),
                    ExpectedConditions.presenceOfElementLocated(uploadCompleteIndicator)
                ));
                System.out.println("Upload indicator found");
            } catch (TimeoutException e) {
                System.out.println("No upload indicator found, assuming upload completed immediately");
                return;
            }

            // If we see progress indicator, wait for it to complete
            try {
                wait.until(ExpectedConditions.or(
                    ExpectedConditions.invisibilityOfElementLocated(uploadProgressIndicator),
                    ExpectedConditions.presenceOfElementLocated(uploadCompleteIndicator)
                ));
                System.out.println("Upload completed");
            } catch (TimeoutException e) {
                System.out.println("Upload progress indicator not found, assuming upload completed");
            }
            
            // Additional wait to ensure UI updates
            Thread.sleep(2000);
        } catch (Exception e) {
            System.out.println("Warning: Could not detect upload completion: " + e.getMessage());
            // Don't throw exception, just log the warning
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
            
            // Wait a bit for the click to take effect
            Thread.sleep(2000);
            
            // Verify button was clicked by checking if it's disabled or has a loading state
            try {
                String buttonClass = button.getAttribute("class");
                String buttonDisabled = button.getAttribute("disabled");
                System.out.println("Button class: " + buttonClass);
                System.out.println("Button disabled: " + buttonDisabled);
                
                if (buttonClass.contains("loading") || "true".equals(buttonDisabled)) {
                    System.out.println("Button click verified - button is in loading state or disabled");
                } else {
                    System.out.println("Button click may not have been successful - button is not in loading state or disabled");
                }
            } catch (Exception e) {
                System.out.println("Warning: Could not verify button state: " + e.getMessage());
            }
            
            System.out.println("Button click completed");
        } catch (Exception e) {
            System.out.println("Error details: " + e.getMessage());
            throw new RuntimeException("Failed to click Start Lip Sync button: " + e.getMessage(), e);
        }
    }

    public boolean isVideoOutputDisplayed() {
        try {
            System.out.println("Checking for video output...");
            
            // First check if the placeholder is still present
            try {
                WebElement placeholder = driver.findElement(creationPlaceholder);
                if (placeholder.isDisplayed()) {
                    System.out.println("Creation placeholder is still visible - video not ready");
                    return false;
                }
            } catch (NoSuchElementException e) {
                System.out.println("Placeholder not found - video might be ready");
            }
            
            // Then check for the video output
            WebElement videoDiv = wait.until(ExpectedConditions.presenceOfElementLocated(videoOutputDiv));
            WebElement video = videoDiv.findElement(videoElement);
            
            // Check if video element has a source
            String videoSrc = video.getAttribute("src");
            System.out.println("Video source: " + videoSrc);
            
            if (videoSrc == null || videoSrc.isEmpty()) {
                System.out.println("Video element found but has no source");
                return false;
            }
            
            // Check if video is actually visible
            boolean isVisible = videoDiv.isDisplayed() && video.isDisplayed();
            System.out.println("Video visibility: " + isVisible);
            
            // Check video dimensions
            Dimension size = video.getSize();
            System.out.println("Video dimensions: " + size.getWidth() + "x" + size.getHeight());
            
            if (size.getWidth() == 0 || size.getHeight() == 0) {
                System.out.println("Video has zero dimensions");
                return false;
            }
            
            return isVisible;
        } catch (TimeoutException e) {
            System.out.println("Timeout waiting for video output: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.out.println("Error checking video output: " + e.getMessage());
            return false;
        }
    }

    public void waitForVideoGeneration() {
        try {
            System.out.println("Starting lip sync video generation process...");
            
            // Wait for video generation to complete
            System.out.println("Waiting for video generation to complete...");
            long startTime = System.currentTimeMillis();
            boolean videoGenerated = false;
            
            while (!videoGenerated && (System.currentTimeMillis() - startTime) < Duration.ofMinutes(45).toMillis()) {
                try {
                    // Check if video is present and has content
                    if (isVideoOutputDisplayed()) {
                        System.out.println("Video output detected!");
                        videoGenerated = true;
                        break;
                    }
                    // Wait a bit before checking again
                    try {
                        Thread.sleep(10000); // Check every 10 seconds
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Thread was interrupted while waiting for video generation", ie);
                    }
                    System.out.println("Still waiting for video generation...");
                } catch (StaleElementReferenceException e) {
                    System.out.println("Element became stale, continuing to check for video...");
                }
            }
            
            if (!videoGenerated) {
                throw new TimeoutException("Video generation did not complete within the expected time (45 minutes)");
            }
            
            // Additional verification after video is found
            if (!isVideoOutputDisplayed()) {
                throw new RuntimeException("Video output verification failed after generation");
            }
            
            System.out.println("Lip sync video generation completed successfully");
        } catch (Exception e) {
            System.out.println("Error during video generation: " + e.getMessage());
            throw e;
        }
    }

    public void performLipSync(String audioFilePath, String videoFilePath) {
        try {
            clickLipSyncCard();
            uploadAudioFile(audioFilePath);
            uploadVideoFile(videoFilePath);
            clickStartLipSync();
            waitForVideoGeneration();
        } catch (Exception e) {
            throw new RuntimeException("Failed to perform lip sync: " + e.getMessage(), e);
        }
    }
} 