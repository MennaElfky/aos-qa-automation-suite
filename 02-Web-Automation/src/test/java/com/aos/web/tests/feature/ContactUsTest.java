package com.aos.web.tests.feature;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ContactUsTest {

    private WebDriver driver;
    private WebDriverWait wait;
    private JavascriptExecutor js;
    private String mainWindow;
    private int passedTests;
    private int failedTests;
    private int skippedTests;

    private final String baseUrl =
            "https://www.advantageonlineshopping.com/#/contact";

    private final By contactUsTitle =
            By.cssSelector("h1.contact_us");

    private final By categoryList =
            By.cssSelector("select[name='categoryListboxContactUs']");

    private final By productList =
            By.cssSelector("select[name='productListboxContactUs']");

    private final By emailInput =
            By.cssSelector("input[name='emailContactUs']");

    private final By subjectInput =
            By.cssSelector("textarea[name='subjectTextareaContactUs']");

    private final By sendButton =
            By.id("send_btn");

    private final By chatButton =
            By.cssSelector("div[name='chat_with_us']");

    private final By chatMessageInput =
            By.id("textMessage");

    private final By chatSendButton =
            By.id("btnSender");

    private final By chatBody =
            By.id("chatBody");

    private final By userChatMessages =
            By.cssSelector("#chatBody .message.user .ballon p");

    private final By assistantChatMessages =
            By.cssSelector("#chatBody .message:not(.user) .ballon p");

    @BeforeClass
    public void setUp() {
        System.setProperty(
                "webdriver.chrome.driver",
                "C:\\chromedriver-win64\\chromedriver.exe"
        );

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-popup-blocking");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(40));
        js = (JavascriptExecutor) driver;

        createReportsFolder();
    }

    @BeforeMethod
    public void openContactUsPage() throws InterruptedException {
        closeExtraWindows();

        driver.switchTo().window(driver.getWindowHandles().iterator().next());
        driver.navigate().to(baseUrl);

        waitForPageReady();
        waitForLoader();

        wait.until(
                ExpectedConditions.visibilityOfElementLocated(contactUsTitle)
        );

        mainWindow = driver.getWindowHandle();

        Thread.sleep(1000);
    }

    @AfterMethod
    public void recordTestResult(ITestResult result) {
        if (result.getStatus() == ITestResult.SUCCESS) {
            passedTests++;
        } else if (result.getStatus() == ITestResult.FAILURE) {
            failedTests++;
            writeBugReport(result);
        } else if (result.getStatus() == ITestResult.SKIP) {
            skippedTests++;
        }

        closeExtraWindows();

        if (driver != null && !driver.getWindowHandles().isEmpty()) {
            driver.switchTo().window(
                    driver.getWindowHandles().iterator().next()
            );
        }
    }

    @AfterClass
    public void tearDown() {
        writeSummaryReport();

        if (driver != null) {
            driver.quit();
        }
    }

    @Test(priority = 1, groups = "component")
    public void componentTest1ContactUsPageElementsAreDisplayed()
            throws InterruptedException {

        WebElement title = wait.until(
                ExpectedConditions.visibilityOfElementLocated(contactUsTitle)
        );

        WebElement category = wait.until(
                ExpectedConditions.visibilityOfElementLocated(categoryList)
        );

        WebElement product = wait.until(
                ExpectedConditions.visibilityOfElementLocated(productList)
        );

        WebElement email = wait.until(
                ExpectedConditions.visibilityOfElementLocated(emailInput)
        );

        WebElement subject = wait.until(
                ExpectedConditions.visibilityOfElementLocated(subjectInput)
        );

        WebElement send = wait.until(
                ExpectedConditions.visibilityOfElementLocated(sendButton)
        );

        WebElement chat = wait.until(
                ExpectedConditions.visibilityOfElementLocated(chatButton)
        );

        Thread.sleep(500);

        Assert.assertEquals(
                title.getText().trim(),
                "CONTACT US",
                "The Contact Us page title should be displayed."
        );

        Assert.assertTrue(
                category.isDisplayed(),
                "The Category list should be displayed."
        );

        Assert.assertTrue(
                product.isDisplayed(),
                "The Product list should be displayed."
        );

        Assert.assertTrue(
                email.isDisplayed(),
                "The Email field should be displayed."
        );

        Assert.assertTrue(
                subject.isDisplayed(),
                "The Subject field should be displayed."
        );

        Assert.assertTrue(
                send.isDisplayed(),
                "The Send button should be displayed."
        );

        Assert.assertTrue(
                chat.isDisplayed(),
                "The Chat With Us component should be displayed."
        );
    }

    @Test(priority = 2, groups = "component")
    public void componentTest2CategoryListContainsAvailableCategories()
            throws InterruptedException {

        Select categorySelect = new Select(
                wait.until(
                        ExpectedConditions.visibilityOfElementLocated(
                                categoryList
                        )
                )
        );

        List<String> categories = new ArrayList<>();

        for (WebElement option : categorySelect.getOptions()) {
            String optionText = option.getText().trim();

            if (!optionText.isEmpty()) {
                categories.add(optionText);
            }
        }

        Thread.sleep(500);

        Assert.assertTrue(
                categories.contains("Select Category"),
                "The default category option should be available."
        );

        Assert.assertTrue(
                categories.contains("Laptops"),
                "The Laptops category should be available."
        );

        Assert.assertTrue(
                categories.contains("Headphones"),
                "The Headphones category should be available."
        );

        Assert.assertTrue(
                categories.contains("Tablets"),
                "The Tablets category should be available."
        );

        Assert.assertTrue(
                categories.contains("Speakers"),
                "The Speakers category should be available."
        );

        Assert.assertTrue(
                categories.contains("Mice"),
                "The Mice category should be available."
        );
    }

    @Test(priority = 3, groups = "component")
    public void componentTest3SendButtonDisabledWhenRequiredFieldsAreEmpty()
            throws InterruptedException {

        WebElement email = wait.until(
                ExpectedConditions.visibilityOfElementLocated(emailInput)
        );

        WebElement subject = wait.until(
                ExpectedConditions.visibilityOfElementLocated(subjectInput)
        );

        WebElement send = wait.until(
                ExpectedConditions.visibilityOfElementLocated(sendButton)
        );

        email.clear();
        subject.clear();

        triggerInputEvent(email);
        triggerInputEvent(subject);

        Thread.sleep(500);

        Assert.assertFalse(
                send.isEnabled(),
                "The Send button should be disabled when required fields are empty."
        );

        Assert.assertTrue(
                send.getAttribute("class").contains("invalid"),
                "The Send button should have the invalid class."
        );
    }

    @Test(priority = 4, groups = "component")
    public void componentTest4EmailFieldAcceptsText()
            throws InterruptedException {

        String expectedEmail = "menna.test@example.com";

        WebElement email = wait.until(
                ExpectedConditions.elementToBeClickable(emailInput)
        );

        email.clear();
        email.sendKeys(expectedEmail);

        triggerInputEvent(email);

        Thread.sleep(500);

        Assert.assertEquals(
                email.getAttribute("value"),
                expectedEmail,
                "The Email field should contain the entered value."
        );
    }

    @Test(priority = 5, groups = "component")
    public void componentTest5SubjectFieldAcceptsMultilineText()
            throws InterruptedException {

        String firstLine = "Product support request";
        String secondLine = "The product requires technical assistance.";

        WebElement subject = wait.until(
                ExpectedConditions.elementToBeClickable(subjectInput)
        );

        subject.clear();
        subject.sendKeys(firstLine);
        subject.sendKeys(Keys.SHIFT, Keys.ENTER);
        subject.sendKeys(secondLine);

        triggerInputEvent(subject);

        Thread.sleep(500);

        String actualValue = subject.getAttribute("value");

        Assert.assertTrue(
                actualValue.contains(firstLine),
                "The Subject field should contain the first line."
        );

        Assert.assertTrue(
                actualValue.contains(secondLine),
                "The Subject field should contain the second line."
        );
    }

    @Test(priority = 6, groups = "component")
    public void componentTest6EmptyChatMessageIsNotSent()
            throws InterruptedException {

        openChatWindow();

        WebElement messageInput = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        chatMessageInput
                )
        );

        WebElement send = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        chatSendButton
                )
        );

        messageInput.clear();
        messageInput.sendKeys(" ");
        messageInput.sendKeys(Keys.BACK_SPACE);

        wait.until(currentDriver -> {
            String value = messageInput.getAttribute("value");
            String classes = send.getAttribute("class");

            return value != null
                    && value.isEmpty()
                    && classes != null
                    && classes.contains("disable");
        });

        int messagesBefore = driver.findElements(
                userChatMessages
        ).size();

        send.click();

        Thread.sleep(1000);

        int messagesAfter = driver.findElements(
                userChatMessages
        ).size();

        Assert.assertTrue(
                send.getAttribute("class").contains("disable"),
                "The Chat Send button should remain disabled for an empty message."
        );

        Assert.assertEquals(
                messagesAfter,
                messagesBefore,
                "An empty chat message should not be sent."
        );
    }

    @Test(priority = 7, groups = "integration")
    public void integrationTest1SelectingCategoryLoadsProducts()
            throws InterruptedException {

        Select categorySelect = new Select(
                wait.until(
                        ExpectedConditions.elementToBeClickable(categoryList)
                )
        );

        categorySelect.selectByVisibleText("Laptops");

        wait.until(currentDriver -> {
            Select productSelect = new Select(
                    currentDriver.findElement(productList)
            );

            List<WebElement> options = productSelect.getOptions();

            return options.size() > 1;
        });

        Thread.sleep(500);

        Select productSelect = new Select(
                driver.findElement(productList)
        );

        List<WebElement> productOptions = productSelect.getOptions();

        Assert.assertTrue(
                productOptions.size() > 1,
                "Selecting a category should load its products."
        );

        Assert.assertEquals(
                categorySelect.getFirstSelectedOption().getText().trim(),
                "Laptops",
                "The selected category should be Laptops."
        );

        Assert.assertTrue(
                productOptions.get(0).getText()
                        .trim()
                        .toLowerCase()
                        .contains("select"),
                "The Product list should contain a default option."
        );
    }

    @Test(priority = 8, groups = "integration")
    public void integrationTest2ChangingCategoryRefreshesProductList()
            throws InterruptedException {

        Select categorySelect = new Select(
                wait.until(
                        ExpectedConditions.elementToBeClickable(categoryList)
                )
        );

        categorySelect.selectByVisibleText("Laptops");

        wait.until(currentDriver ->
                new Select(currentDriver.findElement(productList))
                        .getOptions()
                        .size() > 1
        );

        List<String> laptopProducts = getProductOptions();

        categorySelect = new Select(
                driver.findElement(categoryList)
        );

        categorySelect.selectByVisibleText("Mice");

        wait.until(currentDriver -> {
            List<String> currentProducts = getProductOptions();

            return currentProducts.size() > 1
                    && !currentProducts.equals(laptopProducts);
        });

        Thread.sleep(500);

        List<String> miceProducts = getProductOptions();

        Assert.assertFalse(
                miceProducts.equals(laptopProducts),
                "Changing the category should refresh the Product list."
        );

        Assert.assertTrue(
                miceProducts.size() > 1,
                "The selected category should load available products."
        );
    }

    @Test(priority = 9, groups = "integration")
    public void integrationTest3ValidEmailAndSubjectEnableSendButton()
            throws InterruptedException {

        WebElement email = wait.until(
                ExpectedConditions.elementToBeClickable(emailInput)
        );

        WebElement subject = wait.until(
                ExpectedConditions.elementToBeClickable(subjectInput)
        );

        email.clear();
        email.sendKeys("menna.test@example.com");
        triggerInputEvent(email);

        subject.clear();
        subject.sendKeys("I need help with my product.");
        triggerInputEvent(subject);

        WebElement send = wait.until(currentDriver -> {
            WebElement button = currentDriver.findElement(sendButton);

            if (button.isEnabled()
                    && !button.getAttribute("class").contains("invalid")) {
                return button;
            }

            return null;
        });

        Thread.sleep(500);

        Assert.assertTrue(
                send.isEnabled(),
                "The Send button should be enabled after entering valid required data."
        );

        Assert.assertFalse(
                send.getAttribute("class").contains("invalid"),
                "The Send button should not have the invalid class."
        );
    }

    @Test(priority = 10, groups = "integration")
    public void integrationTest4EmptySubjectKeepsSendButtonDisabled()
            throws InterruptedException {

        WebElement email = wait.until(
                ExpectedConditions.elementToBeClickable(emailInput)
        );

        WebElement subject = wait.until(
                ExpectedConditions.elementToBeClickable(subjectInput)
        );

        WebElement send = wait.until(
                ExpectedConditions.visibilityOfElementLocated(sendButton)
        );

        email.clear();
        email.sendKeys("menna.test@example.com");
        triggerInputEvent(email);

        subject.clear();
        triggerInputEvent(subject);

        subject.click();
        email.click();

        Thread.sleep(1000);

        Assert.assertFalse(
                send.isEnabled(),
                "The Send button should remain disabled when Subject is empty."
        );

        Assert.assertTrue(
                send.getAttribute("class").contains("invalid"),
                "The Send button should remain invalid when Subject is empty."
        );
    }

    @Test(priority = 11, groups = "integration")
    public void integrationTest5InvalidEmailDisplaysValidationError()
            throws InterruptedException {

        WebElement email = wait.until(
                ExpectedConditions.elementToBeClickable(emailInput)
        );

        WebElement subject = wait.until(
                ExpectedConditions.elementToBeClickable(subjectInput)
        );

        WebElement send = wait.until(
                ExpectedConditions.visibilityOfElementLocated(sendButton)
        );

        email.clear();
        email.sendKeys("invalid-email");
        triggerInputEvent(email);

        subject.clear();
        subject.sendKeys("Valid subject for invalid email test.");
        triggerInputEvent(subject);

        subject.click();
        email.click();
        subject.click();

        Thread.sleep(1000);

        List<WebElement> emailErrors = driver.findElements(
                By.xpath(
                        "//input[@name='emailContactUs']" +
                                "/ancestor::sec-view[1]//ul//a"
                )
        );

        boolean visibleErrorFound = false;
        String errorText = "";

        for (WebElement error : emailErrors) {
            if (error.isDisplayed()
                    && !error.getText().trim().isEmpty()) {
                visibleErrorFound = true;
                errorText = error.getText().trim();
                break;
            }
        }

        Assert.assertTrue(
                visibleErrorFound,
                "A validation error should appear under the Email field for an invalid email."
        );

        Assert.assertFalse(
                send.isEnabled(),
                "The Send button should remain disabled for an invalid email."
        );

        Assert.assertFalse(
                errorText.isEmpty(),
                "The Email validation message should not be empty."
        );
    }

    @Test(priority = 12, groups = "integration")
    public void integrationTest6ChatWindowOpensAndSendsMessage()
            throws InterruptedException {

        openChatWindow();

        Assert.assertTrue(
                driver.getTitle().contains(
                        "Advantage Online Shopping Demo Support Chat"
                ),
                "The Support Chat window should open."
        );

        WebElement body = wait.until(
                ExpectedConditions.visibilityOfElementLocated(chatBody)
        );

        WebElement messageInput = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        chatMessageInput
                )
        );

        WebElement send = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        chatSendButton
                )
        );

        wait.until(currentDriver -> {
            List<WebElement> messages = currentDriver.findElements(
                    assistantChatMessages
            );

            for (WebElement message : messages) {
                if (message.isDisplayed()
                        && message.getText()
                        .contains("Server connect")) {
                    return true;
                }
            }

            return false;
        });

        String testMessage = "Hello, I need product support.";

        int messagesBefore = driver.findElements(
                userChatMessages
        ).size();

        messageInput.clear();
        messageInput.sendKeys(testMessage);

        wait.until(currentDriver ->
                !send.getAttribute("class").contains("disable")
        );

        send.click();

        wait.until(currentDriver ->
                currentDriver.findElements(userChatMessages).size()
                        > messagesBefore
        );

        List<WebElement> userMessages = driver.findElements(
                userChatMessages
        );

        WebElement latestMessage = userMessages.get(
                userMessages.size() - 1
        );

        Thread.sleep(500);

        Assert.assertTrue(
                body.isDisplayed(),
                "The Chat Body should be displayed."
        );

        Assert.assertTrue(
                latestMessage.getText().contains(testMessage),
                "The sent message should appear in the Chat Body."
        );

        Assert.assertEquals(
                messageInput.getAttribute("value"),
                "",
                "The message field should be cleared after sending."
        );
    }

    private void openChatWindow() throws InterruptedException {
        mainWindow = driver.getWindowHandle();

        int windowsBefore = driver.getWindowHandles().size();

        WebElement chat = wait.until(
                ExpectedConditions.elementToBeClickable(chatButton)
        );

        scrollToElement(chat);
        chat.click();

        wait.until(currentDriver ->
                currentDriver.getWindowHandles().size()
                        > windowsBefore
        );

        Set<String> windows = driver.getWindowHandles();

        for (String window : windows) {
            if (!window.equals(mainWindow)) {
                driver.switchTo().window(window);
                break;
            }
        }

        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        chatMessageInput
                )
        );

        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        chatSendButton
                )
        );

        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        chatBody
                )
        );

        Thread.sleep(1000);
    }

    private List<String> getProductOptions() {
        Select productSelect = new Select(
                driver.findElement(productList)
        );

        List<String> products = new ArrayList<>();

        for (WebElement option : productSelect.getOptions()) {
            String product = option.getText().trim();

            if (!product.isEmpty()) {
                products.add(product);
            }
        }

        return products;
    }

    private void triggerInputEvent(WebElement element) {
        js.executeScript(
                "arguments[0].dispatchEvent(" +
                        "new Event('input', {bubbles:true}));" +
                        "arguments[0].dispatchEvent(" +
                        "new Event('change', {bubbles:true}));" +
                        "arguments[0].dispatchEvent(" +
                        "new Event('blur', {bubbles:true}));",
                element
        );
    }

    private void scrollToElement(WebElement element) {
        js.executeScript(
                "arguments[0].scrollIntoView(" +
                        "{block:'center', inline:'center'});",
                element
        );
    }

    private void waitForPageReady() {
        wait.until(currentDriver ->
                "complete".equals(
                        ((JavascriptExecutor) currentDriver)
                                .executeScript(
                                        "return document.readyState"
                                )
                )
        );
    }

    private void waitForLoader() {
        try {
            wait.until(currentDriver -> {
                try {
                    List<WebElement> loaders =
                            currentDriver.findElements(
                                    By.cssSelector(
                                            ".loader, .loading, " +
                                                    ".spinner, " +
                                                    ".PopUp, " +
                                                    ".loadingOverlay"
                                    )
                            );

                    for (WebElement loader : loaders) {
                        if (loader.isDisplayed()) {
                            return false;
                        }
                    }

                    return true;
                } catch (StaleElementReferenceException exception) {
                    return false;
                }
            });
        } catch (Exception ignored) {
        }
    }

    private void closeExtraWindows() {
        if (driver == null) {
            return;
        }

        try {
            Set<String> windows = driver.getWindowHandles();

            if (windows.isEmpty()) {
                return;
            }

            String firstWindow = windows.iterator().next();

            for (String window : windows) {
                if (!window.equals(firstWindow)) {
                    try {
                        driver.switchTo().window(window);
                        driver.close();
                    } catch (Exception ignored) {
                    }
                }
            }

            driver.switchTo().window(firstWindow);
        } catch (Exception ignored) {
        }
    }

    private void createReportsFolder() {
        File reportsFolder = new File("test-reports");

        if (!reportsFolder.exists()) {
            reportsFolder.mkdirs();
        }
    }

    private void writeBugReport(ITestResult result) {
        File bugReport = new File(
                "test-reports/ContactUsBugReport.txt"
        );

        try (
                FileWriter writer = new FileWriter(
                        bugReport,
                        true
                )
        ) {
            writer.write(
                    "==================================================" +
                            System.lineSeparator()
            );

            writer.write(
                    "Test Name: "
                            + result.getMethod().getMethodName()
                            + System.lineSeparator()
            );

            writer.write(
                    "Failure Time: "
                            + LocalDateTime.now()
                            + System.lineSeparator()
            );

            writer.write(
                    "Test Class: "
                            + result.getTestClass().getName()
                            + System.lineSeparator()
            );

            if (result.getThrowable() != null) {
                writer.write(
                        "Error Type: "
                                + result.getThrowable()
                                .getClass()
                                .getName()
                                + System.lineSeparator()
                );

                writer.write(
                        "Error Message: "
                                + result.getThrowable()
                                .getMessage()
                                + System.lineSeparator()
                );
            }

            try {
                writer.write(
                        "Current URL: "
                                + driver.getCurrentUrl()
                                + System.lineSeparator()
                );

                writer.write(
                        "Page Title: "
                                + driver.getTitle()
                                + System.lineSeparator()
                );
            } catch (Exception ignored) {
            }

            writer.write(
                    "Status: FAILED"
                            + System.lineSeparator()
            );

            writer.write(
                    "=================================================="
                            + System.lineSeparator()
                            + System.lineSeparator()
            );

        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    private void writeSummaryReport() {
        File summaryReport = new File(
                "test-reports/ContactUsSummaryReport.txt"
        );

        int totalTests =
                passedTests
                        + failedTests
                        + skippedTests;

        try (
                FileWriter writer = new FileWriter(
                        summaryReport
                )
        ) {
            writer.write(
                    "CONTACT US TEST SUMMARY REPORT"
                            + System.lineSeparator()
            );

            writer.write(
                    "Generated At: "
                            + LocalDateTime.now()
                            + System.lineSeparator()
            );

            writer.write(
                    "Total Tests: "
                            + totalTests
                            + System.lineSeparator()
            );

            writer.write(
                    "Passed Tests: "
                            + passedTests
                            + System.lineSeparator()
            );

            writer.write(
                    "Failed Tests: "
                            + failedTests
                            + System.lineSeparator()
            );

            writer.write(
                    "Skipped Tests: "
                            + skippedTests
                            + System.lineSeparator()
            );

            writer.write(
                    "Final Status: "
                            + (failedTests == 0
                            ? "PASSED"
                            : "FAILED")
                            + System.lineSeparator()
            );

        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
}