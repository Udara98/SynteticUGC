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
    private final By videoOutputDiv = By.xpath("//div[contains(@class, 'justify-center') and contains(@class, 'items-center') and contains(@class, 'flex')]//div[contains(@class, 'relative') and (contains(@class, 'aspect-[16/9]') or contains(@class, 'aspect-[9/16]'))]");
    private final By videoElement = By.xpath("//video[contains(@class, 'w-full') and contains(@class, 'h-full') and contains(@class, 'object-contain')]");
    private final By creationPlaceholder = By.xpath("//p[contains(text(), 'Your creation will appear here')]");

    public LipSyncPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        this.jsExecutor = (JavascriptExecutor) driver;
    }

    public void clickLipSyncCard() {
        try {
            // Wait for the page to be fully loaded
            wait.until(webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));
            System.out.println("Page is fully loaded");
            
            // Try multiple locators for the Lip Sync card
            By[] possibleLocators = {
                lipSyncCard,
                By.xpath("//span[contains(text(), 'Lip Sync')]"),
                By.xpath("//*[contains(text(), 'Lip Sync')]"),
                By.xpath("//li[.//span[contains(text(), 'Lip Sync')]]"),
                By.xpath("//div[contains(@class, 'card')]//span[contains(text(), 'Lip Sync')]")
            };
            
            WebElement card = null;
            for (By locator : possibleLocators) {
                try {
                    System.out.println("Trying locator: " + locator);
                    card = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
                    if (card != null) {
                        System.out.println("Found element with locator: " + locator);
                        break;
                    }
                } catch (Exception e) {
                    System.out.println("Locator " + locator + " failed: " + e.getMessage());
                }
            }
            
            if (card == null) {
                throw new RuntimeException("Could not find Lip Sync card with any of the locators");
            }
            
            // Wait for the element to be visible
            wait.until(ExpectedConditions.visibilityOf(card));
            
            // Scroll the element into view with offset
            jsExecutor.executeScript("arguments[0].scrollIntoView({block: 'center', behavior: 'smooth'});", card);
            
            // Add a small delay to allow smooth scrolling to complete
            Thread.sleep(2000);
            
            // Wait for the element to be clickable
            card = wait.until(ExpectedConditions.elementToBeClickable(card));
            
            // Try multiple click methods
            try {
                // Try JavaScript click first
                jsExecutor.executeScript("arguments[0].click();", card);
                System.out.println("Clicked using JavaScript");
            } catch (Exception e) {
                try {
                    // Try regular click
                    card.click();
                    System.out.println("Clicked using regular click");
                } catch (Exception e2) {
                    // Try Actions click
                    new org.openqa.selenium.interactions.Actions(driver)
                        .moveToElement(card)
                        .click()
                        .perform();
                    System.out.println("Clicked using Actions");
                }
            }
            
            // Wait a bit for the click to take effect
            Thread.sleep(2000);
            
            // Verify the click worked by checking if we're on the lip sync page
            try {
                wait.until(ExpectedConditions.presenceOfElementLocated(audioUploadInput));
                System.out.println("Successfully navigated to lip sync page");
            } catch (Exception e) {
                throw new RuntimeException("Click appeared to succeed but page did not change: " + e.getMessage());
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to click Lip Sync card: " + e.getMessage(), e);
        }
    }

    private void waitForUploadToComplete() {
        try {
            System.out.println("Waiting for upload to start...");
            
            // Wait for either progress indicator or complete indicator with increased timeout
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(10));
            try {
                shortWait.until(ExpectedConditions.or(
                    ExpectedConditions.presenceOfElementLocated(uploadProgressIndicator),
                    ExpectedConditions.presenceOfElementLocated(uploadCompleteIndicator)
                ));
                System.out.println("Upload indicator found");
            } catch (TimeoutException e) {
                System.out.println("No upload indicator found within 10 seconds, checking if upload completed immediately");
                // Check if upload completed immediately
                try {
                    WebElement completeIndicator = driver.findElement(uploadCompleteIndicator);
                    if (completeIndicator.isDisplayed()) {
                        System.out.println("Upload completed immediately");
                        return;
                    }
                } catch (NoSuchElementException ne) {
                    System.out.println("No immediate completion indicator found");
                }
            }

            // If we see progress indicator, wait for it to complete with increased timeout
            try {
                WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(60));
                longWait.until(ExpectedConditions.or(
                    ExpectedConditions.invisibilityOfElementLocated(uploadProgressIndicator),
                    ExpectedConditions.presenceOfElementLocated(uploadCompleteIndicator)
                ));
                System.out.println("Upload completed");
            } catch (TimeoutException e) {
                System.out.println("Upload progress indicator still visible after 60 seconds");
                throw new RuntimeException("Upload timeout: Progress indicator still visible after 60 seconds");
            }
            
            // Additional wait to ensure UI updates
            Thread.sleep(3000);
            
            // Final verification
            try {
                WebElement completeIndicator = driver.findElement(uploadCompleteIndicator);
                if (completeIndicator.isDisplayed()) {
                    System.out.println("Final verification: Upload complete indicator is visible");
                } else {
                    System.out.println("Warning: Upload complete indicator not visible in final verification");
                }
            } catch (NoSuchElementException e) {
                System.out.println("Warning: Could not find upload complete indicator in final verification");
            }
        } catch (Exception e) {
            System.out.println("Error during upload completion check: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to verify upload completion: " + e.getMessage(), e);
        }
    }

    public void uploadAudioFile(String filePath) {
        File audioFile = new File(filePath);
        if (!audioFile.exists()) {
            throw new RuntimeException("Audio file not found at: " + filePath);
        }
        
        System.out.println("\n=== Audio Upload Process ===");
        System.out.println("Uploading audio file: " + filePath);
        System.out.println("File size: " + audioFile.length() + " bytes");
        System.out.println("File exists: " + audioFile.exists());
        System.out.println("File can read: " + audioFile.canRead());
        
        try {
            // Wait for the page to be fully loaded
            wait.until(webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));
            System.out.println("Page is fully loaded");
            
            // Check for iframes
            int iframeCount = driver.findElements(By.tagName("iframe")).size();
            System.out.println("Number of iframes found: " + iframeCount);
            
            // Try to find the element in the main document first
            try {
                WebElement audioInput = wait.until(ExpectedConditions.presenceOfElementLocated(audioUploadInput));
                System.out.println("Found audio upload input element in main document");
                
                // Scroll the element into view
                jsExecutor.executeScript("arguments[0].scrollIntoView({block: 'center', behavior: 'smooth'});", audioInput);
                Thread.sleep(1000); // Wait for scroll to complete
                
                // Print element state
                System.out.println("Element enabled: " + audioInput.isEnabled());
                System.out.println("Element displayed: " + audioInput.isDisplayed());
                System.out.println("Element tag name: " + audioInput.getTagName());
                System.out.println("Element attributes: " + audioInput.getAttribute("outerHTML"));
                
                // Try to make the element interactable using JavaScript
                jsExecutor.executeScript("arguments[0].style.opacity = '1'; arguments[0].style.visibility = 'visible'; arguments[0].style.display = 'block';", audioInput);
                
                // Wait for the element to be clickable with increased timeout
                WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(60));
                audioInput = longWait.until(ExpectedConditions.elementToBeClickable(audioUploadInput));
                System.out.println("Audio upload input is clickable");
                
                // Clear any existing value
                try {
                    audioInput.clear();
                } catch (ElementNotInteractableException e) {
                    System.out.println("Could not clear input, trying JavaScript clear");
                    jsExecutor.executeScript("arguments[0].value = '';", audioInput);
                }
                
                // Send the file path
                try {
                    audioInput.sendKeys(audioFile.getAbsolutePath());
                    System.out.println("Successfully sent file path to input element");
                } catch (ElementNotInteractableException e) {
                    System.out.println("Could not send keys directly, trying JavaScript");
                    jsExecutor.executeScript("arguments[0].value = arguments[1];", audioInput, audioFile.getAbsolutePath());
                    System.out.println("Successfully set file path using JavaScript");
                }
                
                // Wait for upload to complete with increased timeout
                waitForUploadToComplete();
                
                // Verify upload success
                try {
                    WebElement successIndicator = driver.findElement(uploadCompleteIndicator);
                    if (successIndicator.isDisplayed()) {
                        System.out.println("Audio upload completed successfully");
                    } else {
                        System.out.println("Warning: Upload complete indicator not visible");
                    }
                } catch (NoSuchElementException e) {
                    System.out.println("Warning: Could not find upload complete indicator");
                }
            } catch (TimeoutException e) {
                System.out.println("Element not found in main document, checking iframes...");
                
                // If not found in main document, check each iframe
                for (int i = 0; i < iframeCount; i++) {
                    try {
                        driver.switchTo().frame(i);
                        System.out.println("Switched to iframe " + i);
                        
                        WebElement audioInput = wait.until(ExpectedConditions.presenceOfElementLocated(audioUploadInput));
                        System.out.println("Found audio upload input element in iframe " + i);
                        
                        // Perform the same operations as above
                        jsExecutor.executeScript("arguments[0].scrollIntoView({block: 'center', behavior: 'smooth'});", audioInput);
                        Thread.sleep(1000);
                        
                        audioInput = wait.until(ExpectedConditions.elementToBeClickable(audioUploadInput));
                        audioInput.sendKeys(audioFile.getAbsolutePath());
                        
                        waitForUploadToComplete();
                        
                        // Switch back to main document
                        driver.switchTo().defaultContent();
                        return;
                    } catch (Exception iframeEx) {
                        System.out.println("Element not found in iframe " + i + ": " + iframeEx.getMessage());
                        driver.switchTo().defaultContent();
                    }
                }
                
                throw new RuntimeException("Audio upload element not found in main document or any iframe");
            }
        } catch (Exception e) {
            System.out.println("Error during audio upload: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to upload audio file: " + e.getMessage(), e);
        }
    }

    public void uploadVideoFile(String filePath) {
        File videoFile = new File(filePath);
        if (!videoFile.exists()) {
            throw new RuntimeException("Video file not found at: " + filePath);
        }
        
        System.out.println("\n=== Video Upload Process ===");
        System.out.println("Uploading video file: " + filePath);
        System.out.println("File size: " + videoFile.length() + " bytes");
        System.out.println("File exists: " + videoFile.exists());
        System.out.println("File can read: " + videoFile.canRead());
        
        try {
            // Wait for the page to be fully loaded
            wait.until(webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));
            System.out.println("Page is fully loaded");
            
            // Try to find the element in the main document first
            try {
                WebElement videoInput = wait.until(ExpectedConditions.presenceOfElementLocated(videoUploadInput));
                System.out.println("Found video upload input element in main document");
                
                // Scroll the element into view
                jsExecutor.executeScript("arguments[0].scrollIntoView({block: 'center', behavior: 'smooth'});", videoInput);
                Thread.sleep(1000); // Wait for scroll to complete
                
                // Print element state
                System.out.println("Element enabled: " + videoInput.isEnabled());
                System.out.println("Element displayed: " + videoInput.isDisplayed());
                System.out.println("Element tag name: " + videoInput.getTagName());
                System.out.println("Element attributes: " + videoInput.getAttribute("outerHTML"));
                
                // Try to make the element interactable using JavaScript
                jsExecutor.executeScript("arguments[0].style.opacity = '1'; arguments[0].style.visibility = 'visible'; arguments[0].style.display = 'block';", videoInput);
                
                // Wait for the element to be clickable with increased timeout
                WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(60));
                videoInput = longWait.until(ExpectedConditions.elementToBeClickable(videoUploadInput));
                System.out.println("Video upload input is clickable");
                
                // Clear any existing value
                try {
                    videoInput.clear();
                } catch (ElementNotInteractableException e) {
                    System.out.println("Could not clear input, trying JavaScript clear");
                    jsExecutor.executeScript("arguments[0].value = '';", videoInput);
                }
                
                // Send the file path
                try {
                    videoInput.sendKeys(videoFile.getAbsolutePath());
                    System.out.println("Successfully sent file path to input element");
                } catch (ElementNotInteractableException e) {
                    System.out.println("Could not send keys directly, trying JavaScript");
                    jsExecutor.executeScript("arguments[0].value = arguments[1];", videoInput, videoFile.getAbsolutePath());
                    System.out.println("Successfully set file path using JavaScript");
                }
                
                // Wait for upload to complete with increased timeout
                waitForUploadToComplete();
                
                // Verify upload success
                try {
                    WebElement successIndicator = driver.findElement(uploadCompleteIndicator);
                    if (successIndicator.isDisplayed()) {
                        System.out.println("Video upload completed successfully");
                    } else {
                        System.out.println("Warning: Upload complete indicator not visible");
                    }
                } catch (NoSuchElementException e) {
                    System.out.println("Warning: Could not find upload complete indicator");
                }
            } catch (TimeoutException e) {
                System.out.println("Element not found in main document, checking iframes...");
                
                // Check for iframes
                int iframeCount = driver.findElements(By.tagName("iframe")).size();
                System.out.println("Number of iframes found: " + iframeCount);
                
                // If not found in main document, check each iframe
                for (int i = 0; i < iframeCount; i++) {
                    try {
                        driver.switchTo().frame(i);
                        System.out.println("Switched to iframe " + i);
                        
                        WebElement videoInput = wait.until(ExpectedConditions.presenceOfElementLocated(videoUploadInput));
                        System.out.println("Found video upload input element in iframe " + i);
                        
                        // Perform the same operations as above
                        jsExecutor.executeScript("arguments[0].scrollIntoView({block: 'center', behavior: 'smooth'});", videoInput);
                        Thread.sleep(1000);
                        
                        videoInput = wait.until(ExpectedConditions.elementToBeClickable(videoUploadInput));
                        videoInput.sendKeys(videoFile.getAbsolutePath());
                        
                        waitForUploadToComplete();
                        
                        // Switch back to main document
                        driver.switchTo().defaultContent();
                        return;
                    } catch (Exception iframeEx) {
                        System.out.println("Element not found in iframe " + i + ": " + iframeEx.getMessage());
                        driver.switchTo().defaultContent();
                    }
                }
                
                throw new RuntimeException("Video upload element not found in main document or any iframe");
            }
        } catch (Exception e) {
            System.out.println("Error during video upload: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to upload video file: " + e.getMessage(), e);
        }
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
            
            // Additional check for video controls
            String controls = video.getAttribute("controls");
            System.out.println("Video controls: " + controls);
            
            // Check if the video source is a valid URL
            boolean hasValidSource = videoSrc.startsWith("http") && videoSrc.endsWith(".mp4");
            System.out.println("Has valid video source: " + hasValidSource);
            
            return isVisible && controls != null && hasValidSource;
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