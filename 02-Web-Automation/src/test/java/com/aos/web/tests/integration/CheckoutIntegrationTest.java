
package com.aos.web.tests.integration;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;
import java.util.Locale;

public class CheckoutIntegrationTest {

    private static final String BASE_URL =
            "https://www.advantageonlineshopping.com/";

    private static final String PRODUCT_URL =
            BASE_URL + "#/product/17";

    private static final String PRODUCT_NAME =
            "HP ELITE X2 1011 G1 TABLET";

    private static final String APP_USERNAME =
            System.getenv("AOS_USERNAME");

    private static final String APP_PASSWORD =
            System.getenv("AOS_PASSWORD");

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeMethod
    public void setUp() {

        driver = new ChromeDriver();

        wait = new WebDriverWait(
                driver,
                Duration.ofSeconds(40)
        );

        wait.ignoring(StaleElementReferenceException.class);

        driver.manage().window().maximize();
        driver.get(BASE_URL);

        waitForAngularPage();
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {

        if (driver != null) {
            driver.quit();
        }
    }

    // TC1: Complete order flow using saved MasterCredit card.
    @Test
    public void verifyCompleteOrderFlowUsingMasterCredit() {

        requireAppCredentials();

        loginToApplicationIfNeeded();
        addProductToCart();
        openOrderPayment();

        ensureShippingStepAvailable();
        continueToPaymentStep();

        payWithMasterCredit();
        verifyOrderSuccess();
    }

    // TC2: Guest can login during checkout.
    @Test
    public void verifyGuestCanLoginDuringCheckout() {

        requireAppCredentials();

        addProductToCart();
        openOrderPayment();

        By checkoutUsername =
                By.name("usernameInOrderPayment");

        By checkoutPassword =
                By.name("passwordInOrderPayment");

        if (!isDisplayed(checkoutUsername)) {

            Assert.assertTrue(
                    isDisplayed(By.id("next_btn")),
                    "Neither checkout login fields nor Shipping Next button appeared."
            );

            return;
        }

        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        checkoutUsername
                )
        ).sendKeys(APP_USERNAME);

        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        checkoutPassword
                )
        ).sendKeys(APP_PASSWORD);

        wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.id("login_btn")
                )
        ).click();

        wait.until(d ->
                !isDisplayed(checkoutUsername)
                        || isDisplayed(By.id("next_btn"))
        );

        Assert.assertTrue(
                isDisplayed(By.id("next_btn")),
                "Shipping details should appear after login."
        );
    }

    // TC3: Shopping Cart total matches checkout total.
    @Test
    public void verifyShoppingCartTotalMatchesOrderPaymentTotal() {

        addProductToCart();

        driver.get(BASE_URL + "#/shoppingCart");

        waitForCartProduct();

        String cartTotal = readDisplayedTotal();

        Assert.assertTrue(
                driver.findElement(By.id("shoppingCart"))
                        .getText()
                        .contains(PRODUCT_NAME),
                "Expected product is not displayed in Shopping Cart."
        );

        openOrderPayment();

        String checkoutTotal = readDisplayedTotal();

        Assert.assertEquals(
                normalizePrice(checkoutTotal),
                normalizePrice(cartTotal),
                "Shopping Cart total and checkout total do not match."
        );
    }

    // TC4: Completed order appears in My Orders.
    @Test
    public void verifyCompletedOrderAppearsInMyOrders() {

        requireAppCredentials();

        loginToApplicationIfNeeded();
        addProductToCart();
        openOrderPayment();

        ensureShippingStepAvailable();
        continueToPaymentStep();

        payWithMasterCredit();

        String orderNumber = verifyOrderSuccess();

        openMyOrders();

        WebElement ordersArea = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.id("myAccountContainer")
                )
        );

        wait.until(d ->
                d.findElement(By.id("myAccountContainer"))
                        .getText()
                        .contains(orderNumber)
        );

        Assert.assertTrue(
                ordersArea.getText().contains(orderNumber),
                "The completed order was not found in My Orders."
        );
    }

    // TC5: Cart is cleared after successful payment.
    @Test
    public void verifyCartIsClearedAfterSuccessfulPayment() {

        requireAppCredentials();

        loginToApplicationIfNeeded();
        addProductToCart();
        openOrderPayment();

        ensureShippingStepAvailable();
        continueToPaymentStep();

        payWithMasterCredit();
        verifyOrderSuccess();

        driver.get(BASE_URL + "#/shoppingCart");

        WebElement emptyCart = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("#shoppingCart .bigEmptyCart")
                )
        );

        Assert.assertTrue(
                emptyCart.getText()
                        .toLowerCase(Locale.ROOT)
                        .contains("empty"),
                "Shopping Cart should be empty after successful payment."
        );
    }

    // Open My Orders from the user menu.
    private void openMyOrders() {

        waitForLoaderToDisappear();

        WebElement userMenu = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.id("menuUserLink")
                )
        );

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].click();",
                userMenu
        );

        By myOrdersText = By.xpath(
                "//*[translate(normalize-space(.),"
                        + "'ABCDEFGHIJKLMNOPQRSTUVWXYZ',"
                        + "'abcdefghijklmnopqrstuvwxyz')='my orders']"
        );

        WebElement ordersElement = wait.until(d -> {

            List<WebElement> elements =
                    d.findElements(myOrdersText);

            for (WebElement element : elements) {

                try {

                    if (!element.isDisplayed()) {
                        continue;
                    }

                    WebElement clickable = (WebElement)
                            ((JavascriptExecutor) d).executeScript(
                                    "return arguments[0].closest("
                                            + "'a, button, [ng-click], "
                                            + "[data-ng-click], "
                                            + "[role=menuitem], "
                                            + "[role=button], li'"
                                            + ") || arguments[0];",
                                    element
                            );

                    if (clickable != null
                            && clickable.isDisplayed()
                            && clickable.getSize().getHeight() > 0) {

                        return clickable;
                    }

                } catch (StaleElementReferenceException ignored) {
                    // Angular menu is refreshing.
                }
            }

            return null;
        });

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'});",
                ordersElement
        );

        try {

            ordersElement.click();

        } catch (org.openqa.selenium.ElementNotInteractableException e) {

            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].click();",
                    ordersElement
            );
        }

        wait.until(d ->
                d.getCurrentUrl().toLowerCase(Locale.ROOT)
                        .contains("myaccount")
                        || isDisplayed(By.id("myAccountContainer"))
        );
    }

    // Wait for Angular page loading.
    private void waitForAngularPage() {

        wait.until(d ->
                "complete".equals(
                        ((JavascriptExecutor) d)
                                .executeScript("return document.readyState")
                )
        );

        wait.until(d ->
                !d.findElements(By.tagName("body")).isEmpty()
        );
    }

    // Wait until the loader disappears.
    private void waitForLoaderToDisappear() {

        wait.until(
                ExpectedConditions.invisibilityOfElementLocated(
                        By.cssSelector(".loader")
                )
        );
    }

    // Add product to Shopping Cart.
    private void addProductToCart() {

        driver.get(PRODUCT_URL);

        waitForAngularPage();

        wait.until(d ->
                d.findElement(By.tagName("body"))
                        .getText()
                        .contains(PRODUCT_NAME)
        );

        By addToCart = By.name("save_to_cart");

        WebElement addButton = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        addToCart
                )
        );

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'});",
                addButton
        );

        waitForLoaderToDisappear();

        wait.until(
                ExpectedConditions.elementToBeClickable(addToCart)
        ).click();

        driver.get(BASE_URL + "#/shoppingCart");

        waitForCartProduct();
    }

    // Open checkout page.
    private void openOrderPayment() {

        driver.get(BASE_URL + "#/shoppingCart");

        waitForCartProduct();

        By checkoutButton = By.cssSelector(
                "button#checkOutButton, "
                        + "button#checkOutPopUp, "
                        + "button[name='check_out_btn']"
        );

        WebElement button = wait.until(d -> {

            List<WebElement> candidates =
                    d.findElements(checkoutButton);

            for (WebElement candidate : candidates) {

                if (candidate.isDisplayed()
                        && candidate.isEnabled()) {
                    return candidate;
                }
            }

            return null;
        });

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'});",
                button
        );

        wait.until(
                ExpectedConditions.elementToBeClickable(button)
        ).click();

        wait.until(d ->
                d.getCurrentUrl().contains("orderPayment")
                        || isDisplayed(By.id("next_btn"))
                        || isDisplayed(By.name("usernameInOrderPayment"))
        );
    }

    // Ensure shipping step is available.
    private void ensureShippingStepAvailable() {

        By checkoutUsername =
                By.name("usernameInOrderPayment");

        if (isDisplayed(checkoutUsername)) {

            requireAppCredentials();

            wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            checkoutUsername
                    )
            ).sendKeys(APP_USERNAME);

            wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            By.name("passwordInOrderPayment")
                    )
            ).sendKeys(APP_PASSWORD);

            wait.until(
                    ExpectedConditions.elementToBeClickable(
                            By.id("login_btn")
                    )
            ).click();
        }

        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.id("next_btn")
                )
        );
    }

    // Continue to payment step.
    private void continueToPaymentStep() {

        wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.id("next_btn")
                )
        ).click();

        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.id("paymentMethod")
                )
        );
    }

    // Select MasterCredit and pay using the saved card.
    private void payWithMasterCredit() {

        By masterCreditRadio = By.name("masterCredit");

        WebElement radio = wait.until(
                ExpectedConditions.presenceOfElementLocated(
                        masterCreditRadio
                )
        );

        if (!radio.isSelected()) {

            By masterCreditOption = By.xpath(
                    "//*[@id='paymentMethod']"
                            + "//div[contains(@class,'imgRadioButton')]"
                            + "[.//input[@name='masterCredit']]"
            );

            wait.until(
                    ExpectedConditions.elementToBeClickable(
                            masterCreditOption
                    )
            ).click();
        }

        wait.until(d ->
                d.findElement(masterCreditRadio).isSelected()
        );

        By savedCardPayButton =
                By.id("pay_now_btn_MasterCredit");

        WebElement payButton = wait.until(
                ExpectedConditions.elementToBeClickable(
                        savedCardPayButton
                )
        );

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'});",
                payButton
        );

        waitForLoaderToDisappear();

        wait.until(
                ExpectedConditions.elementToBeClickable(
                        savedCardPayButton
                )
        ).click();

        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.id("orderPaymentSuccess")
                )
        );
    }

    // Verify successful order and return order number.
    private String verifyOrderSuccess() {

        WebElement success = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.id("orderPaymentSuccess")
                )
        );

        Assert.assertTrue(
                success.getText()
                        .toLowerCase(Locale.ROOT)
                        .contains("thank you"),
                "Order confirmation message was not displayed."
        );

        String orderNumber = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.id("orderNumberLabel")
                )
        ).getText().trim();

        Assert.assertFalse(
                orderNumber.isEmpty(),
                "Order number should be displayed."
        );

        return orderNumber;
    }

    // Wait for product in Shopping Cart.
    private void waitForCartProduct() {

        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.id("shoppingCart")
                )
        );

        wait.until(d ->
                d.findElement(By.id("shoppingCart"))
                        .getText()
                        .contains(PRODUCT_NAME)
        );
    }

    // Read displayed total.
    private String readDisplayedTotal() {

        By[] totalLocators = {

                By.cssSelector("#shoppingCart .cart-total"),
                By.cssSelector("#userCart .totalValue"),
                By.cssSelector(".cart-total"),
                By.cssSelector(".totalValue")
        };

        for (By locator : totalLocators) {

            List<WebElement> totals =
                    driver.findElements(locator);

            for (WebElement total : totals) {

                if (total.isDisplayed()
                        && !total.getText().trim().isEmpty()) {

                    return total.getText().trim();
                }
            }
        }

        throw new NoSuchElementException(
                "Could not find a visible cart/checkout total."
        );
    }

    // Normalize price before comparison.
    private String normalizePrice(String price) {

        return price.replaceAll("[^0-9.]", "");
    }

    // Check if element is displayed.
    private boolean isDisplayed(By locator) {

        for (WebElement element : driver.findElements(locator)) {

            try {

                if (element.isDisplayed()) {
                    return true;
                }

            } catch (StaleElementReferenceException ignored) {
                // Angular view is refreshing.
            }
        }

        return false;
    }

    // Login to application if not already signed in.
    private void loginToApplicationIfNeeded() {

        driver.get(BASE_URL + "#/");

        waitForAngularPage();

        By userMenu = By.id("menuUserLink");

        By signedInGreeting = By.cssSelector(
                "#menuUserLink .hi-user:not(.ng-hide)"
        );

        wait.until(
                ExpectedConditions.presenceOfElementLocated(userMenu)
        );

        if (isDisplayed(signedInGreeting)) {

            String greeting = driver.findElement(
                    signedInGreeting
            ).getText().trim();

            if (!greeting.isEmpty()) {
                return;
            }
        }

        By usernameField = By.name("username");

        if (!isDisplayed(usernameField)) {

            waitForLoaderToDisappear();

            wait.until(
                    ExpectedConditions.elementToBeClickable(userMenu)
            ).click();
        }

        WebElement username = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        usernameField
                )
        );

        requireAppCredentials();

        username.clear();
        username.sendKeys(APP_USERNAME);

        WebElement password = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.name("password")
                )
        );

        password.clear();
        password.sendKeys(APP_PASSWORD);

        wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.id("sign_in_btn")
                )
        ).click();

        wait.until(d ->
                isDisplayed(signedInGreeting)
                        && !d.findElement(signedInGreeting)
                        .getText().trim().isEmpty()
        );
    }

    // Validate application credentials.
    private void requireAppCredentials() {

        Assert.assertNotNull(
                APP_USERNAME,
                "Set AOS_USERNAME in IntelliJ Run Configuration environment variables."
        );

        Assert.assertNotNull(
                APP_PASSWORD,
                "Set AOS_PASSWORD in IntelliJ Run Configuration environment variables."
        );

        Assert.assertFalse(
                APP_USERNAME.isBlank(),
                "AOS_USERNAME is empty."
        );

        Assert.assertFalse(
                APP_PASSWORD.isBlank(),
                "AOS_PASSWORD is empty."
        );
    }
}
