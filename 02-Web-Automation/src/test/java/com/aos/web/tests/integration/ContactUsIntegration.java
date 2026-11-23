package com.aos.web.tests.feature;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
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
import java.util.List;
import java.util.Set;

public class ContactUsIntegration {

    WebDriver driver;
    WebDriverWait wait;
    JavascriptExecutor js;

    int passedTests;
    int failedTests;
    int skippedTests;

    By supportCover = By.id("supportCover");

    By contactUsTitle = By.cssSelector(
            "h1.contact_us"
    );

    By categoryList = By.cssSelector(
            "select[name='categoryListboxContactUs']"
    );

    By productList = By.cssSelector(
            "select[name='productListboxContactUs']"
    );

    By emailInput = By.cssSelector(
            "input[name='emailContactUs']"
    );

    By subjectInput = By.cssSelector(
            "textarea[name='subjectTextareaContactUs']"
    );

    By sendButton = By.id("send_btn");

    By pageLoader = By.cssSelector(
            "body > div.loader"
    );

    By emailErrorElements = By.xpath(
            "//input[@name='emailContactUs']" +
                    "/ancestor::sec-view[1]//ul//a"
    );

    By successCover = By.id(
            "registerSuccessCover"
    );

    By successMessage = By.cssSelector(
            "#registerSuccessCover p.successMessage"
    );

    @BeforeClass
    public void beforeClass() {
        System.setProperty(
                "webdriver.chrome.driver",
                "C:\\chromedriver-win64\\chromedriver.exe"
        );

        ChromeOptions options = new ChromeOptions();

        options.addArguments(
                "--start-maximized"
        );

        options.addArguments(
                "--disable-notifications"
        );

        options.addArguments(
                "--disable-popup-blocking"
        );

        driver = new ChromeDriver(
                options
        );

        wait = new WebDriverWait(
                driver,
                Duration.ofSeconds(40)
        );

        js = (JavascriptExecutor) driver;

        createReportsFolder();
    }

