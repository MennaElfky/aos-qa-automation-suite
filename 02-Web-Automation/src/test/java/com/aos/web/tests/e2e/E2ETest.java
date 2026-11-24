package com.aos.web.tests.e2e;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;

public class E2ETest {

    WebDriver driver;
    WebDriverWait wait;
    JavascriptExecutor js;

    String USERNAME = "meeeeenaALLAHe";
    String PASSWORD = "123meN";
    String SAFEPAY_USERNAME = "safepayuser";
    String SAFEPAY_PASSWORD = "Safe@123";

    By mainLoader = By.cssSelector("body > div.loader");

    By menuUserLink = By.id("menuUserLink");
    By loginPopup = By.cssSelector("login-modal .PopUp");
    By loginUsername = By.cssSelector("login-modal input[name='username']");
    By loginPassword = By.cssSelector("login-modal input[name='password']");
    By signInButton = By.id("sign_in_btn");
    By signInMessage = By.id("signInResultMessage");
    By headerUsername = By.cssSelector("span.hi-user.containMiniTitle");
    By forgotPasswordLink = By.cssSelector("a.forgot-Passwowd");

    By productTitle = By.cssSelector("#Description h1");
    By productColors = By.cssSelector("#productProperties span.productColor");
    By addToCartButton = By.cssSelector(
            "#productProperties button[name='save_to_cart']"
    );

    By shoppingCart = By.id("shoppingCart");
    By cartProductNames = By.cssSelector(
            "#shoppingCart table tbody label.productName"
    );
    By checkoutButton = By.id("checkOutButton");

    By orderPayment = By.id("orderPayment");
    By guestUsername = By.cssSelector(
            "input[name='usernameInOrderPayment']"
    );
    By guestPassword = By.cssSelector(
            "input[name='passwordInOrderPayment']"
    );
    By guestLoginButton = By.id("login_btn");

    By userSection = By.id("userSection");
    By userDetails = By.id("userDetails");
    By nextButton = By.cssSelector("#userSection #next_btn");
    By paymentMethod = By.id("paymentMethod");

    By safePayUsername = By.cssSelector(
            "#paymentMethod input[name='safepay_username']"
    );
    By safePayPassword = By.cssSelector(
            "#paymentMethod input[name='safepay_password']"
    );
    By safePayButton = By.id("pay_now_btn_SAFEPAY");
    By safePayError = By.id("payNowSPErrorLabel");

    By orderSuccess = By.id("orderPaymentSuccess");
    By orderNumber = By.id("orderNumberLabel");

    @BeforeMethod
    public void beforeMethod() {
        System.setProperty(
                "webdriver.chrome.driver",
                "C:\\chromedriver-win64\\chromedriver.exe"
        );

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-popup-blocking");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(
                driver,
                Duration.ofSeconds(60)
        );
        js = (JavascriptExecutor) driver;

