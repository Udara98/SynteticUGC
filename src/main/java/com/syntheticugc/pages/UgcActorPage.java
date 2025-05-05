package com.syntheticugc.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class UgcActorPage {
    private final WebDriver driver;
    private final WebDriverWait wait;
    private final JavascriptExecutor jsExecutor;

    // Locators
    private final By generateVideoButton = By.xpath("//button[contains(@class, 'bg-black') and .//div[contains(text(), 'Generate Video')]]");
    private final By healthcareCard = By.xpath("//button[contains(@class, 'text-left') and .//p[contains(text(), 'Healthcare Professional')]]");
    private final By scriptInput = By.cssSelector("textarea[placeholder*='charismatic']");
    private final By sparklesIcon = By.cssSelector("svg.lucide-sparkles");
    private final By generateActorButton = By.cssSelector("button.px-8.min-w-\\[180px\\]");
    private final By loadingStateDiv = By.xpath("//div[contains(@class, 'absolute') and contains(@class, 'inset-0')]//p[contains(text(), 'AI Video Generation')]");
    private final By videoOutputDiv = By.xpath("//div[contains(@class, 'rounded-2xl') and .//video]");
    private final By videoElement = By.tagName("video");
    private final By downloadButton = By.xpath("//button[.//*[contains(@class, 'lucide-download')]]");
    private final By addVoiceButton = By.xpath("//button[.//*[contains(@class, 'lucide-mic')]]");

    public UgcActorPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        this.jsExecutor = (JavascriptExecutor) driver;
    }

    public void navigateToUgcActorPage() {
        driver.get("https://www.syntheticugc.com/ugc-actor");
        wait.until(ExpectedConditions.presenceOfElementLocated(generateVideoButton));
    }

    private void waitForPageLoad() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void clickWithRetry(WebElement element, By locator) {
        int maxAttempts = 3;
        int attempts = 0;
        while (attempts < maxAttempts) {
            try {
                jsExecutor.executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
                waitForPageLoad();
                element.click();
                return;
            } catch (ElementClickInterceptedException e) {
                attempts++;
                if (attempts == maxAttempts) {
                    // Try one last time with JavaScript click
                    jsExecutor.executeScript("arguments[0].click();", element);
                }
                waitForPageLoad();
            }
        }
    }

    public void clickGenerateVideoButton() {
        WebElement button = wait.until(ExpectedConditions.presenceOfElementLocated(generateVideoButton));
        jsExecutor.executeScript("arguments[0].scrollIntoView({block: 'center'});", button);
        waitForPageLoad();
        jsExecutor.executeScript("arguments[0].click();", button);
    }

    public void selectHealthcareProfessionalCard() {
        waitForPageLoad();
        WebElement card = wait.until(ExpectedConditions.presenceOfElementLocated(healthcareCard));
        clickWithRetry(card, healthcareCard);
    }

    public void enterScript(String script) {
        waitForPageLoad();
        WebElement textarea = wait.until(ExpectedConditions.visibilityOfElementLocated(scriptInput));
        jsExecutor.executeScript("arguments[0].scrollIntoView({block: 'center'});", textarea);
        textarea.clear();
        textarea.sendKeys(script);
    }

    public void clickGenerateActorButton() {
        waitForPageLoad();
        WebElement button = wait.until(ExpectedConditions.presenceOfElementLocated(generateActorButton));
        jsExecutor.executeScript("arguments[0].scrollIntoView({block: 'center'});", button);
        waitForPageLoad();
        jsExecutor.executeScript("arguments[0].click();", button);
    }

    public void generateActorWithScript(String script) {
        clickGenerateVideoButton();
        selectHealthcareProfessionalCard();
        enterScript(script);
        clickGenerateActorButton();
    }

    public boolean isLoadingStateDisplayed() {
        try {
            WebElement loadingDiv = wait.until(ExpectedConditions.presenceOfElementLocated(loadingStateDiv));
            return loadingDiv.isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }

    public boolean isVideoOutputDisplayed() {
        try {
            WebElement videoDiv = wait.until(ExpectedConditions.presenceOfElementLocated(videoOutputDiv));
            WebElement video = videoDiv.findElement(videoElement);
            return videoDiv.isDisplayed() && video.isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }

    public boolean areActionButtonsDisplayed() {
        try {
            System.out.println("Checking for action buttons...");
            WebElement downloadBtn = wait.until(ExpectedConditions.presenceOfElementLocated(downloadButton));
            System.out.println("Download button found");
            WebElement addVoiceBtn = wait.until(ExpectedConditions.presenceOfElementLocated(addVoiceButton));
            System.out.println("Add Voice button found");
            return downloadBtn.isDisplayed() && addVoiceBtn.isDisplayed();
        } catch (TimeoutException e) {
            System.out.println("Action buttons not found: " + e.getMessage());
            return false;
        }
    }

    public void waitForVideoGeneration() {
        try {
            System.out.println("Starting video generation process...");
            
            // Wait for loading state to appear with a longer timeout
            System.out.println("Waiting for loading state to appear...");
            WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(60));
            WebElement loadingDiv = longWait.until(ExpectedConditions.presenceOfElementLocated(loadingStateDiv));
            System.out.println("Loading state appeared");
            
            // Wait for video generation to complete
            System.out.println("Waiting for video generation to complete...");
            long startTime = System.currentTimeMillis();
            boolean videoGenerated = false;
            
            while (!videoGenerated && (System.currentTimeMillis() - startTime) < Duration.ofMinutes(15).toMillis()) {
                try {
                    // Check if loading state is gone
                    if (!loadingDiv.isDisplayed()) {
                        System.out.println("Loading state disappeared, checking for video...");
                        // Check if video is present
                        if (isVideoOutputDisplayed()) {
                            System.out.println("Video output detected!");
                            videoGenerated = true;
                            break;
                        }
                    }
                    // Wait a bit before checking again
                    try {
                        Thread.sleep(5000); // Check every 5 seconds
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Thread was interrupted while waiting for video generation", ie);
                    }
                    System.out.println("Still waiting for video generation...");
                } catch (StaleElementReferenceException e) {
                    // Element became stale, try to find it again
                    System.out.println("Element became stale, refreshing...");
                    try {
                        loadingDiv = longWait.until(ExpectedConditions.presenceOfElementLocated(loadingStateDiv));
                    } catch (TimeoutException te) {
                        // If we can't find the loading state anymore, check for video
                        System.out.println("Loading state not found, checking for video...");
                        if (isVideoOutputDisplayed()) {
                            System.out.println("Video output detected!");
                            videoGenerated = true;
                            break;
                        }
                    }
                }
            }
            
            if (!videoGenerated) {
                throw new TimeoutException("Video generation did not complete within the expected time");
            }
            
            System.out.println("Video generation completed successfully");
        } catch (Exception e) {
            System.out.println("Error during video generation: " + e.getMessage());
            throw e;
        }
    }
} 