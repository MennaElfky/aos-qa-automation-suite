
package com.aos.web.tests.feature;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;

public class CheckoutPaymentTest {

    WebDriver driver;
    WebDriverWait wait;

    private static final String BASE_URL =
            "https://www.advantageonlineshopping.com/";

    private static final String PRODUCT_NAME =
            "HP ELITE X2 1011 G1 TABLET";

    @BeforeClass
    public void beforeClass() {
        System.setProperty(
                "webdriver.chrome.driver",
                "C:\\chromedriver-win64\\chromedriver.exe"
        );
    }

    @BeforeMethod
    public void beforeMethod() {

        driver = new ChromeDriver();
        driver.manage().window().maximize();

        wait = new WebDriverWait(driver, Duration.ofSeconds(25));

        // Order Payment redirects to home when the cart is empty,
        // so add the product first.
        driver.get(BASE_URL + "#/product/17");
        pause(1000);

        wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.name("save_to_cart")
                )
        ).click();
        pause(1000);

        driver.get(BASE_URL + "#/orderPayment");
        pause(1000);

        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.id("orderPayment")
                )
        );
    }

    // TC1: Verify Order Payment page is displayed
    @Test
    public void testCase1() {

        WebElement paymentPage = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.id("orderPayment")
                )
        );

        Assert.assertTrue(
                paymentPage.isDisplayed(),
                "Order Payment page should be displayed"
        );

        WebElement title = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("nav.pages a.select")
                )
        );

        Assert.assertTrue(
                title.getText().contains("ORDER PAYMENT"),
                "Order Payment title should be displayed"
        );
    }

    // TC2: Verify order summary details
    @Test
    public void testCase2() {

        // The product was added in beforeMethod.
        Assert.assertTrue(
                driver.findElement(By.id("orderPayment")).isDisplayed(),
                "Order Payment page should be displayed"
        );

        // Wait until the order summary is displayed
        wait.until(
                ExpectedConditions.textToBePresentInElementLocated(
                        By.tagName("body"),
                        PRODUCT_NAME
                )
        );

        String pageText = driver.findElement(
                By.tagName("body")
        ).getText();

        Assert.assertTrue(
                pageText.contains("1 ITEM"),
                "Order should contain one item"
        );

        Assert.assertTrue(
                pageText.contains(PRODUCT_NAME),
                "Product name should match"
        );

        Assert.assertTrue(
                pageText.contains("$1,279.00"),
                "Product total should be $1,279.00"
        );
    }

    // TC3: Verify login button behavior with empty fields
    @Test
    public void testCase3() {

        WebElement username = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.name("usernameInOrderPayment")
                )
        );

        WebElement password = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.name("passwordInOrderPayment")
                )
        );

        WebElement loginButton = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.id("login_btn")
                )
        );

        username.clear();
        password.clear();

        Assert.assertFalse(
                loginButton.isEnabled(),
                "Login button should be disabled with empty fields"
        );
    }

    // TC4: Verify invalid login credentials
    @Test
    public void testCase4() {

        WebElement username = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.name("usernameInOrderPayment")
                )
        );

        WebElement password = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.name("passwordInOrderPayment")
                )
        );

        username.clear();
        password.clear();

        username.sendKeys("InvalidUser123");
        password.sendKeys("Wrong123");

        String urlBeforeLogin = driver.getCurrentUrl();

        WebElement loginButton = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.id("login_btn")
                )
        );

        loginButton.click();
        pause(1000);

        // Wait for the application to respond
        wait.until(
                d -> !d.getCurrentUrl().equals(urlBeforeLogin)
                        || !d.findElements(
                        By.name("usernameInOrderPayment")
                ).isEmpty()
        );

        String currentUrl = driver.getCurrentUrl();

        String pageText = driver.findElement(
                By.tagName("body")
        ).getText();

        System.out.println("========== TC4 DIAGNOSTICS ==========");
        System.out.println("CURRENT URL: " + currentUrl);
        System.out.println("PAGE TEXT: " + pageText);

        System.out.println("ERROR LABEL:");

        driver.findElements(
                        By.cssSelector("#orderPayment label.ng-binding")
                ).stream()
                .map(WebElement::getText)
                .filter(text -> !text.trim().isEmpty())
                .forEach(System.out::println);

        System.out.println("=====================================");

        // Verify that the application did not open an authenticated page
        Assert.assertFalse(
                currentUrl.contains("#/myAccount"),
                "Invalid credentials should not open the user account"
        );

        Assert.assertFalse(
                currentUrl.contains("#/account"),
                "Invalid credentials should not open the account page"
        );

        // Verify that the user remains within the shopping application
        Assert.assertTrue(
                currentUrl.contains("#/"),
                "Application should remain within the shopping application"
        );
    }

    // TC5: Verify registration button
    @Test
    public void testCase5() {

        WebElement registrationButton = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.id("registration_btn")
                )
        );

        Assert.assertTrue(
                registrationButton.isDisplayed(),
                "Registration button should be displayed"
        );

        registrationButton.click();
        pause(1000);

        wait.until(
                d -> !d.getCurrentUrl().contains("orderPayment")
        );

        Assert.assertFalse(
                driver.getCurrentUrl().contains("orderPayment"),
                "User should leave Order Payment page after registration click"
        );
    }

    @AfterMethod
    public void afterMethod() {

        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }

    private void pause(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @AfterClass(alwaysRun = true)
    public void afterClass() {
        if (driver != null) {
            driver.quit();
        }
    }
}