        openHomePage();
    }

    @Test(priority = 1)
    public void testCase1() {
        openLoginModal();

        enterLoginCredentials(
                USERNAME,
                "Wrong@999"
        );

        clickSignInButton();

        wait.until(currentDriver ->
                getSignInMessage().equals(
                        "Incorrect user name or password."
                )
        );

        Assert.assertEquals(
                getSignInMessage(),
                "Incorrect user name or password."
        );

        Assert.assertFalse(
                isUserLoggedIn()
        );

        WebElement password = wait.until(
                ExpectedConditions.elementToBeClickable(
                        loginPassword
                )
        );

        password.clear();
        password.sendKeys(PASSWORD);

        clickSignInButton();

        wait.until(currentDriver ->
                isUserLoggedIn()
        );

        Assert.assertTrue(
                isUserLoggedIn()
        );

        String selectedProduct = addProductToCart();

        openShoppingCart();

        Assert.assertTrue(
                cartContainsProduct(selectedProduct)
        );
    }

    @Test(priority = 2)
    public void testCase2() {
        login();

        String selectedProduct = addProductToCart();

        openShoppingCart();

        Assert.assertTrue(
                cartContainsProduct(selectedProduct)
        );

        clickCheckout();

        waitForRegisteredCheckout();

        Assert.assertTrue(
                getBodyText().toUpperCase().contains(
                        selectedProduct.toUpperCase()
                )
        );

        goToPaymentMethod();

        completeSafePay(
                SAFEPAY_PASSWORD
        );

        Assert.assertTrue(
                isVisible(orderSuccess)
        );

        Assert.assertFalse(
                getOrderNumberText().isEmpty()
        );

        Assert.assertTrue(
                getBodyText().contains(
                        "Thank you for buying with Advantage"
                )
        );
    }

    @Test(priority = 3)
    public void testCase3() {
        String selectedProduct = addProductToCart();

        openShoppingCart();

        Assert.assertTrue(
                cartContainsProduct(selectedProduct)
        );

        clickCheckout();

        waitForGuestCheckout();

        WebElement username = wait.until(
                ExpectedConditions.elementToBeClickable(
                        guestUsername
                )
        );

        WebElement password = wait.until(
                ExpectedConditions.elementToBeClickable(
                        guestPassword
                )
        );

        username.clear();
        username.sendKeys(USERNAME);

        password.clear();
        password.sendKeys(PASSWORD);

        WebElement loginButton = wait.until(
                ExpectedConditions.elementToBeClickable(
                        guestLoginButton
                )
        );

        clickElement(loginButton);

        waitForLoader();

        waitForRegisteredCheckout();

        Assert.assertTrue(
                getBodyText().toUpperCase().contains(
                        selectedProduct.toUpperCase()
                )
        );

        goToPaymentMethod();

        completeSafePay(
                SAFEPAY_PASSWORD
        );

        Assert.assertTrue(
                isVisible(orderSuccess)
        );

        Assert.assertFalse(
                getOrderNumberText().isEmpty()
        );
    }

    @Test(priority = 4)
    public void testCase4() {
        openLoginModal();

        WebElement forgotPassword = wait.until(
                ExpectedConditions.elementToBeClickable(
                        forgotPasswordLink
                )
        );

        clickElement(forgotPassword);

        boolean recoveryScreenOpened =
                waitForRecoveryScreen();

        Assert.assertTrue(
                recoveryScreenOpened,
                "Bug detected: Forgot your password should open the password recovery screen."
        );
    }

    @Test(priority = 5)
    public void testCase5() {
        login();

        String selectedProduct = addProductToCart();

        openShoppingCart();

        Assert.assertTrue(
                cartContainsProduct(selectedProduct)
        );

        clickCheckout();

        waitForRegisteredCheckout();

        goToPaymentMethod();

        WebElement username = wait.until(
                ExpectedConditions.elementToBeClickable(
                        safePayUsername
                )
        );

        WebElement password = wait.until(
                ExpectedConditions.elementToBeClickable(
                        safePayPassword
                )
        );

        username.clear();
        username.sendKeys(SAFEPAY_USERNAME);

        password.clear();
        password.sendKeys("Wrong123");

        WebElement payButton = wait.until(
                ExpectedConditions.elementToBeClickable(
                        safePayButton
                )
        );

        clickElement(payButton);

        waitForLoader();

        boolean orderCompleted =
                waitForPossibleOrderCompletion();

        Assert.assertFalse(
                orderCompleted,
                "Bug detected: SafePay accepted an incorrect password and completed the order."
        );
    }

    private void openHomePage() {
        driver.get(
                "https://www.advantageonlineshopping.com/#/"
        );

        waitForPageReady();
        waitForLoader();

        wait.until(
                ExpectedConditions.presenceOfElementLocated(
                        menuUserLink
                )
        );

        wait.until(currentDriver ->
                isAngularReady()
        );
    }

    private void login() {
        openLoginModal();

        enterLoginCredentials(
                USERNAME,
                PASSWORD
        );

        clickSignInButton();

        wait.until(currentDriver ->
                isUserLoggedIn()
                        || getSignInMessage().equals(
                        "Incorrect user name or password."
                )
        );

        Assert.assertTrue(
                isUserLoggedIn(),
                "Login failed. Check USERNAME and PASSWORD."
        );
    }

    private void openLoginModal() {
        waitForLoader();

        WebElement menu = wait.until(
                ExpectedConditions.presenceOfElementLocated(
                        menuUserLink
                )
        );

        try {
            wait.until(
                    ExpectedConditions.elementToBeClickable(
                            menuUserLink
                    )
            ).click();
        } catch (Exception exception) {
            js.executeScript(
                    "arguments[0].click();",
                    menu
            );
        }

        boolean opened = waitForLoginInputs(
                Duration.ofSeconds(8)
        );

        if (!opened) {
            js.executeScript(
                    "arguments[0].click();",
                    driver.findElement(menuUserLink)
            );

            opened = waitForLoginInputs(
                    Duration.ofSeconds(15)
            );
        }

        Assert.assertTrue(
                opened,
                "The login modal did not open."
        );
    }

    private boolean waitForLoginInputs(
            Duration duration
    ) {
        try {
            WebDriverWait modalWait =
                    new WebDriverWait(
                            driver,
                            duration
                    );

            return modalWait.until(
                    currentDriver ->
                            isVisible(loginPopup)
                                    && isVisible(loginUsername)
                                    && isVisible(loginPassword)
            );
        } catch (TimeoutException exception) {
            return false;
        }
    }

    private void enterLoginCredentials(
            String usernameValue,
            String passwordValue
    ) {
        WebElement username = wait.until(
                ExpectedConditions.elementToBeClickable(
                        loginUsername
                )
        );

        WebElement password = wait.until(
                ExpectedConditions.elementToBeClickable(
                        loginPassword
                )
        );

        username.clear();
        username.sendKeys(usernameValue);

        password.clear();
        password.sendKeys(passwordValue);

        wait.until(currentDriver ->
                currentDriver.findElement(
                        signInButton
                ).isEnabled()
        );
    }

    private void clickSignInButton() {
        waitForLoader();

        WebElement button = wait.until(
                ExpectedConditions.elementToBeClickable(
                        signInButton
                )
        );

        try {
            button.click();
        } catch (Exception exception) {
            js.executeScript(
                    "arguments[0].click();",
                    button
            );
        }

        wait.until(currentDriver ->
                isLoaderVisible()
                        || isUserLoggedIn()
                        || getSignInMessage().equals(
                        "Incorrect user name or password."
                )
        );

        waitForLoader();
    }

    private String addProductToCart() {
        driver.get(
                "https://www.advantageonlineshopping.com/#/product/16"
        );

        waitForPageReady();
        waitForLoader();

        WebElement title = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        productTitle
                )
        );

        String selectedProduct =
                title.getText().trim();

        WebElement color = wait.until(
                currentDriver -> {
                    List<WebElement> colors =
                            currentDriver.findElements(
                                    productColors
                            );

                    for (WebElement currentColor :
                            colors) {
                        try {
                            if (currentColor.isDisplayed()) {
                                return currentColor;
                            }
                        } catch (
                                StaleElementReferenceException ignored
                        ) {
                        }
                    }

                    return null;
                }
        );

        clickElement(color);

        WebElement addButton = wait.until(
                ExpectedConditions.elementToBeClickable(
                        addToCartButton
                )
        );

        clickElement(addButton);

        waitForLoader();

        driver.get(
                "https://www.advantageonlineshopping.com/#/shoppingCart"
        );

        waitForPageReady();
        waitForLoader();

        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        shoppingCart
                )
        );

        wait.until(currentDriver ->
                cartContainsProduct(selectedProduct)
        );

        return selectedProduct;
    }

    private void openShoppingCart() {
        if (driver.getCurrentUrl()
                .toLowerCase()
                .contains("#/shoppingcart")
                && isVisible(shoppingCart)) {
            return;
        }

        driver.get(
                "https://www.advantageonlineshopping.com/#/shoppingCart"
        );

        waitForPageReady();
        waitForLoader();

        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        shoppingCart
                )
        );

        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        checkoutButton
                )
        );
    }

    private boolean cartContainsProduct(
            String expectedProduct
    ) {
        List<WebElement> products =
                driver.findElements(
                        cartProductNames
                );

        for (WebElement product :
                products) {
            try {
                if (product.isDisplayed()
                        && product.getText()
                        .trim()
                        .equalsIgnoreCase(
                                expectedProduct
                        )) {
                    return true;
                }
            } catch (
                    StaleElementReferenceException ignored
            ) {
            }
        }

        return false;
    }

    private void clickCheckout() {
        WebElement checkout = wait.until(
                ExpectedConditions.elementToBeClickable(
                        checkoutButton
                )
        );

        clickElement(checkout);

        waitForLoader();

        wait.until(currentDriver -> {
            String url =
                    currentDriver.getCurrentUrl()
                            .toLowerCase();

            return url.contains("#/login")
                    || url.contains("#/orderpayment")
                    || isVisible(guestUsername)
                    || isVisible(orderPayment);
        });
    }

    private void waitForGuestCheckout() {
        wait.until(currentDriver ->
                isVisible(guestUsername)
                        && isVisible(guestPassword)
                        && isVisible(guestLoginButton)
        );
    }

    private void waitForRegisteredCheckout() {
        waitForLoader();

        wait.until(currentDriver ->
                isVisible(orderPayment)
                        && isVisible(userSection)
                        && isVisible(userDetails)
                        && isVisible(nextButton)
        );
    }

    private void goToPaymentMethod() {
        WebElement next = wait.until(
                ExpectedConditions.elementToBeClickable(
                        nextButton
                )
        );

        clickElement(next);

        waitForLoader();

        wait.until(currentDriver ->
                isVisible(paymentMethod)
                        && isVisible(safePayUsername)
                        && isVisible(safePayPassword)
                        && isVisible(safePayButton)
        );
    }

    private void completeSafePay(
            String passwordValue
    ) {
        WebElement username = wait.until(
                ExpectedConditions.elementToBeClickable(
                        safePayUsername
                )
        );

        WebElement password = wait.until(
                ExpectedConditions.elementToBeClickable(
                        safePayPassword
                )
        );

        username.clear();
        username.sendKeys(SAFEPAY_USERNAME);

        password.clear();
        password.sendKeys(passwordValue);

        WebElement payButton = wait.until(
                ExpectedConditions.elementToBeClickable(
                        safePayButton
                )
        );

        clickElement(payButton);

        waitForLoader();

        wait.until(currentDriver ->
                isVisible(orderSuccess)
                        && !getOrderNumberText()
                        .isEmpty()
        );
    }

    private boolean waitForRecoveryScreen() {
        try {
            WebDriverWait shortWait =
                    new WebDriverWait(
                            driver,
                            Duration.ofSeconds(6)
                    );

            return shortWait.until(
                    currentDriver -> {
                        List<WebElement> fields =
                                currentDriver.findElements(
                                        By.cssSelector(
                                                "input[name='emailForgotPassword'], " +
                                                        "input[name='forgotPasswordEmail']"
                                        )
                                );

                        for (WebElement field :
                                fields) {
                            try {
                                if (field.isDisplayed()) {
                                    return true;
                                }
                            } catch (
                                    StaleElementReferenceException ignored
                            ) {
                            }
                        }

                        String text =
                                getBodyText().toUpperCase();

                        return text.contains(
                                "RECOVER PASSWORD"
                        )
                                || text.contains(
                                "RESET PASSWORD"
                        )
                                || text.contains(
                                "PASSWORD RECOVERY"
                        );
                    }
            );

        } catch (TimeoutException exception) {
            return false;
        }
    }

    private boolean waitForPossibleOrderCompletion() {
        try {
            WebDriverWait shortWait =
                    new WebDriverWait(
                            driver,
                            Duration.ofSeconds(12)
                    );

            return shortWait.until(
                    currentDriver ->
                            isVisible(orderSuccess)
                                    && !getOrderNumberText()
                                    .isEmpty()
            );

        } catch (TimeoutException exception) {
            return false;
        }
    }

    private String getOrderNumberText() {
        List<WebElement> numbers =
                driver.findElements(
                        orderNumber
                );

        if (numbers.isEmpty()) {
            return "";
        }

        try {
            return numbers.get(0)
                    .getText()
                    .trim();

        } catch (
                StaleElementReferenceException exception
        ) {
            return "";
        }
    }

    private String getSignInMessage() {
        List<WebElement> messages =
                driver.findElements(
                        signInMessage
                );

        if (messages.isEmpty()) {
            return "";
        }

        try {
            return messages.get(0)
                    .getText()
                    .trim();

        } catch (
                StaleElementReferenceException exception
        ) {
            return "";
        }
    }

    private boolean isUserLoggedIn() {
        List<WebElement> names =
                driver.findElements(
                        headerUsername
                );

        for (WebElement name : names) {
            try {
                if (name.isDisplayed()
                        && !name.getText()
                        .trim()
                        .isEmpty()) {
                    return true;
                }
            } catch (
                    StaleElementReferenceException ignored
            ) {
            }
        }

        return false;
    }

    private boolean isVisible(
            By locator
    ) {
        List<WebElement> elements =
                driver.findElements(
                        locator
                );

        for (WebElement element :
                elements) {
            try {
                if (element.isDisplayed()) {
                    return true;
                }
            } catch (
                    StaleElementReferenceException ignored
            ) {
            }
        }

        return false;
    }

    private boolean isLoaderVisible() {
        List<WebElement> loaders =
                driver.findElements(
                        mainLoader
                );

        for (WebElement currentLoader :
                loaders) {
            try {
                if (currentLoader.isDisplayed()) {
                    return true;
                }
            } catch (
                    StaleElementReferenceException ignored
            ) {
            }
        }

        return false;
    }

    private boolean isAngularReady() {
        Object result = js.executeScript(
                "return document.readyState === 'complete' " +
                        "&& !!document.querySelector('#menuUserLink');"
        );

        return Boolean.TRUE.equals(result);
    }

    private String getBodyText() {
        List<WebElement> bodyElements =
                driver.findElements(
                        By.tagName("body")
                );

        if (bodyElements.isEmpty()) {
            return "";
        }

        try {
            return bodyElements.get(0)
                    .getText();

        } catch (
                StaleElementReferenceException exception
        ) {
            return "";
        }
    }

    private void waitForLoader() {
        wait.until(currentDriver ->
                !isLoaderVisible()
        );
    }

    private void waitForPageReady() {
        wait.until(currentDriver ->
                "complete".equals(
                        js.executeScript(
                                "return document.readyState"
                        )
                )
        );
    }

    private void clickElement(
            WebElement element
    ) {
        waitForLoader();

        js.executeScript(
                "arguments[0].scrollIntoView(" +
                        "{block:'center',inline:'center'});",
                element
        );

        try {
            element.click();
        } catch (Exception exception) {
            js.executeScript(
                    "arguments[0].click();",
                    element
            );
        }
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod() {
        if (driver != null) {
            driver.quit();
        }
    }
}