    @BeforeMethod
    public void beforeMethod() throws InterruptedException {
        closeExtraWindows();

        if (driver.getWindowHandles().isEmpty()) {
            throw new IllegalStateException(
                    "No browser window is available."
            );
        }

        String mainWindow =
                driver.getWindowHandles()
                        .iterator()
                        .next();

        driver.switchTo().window(
                mainWindow
        );

        String freshUrl =
                "https://www.advantageonlineshopping.com/" +
                        "?testRun=" +
                        System.nanoTime() +
                        "#/";

        driver.get(
                freshUrl
        );

        waitForPageReady();
        waitForLoader();

        WebElement category = wait.until(
                ExpectedConditions.presenceOfElementLocated(
                        categoryList
                )
        );

        scrollToElement(
                category
        );

        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        supportCover
                )
        );

        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        contactUsTitle
                )
        );

        wait.until(
                ExpectedConditions.elementToBeClickable(
                        categoryList
                )
        );

        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        productList
                )
        );

        wait.until(
                ExpectedConditions.elementToBeClickable(
                        emailInput
                )
        );

        wait.until(
                ExpectedConditions.elementToBeClickable(
                        subjectInput
                )
        );

        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        sendButton
                )
        );

        Assert.assertFalse(
                driver.findElement(supportCover)
                        .findElement(By.xpath(".."))
                        .getAttribute("class")
                        .contains("ng-hide"),
                "The Contact Us form should be visible before every test."
        );

        Thread.sleep(1000);
    }

    @Test(priority = 1)
    public void testCase1() throws InterruptedException {
        selectCategory(
                "Laptops"
        );

        waitUntilProductsLoad();

        Select category = new Select(
                driver.findElement(
                        categoryList
                )
        );

        Select products = new Select(
                driver.findElement(
                        productList
                )
        );

        Assert.assertEquals(
                category.getFirstSelectedOption()
                        .getText()
                        .trim(),
                "Laptops",
                "The selected Category should be Laptops."
        );

        Assert.assertTrue(
                hasProductNamed(
                        products,
                        "HP ENVY - 17t Touch Laptop"
                ),
                "The Product list should contain Laptop products."
        );

        Assert.assertTrue(
                hasProductNamed(
                        products,
                        "HP Spectre x360 - 13-4102dx"
                ),
                "The Product list should contain HP Spectre x360 - 13-4102dx."
        );

        Assert.assertTrue(
                countAvailableProducts(
                        products
                ) >= 1,
                "Selecting Laptops should load available Laptop products."
        );

        Thread.sleep(1000);
    }

    @Test(priority = 2)
    public void testCase2() throws InterruptedException {
        selectCategory(
                "Laptops"
        );

        waitUntilProductsLoad();

        Select products = new Select(
                driver.findElement(
                        productList
                )
        );

        products.selectByVisibleText(
                "HP ENVY - 17t Touch Laptop"
        );

        Assert.assertEquals(
                products.getFirstSelectedOption()
                        .getText()
                        .trim(),
                "HP ENVY - 17t Touch Laptop",
                "HP ENVY - 17t Touch Laptop should be selected."
        );

        selectCategory(
                "Mice"
        );

        wait.until(currentDriver -> {
            Select refreshedProducts = new Select(
                    currentDriver.findElement(
                            productList
                    )
            );

            String selectedProduct =
                    refreshedProducts
                            .getFirstSelectedOption()
                            .getText()
                            .trim();

            return !selectedProduct.equals(
                    "HP ENVY - 17t Touch Laptop"
            );
        });

        waitUntilProductsLoad();

        Select refreshedProducts = new Select(
                driver.findElement(
                        productList
                )
        );

        Assert.assertNotEquals(
                refreshedProducts.getFirstSelectedOption()
                        .getText()
                        .trim(),
                "HP ENVY - 17t Touch Laptop",
                "Changing Category should reset the selected Product."
        );

        Assert.assertTrue(
                countAvailableProducts(
                        refreshedProducts
                ) >= 1,
                "Changing Category to Mice should load Mice products."
        );

        Thread.sleep(1000);
    }

    @Test(priority = 3)
    public void testCase3() throws InterruptedException {
        selectCategory(
                "Laptops"
        );

        waitUntilProductsLoad();

        Select products = new Select(
                driver.findElement(
                        productList
                )
        );

        String selectedProduct =
                products.getFirstSelectedOption()
                        .getText()
                        .trim();

        Assert.assertTrue(
                selectedProduct.isEmpty()
                        || selectedProduct.equalsIgnoreCase(
                        "Select Product"
                ),
                "The Product field should remain empty."
        );

        enterEmail(
                "mostvfv.mo7med@gmail.com"
        );

        enterSubject(
                "Hello World"
        );

        WebElement send =
                waitForSendButtonEnabled();

        clickSendButton(
                send
        );

        assertSuccessMessageDisplayed();

        Thread.sleep(1000);
    }

    @Test(priority = 4)
    public void testCase4() throws InterruptedException {
        String expectedEmail =
                "mostvfv.mo7med@gmail.com";

        String expectedSubject =
                "Hello World";

        enterEmail(
                expectedEmail
        );

        enterSubject(
                expectedSubject
        );

        WebElement email =
                driver.findElement(
                        emailInput
                );

        WebElement subject =
                driver.findElement(
                        subjectInput
                );

        String actualEmail =
                email.getAttribute(
                        "value"
                );

        String actualSubject =
                subject.getAttribute(
                        "value"
                );

        Assert.assertEquals(
                actualEmail,
                expectedEmail,
                "The Email field should accept the valid Email value."
        );

        Assert.assertEquals(
                actualSubject,
                expectedSubject,
                "The Subject field should accept the valid Subject value."
        );

        WebElement send =
                waitForSendButtonEnabled();

        Assert.assertTrue(
                send.isEnabled(),
                "The Send button should be enabled for valid Email and Subject."
        );

        Assert.assertFalse(
                send.getAttribute("class")
                        .contains("invalid"),
                "The Send button should not be invalid for valid Email and Subject."
        );

        Thread.sleep(1000);
    }

    @Test(priority = 5)
    public void testCase5() throws InterruptedException {
        enterEmail(
                "mostvfv.mo7med"
        );

        enterSubject(
                "Hello World"
        );

        WebElement email =
                driver.findElement(
                        emailInput
                );

        WebElement subject =
                driver.findElement(
                        subjectInput
                );

        subject.click();

        triggerValidation(
                email
        );

        Thread.sleep(1000);

        boolean emailErrorDisplayed =
                hasVisibleValidationError(
                        emailErrorElements
                );

        Assert.assertTrue(
                emailErrorDisplayed,
                "Bug detected: A validation error should appear under the Email field for an invalid Email."
        );
    }

    @Test(priority = 6)
    public void testCase6() throws InterruptedException {
        enterEmail(
                "mostvfv.mo7med@gmail.com"
        );

        WebElement subject = wait.until(
                ExpectedConditions.elementToBeClickable(
                        subjectInput
                )
        );

        subject.clear();
        subject.click();

        WebElement email =
                driver.findElement(
                        emailInput
                );

        email.click();

        triggerValidation(
                subject
        );

        Thread.sleep(1000);

        WebElement send =
                driver.findElement(
                        sendButton
                );

        String sendClass =
                send.getAttribute(
                        "class"
                );

        String subjectClass =
                subject.getAttribute(
                        "class"
                );

        boolean sendMarkedInvalid =
                sendClass != null
                        && sendClass.contains(
                        "invalid"
                );

        boolean subjectMarkedInvalid =
                subjectClass != null
                        && subjectClass.contains(
                        "ng-invalid"
                );

        Assert.assertFalse(
                send.isEnabled(),
                "The Send button should remain disabled when Subject is empty."
        );

        Assert.assertTrue(
                sendMarkedInvalid
                        || subjectMarkedInvalid,
                "The form should remain invalid when Subject is empty."
        );

        Assert.assertTrue(
                driver.findElements(
                        By.cssSelector(
                                "#registerSuccessCover:not(.ng-hide) " +
                                        "p.successMessage"
                        )
                ).isEmpty(),
                "The form should not be submitted while Subject is empty."
        );

        Thread.sleep(1000);
    }

    private void selectCategory(
            String categoryName
    ) throws InterruptedException {
        WebElement categoryElement = wait.until(
                ExpectedConditions.elementToBeClickable(
                        categoryList
                )
        );

        scrollToElement(
                categoryElement
        );

        categoryElement.click();

        categoryElement.sendKeys(
                Keys.HOME
        );

        Thread.sleep(300);

        categoryElement.sendKeys(
                categoryName
        );

        categoryElement.sendKeys(
                Keys.ENTER
        );

        wait.until(currentDriver -> {
            Select category = new Select(
                    currentDriver.findElement(
                            categoryList
                    )
            );

            return category
                    .getFirstSelectedOption()
                    .getText()
                    .trim()
                    .equals(categoryName);
        });

        WebElement selectedCategory =
                driver.findElement(
                        categoryList
                );

        js.executeScript(
                "arguments[0].dispatchEvent(" +
                        "new Event('input', {bubbles:true}));" +
                        "arguments[0].dispatchEvent(" +
                        "new Event('change', {bubbles:true}));",
                selectedCategory
        );

        waitForLoader();

        Thread.sleep(500);
    }

    private void waitUntilProductsLoad() {
        wait.until(currentDriver -> {
            WebElement productElement =
                    currentDriver.findElement(
                            productList
                    );

            Select products = new Select(
                    productElement
            );

            return countAvailableProducts(
                    products
            ) >= 1;
        });
    }

    private int countAvailableProducts(
            Select products
    ) {
        int count = 0;

        for (WebElement option :
                products.getOptions()) {
            String productName =
                    option.getText()
                            .trim();

            if (!productName.isEmpty()
                    && !productName.equalsIgnoreCase(
                    "Select Product"
            )) {
                count++;
            }
        }

        return count;
    }

    private boolean hasProductNamed(
            Select products,
            String expectedProduct
    ) {
        for (WebElement option :
                products.getOptions()) {
            if (option.getText()
                    .trim()
                    .equals(expectedProduct)) {
                return true;
            }
        }

        return false;
    }

    private void enterEmail(
            String value
    ) {
        WebElement email = wait.until(
                ExpectedConditions.elementToBeClickable(
                        emailInput
                )
        );

        email.clear();

        email.sendKeys(
                value
        );

        triggerValidation(
                email
        );
    }

    private void enterSubject(
            String value
    ) {
        WebElement subject = wait.until(
                ExpectedConditions.elementToBeClickable(
                        subjectInput
                )
        );

        subject.clear();

        subject.sendKeys(
                value
        );

        triggerValidation(
                subject
        );
    }

    private void triggerValidation(
            WebElement element
    ) {
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

    private WebElement waitForSendButtonEnabled() {
        waitForLoader();

        return wait.until(currentDriver -> {
            WebElement button =
                    currentDriver.findElement(
                            sendButton
                    );

            String classes =
                    button.getAttribute(
                            "class"
                    );

            if (button.isDisplayed()
                    && button.isEnabled()
                    && classes != null
                    && !classes.contains(
                    "invalid"
            )) {
                return button;
            }

            return null;
        });
    }

    private void clickSendButton(
            WebElement send
    ) {
        scrollToElement(
                send
        );

        waitForLoader();

        wait.until(
                ExpectedConditions.elementToBeClickable(
                        sendButton
                )
        );

        wait.until(
                ExpectedConditions.invisibilityOfElementLocated(
                        pageLoader
                )
        );

        WebElement currentSendButton =
                driver.findElement(
                        sendButton
                );

        currentSendButton.click();
    }

    private boolean hasVisibleValidationError(
            By locator
    ) {
        List<WebElement> errors =
                driver.findElements(
                        locator
                );

        for (WebElement error : errors) {
            if (error.isDisplayed()
                    && !error.getText()
                    .trim()
                    .isEmpty()) {
                return true;
            }
        }

        return false;
    }

    private void assertSuccessMessageDisplayed() {
        waitForLoader();

        WebElement cover = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        successCover
                )
        );

        WebElement message = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        successMessage
                )
        );

        Assert.assertTrue(
                cover.isDisplayed(),
                "The Contact Us success section should be displayed."
        );

        Assert.assertEquals(
                message.getText()
                        .trim(),
                "Thank you for contacting Advantage support.",
                "The Contact Us success message should be displayed."
        );
    }

    private void scrollToElement(
            WebElement element
    ) {
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
        wait.until(currentDriver -> {
            List<WebElement> loaders =
                    currentDriver.findElements(
                            pageLoader
                    );

            for (WebElement loader : loaders) {
                if (loader.isDisplayed()) {
                    return false;
                }
            }

            return true;
        });
    }

    private void closeExtraWindows() {
        if (driver == null) {
            return;
        }

        try {
            Set<String> windows =
                    driver.getWindowHandles();

            if (windows.isEmpty()) {
                return;
            }

            String firstWindow =
                    windows.iterator()
                            .next();

            for (String window : windows) {
                if (!window.equals(
                        firstWindow
                )) {
                    driver.switchTo()
                            .window(
                                    window
                            );

                    driver.close();
                }
            }

            driver.switchTo()
                    .window(
                            firstWindow
                    );

        } catch (Exception ignored) {
        }
    }

    private void createReportsFolder() {
        File folder = new File(
                "test-reports"
        );

        if (!folder.exists()) {
            folder.mkdirs();
        }
    }

    private void writeBugReport(
            ITestResult result
    ) {
        File report = new File(
                "test-reports/" +
                        "ContactUsIntegrationBugReport.txt"
        );

        try (
                FileWriter writer =
                        new FileWriter(
                                report,
                                true
                        )
        ) {
            writer.write(
                    "========================================"
                            + System.lineSeparator()
            );

            writer.write(
                    "Test: "
                            + result.getMethod()
                            .getMethodName()
                            + System.lineSeparator()
            );

            writer.write(
                    "Time: "
                            + LocalDateTime.now()
                            + System.lineSeparator()
            );

            writer.write(
                    "Status: FAILED"
                            + System.lineSeparator()
            );

            if (result.getThrowable() != null) {
                writer.write(
                        "Error: "
                                + result.getThrowable()
                                .getMessage()
                                + System.lineSeparator()
                );
            }

            try {
                writer.write(
                        "URL: "
                                + driver.getCurrentUrl()
                                + System.lineSeparator()
                );
            } catch (Exception ignored) {
            }

            writer.write(
                    "========================================"
                            + System.lineSeparator()
                            + System.lineSeparator()
            );

        } catch (IOException exception) {
            throw new RuntimeException(
                    exception
            );
        }
    }

    private void writeSummaryReport() {
        File report = new File(
                "test-reports/" +
                        "ContactUsIntegrationSummaryReport.txt"
        );

        int totalTests =
                passedTests
                        + failedTests
                        + skippedTests;

        try (
                FileWriter writer =
                        new FileWriter(
                                report
                        )
        ) {
            writer.write(
                    "CONTACT US INTEGRATION SUMMARY"
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
                    "Passed: "
                            + passedTests
                            + System.lineSeparator()
            );

            writer.write(
                    "Failed: "
                            + failedTests
                            + System.lineSeparator()
            );

            writer.write(
                    "Skipped: "
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
            throw new RuntimeException(
                    exception
            );
        }
    }

    @AfterMethod
    public void afterMethod(
            ITestResult result
    ) {
        if (result.getStatus()
                == ITestResult.SUCCESS) {
            passedTests++;
        } else if (result.getStatus()
                == ITestResult.FAILURE) {
            failedTests++;

            writeBugReport(
                    result
            );
        } else {
            skippedTests++;
        }

        closeExtraWindows();
    }

    @AfterClass
    public void afterClass() {
        writeSummaryReport();

        if (driver != null) {
            driver.quit();
        }
    }
}