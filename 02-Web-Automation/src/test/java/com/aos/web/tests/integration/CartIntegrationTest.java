package com.aos.web.tests.integration;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;

public class CartIntegrationTest {

    WebDriver driver;
    WebDriverWait wait;

    @BeforeClass
    public void beforeClass() {
        System.setProperty(
                "webdriver.chrome.driver",
                "C:\\chromedriver-win64\\chromedriver.exe"
        );

        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(40));
    }

    @BeforeMethod
    public void beforeMethod() throws InterruptedException {
        clearCart();
    }

    @Test(priority = 1)
    public void testCase1() throws InterruptedException {
        addProductToCart(17, 1);

        openMiniCart();

        WebElement miniCartRow = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(
                                "ul.desktop-handler #shoppingCartLink ~ ul " +
                                        "tool-tip-cart tbody tr:first-child"
                        )
                )
        );

        String miniCartProductName = miniCartRow.findElement(
                By.cssSelector("td:nth-child(2) h3")
        ).getText().trim();

        String miniCartQuantity = miniCartRow.findElement(
                        By.cssSelector(
                                "td:nth-child(2) > a > label:first-of-type"
                        )
                ).getText()
                .replace("QTY:", "")
                .trim();

        String miniCartColor = miniCartRow.findElement(
                By.cssSelector(
                        "td:nth-child(2) > a > label:nth-of-type(2) span"
                )
        ).getText().trim();

        String miniCartPrice = miniCartRow.findElement(
                By.cssSelector("p.price")
        ).getText().trim();

        driver.navigate().to(
                "https://www.advantageonlineshopping.com/#/shoppingCart"
        );

        waitForLoader();

        WebElement cartRow = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(
                                "#shoppingCart table tbody tr:first-child"
                        )
                )
        );

        String cartProductName = cartRow.findElement(
                By.cssSelector("label.productName")
        ).getText().trim();

        String cartQuantity = cartRow.findElement(
                By.cssSelector(
                        "td.quantityMobile > label.ng-binding"
                )
        ).getText().trim();

        String cartColor = cartRow.findElement(
                By.cssSelector("span.productColor")
        ).getAttribute("title");

        String cartPrice = cartRow.findElement(
                By.cssSelector("p.price")
        ).getText().trim();

        Assert.assertEquals(
                cartProductName,
                miniCartProductName,
                "The product name should be consistent."
        );

        Assert.assertEquals(
                cartQuantity,
                miniCartQuantity,
                "The product quantity should be consistent."
        );

        Assert.assertEquals(
                cartColor,
                miniCartColor,
                "The product color should be consistent."
        );

        Assert.assertEquals(
                cartPrice,
                miniCartPrice,
                "The product price should be consistent."
        );
    }

    @Test(priority = 2)
    public void testCase2() throws InterruptedException {
        driver.navigate().to(
                "https://www.advantageonlineshopping.com/#/product/17"
        );

        waitForLoader();

        WebElement quantityInput = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(
                                "#productProperties input[name='quantity']"
                        )
                )
        );

        String initialValue = quantityInput.getAttribute("value");

        Assert.assertNotNull(
                initialValue,
                "The quantity field should have an initial value."
        );

        int currentQuantity = Integer.parseInt(initialValue.trim());

        WebElement plusButton = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector(
                                "#productProperties .e-sec-plus-minus .plus"
                        )
                )
        );

        while (currentQuantity < 3) {
            plusButton.click();
            currentQuantity++;

            int expectedQuantity = currentQuantity;

            wait.until(currentDriver -> {
                String value = currentDriver.findElement(
                        By.cssSelector(
                                "#productProperties input[name='quantity']"
                        )
                ).getAttribute("value");

                return value != null
                        && value.trim().equals(
                        String.valueOf(expectedQuantity)
                );
            });
        }

        Assert.assertEquals(
                quantityInput.getAttribute("value"),
                "3",
                "The selected product quantity should be 3."
        );

        WebElement addToCartButton = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector(
                                "#productProperties button[name='save_to_cart']"
                        )
                )
        );

        scrollToElement(addToCartButton);
        waitForLoader();
        addToCartButton.click();

        WebElement cartCounter = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("#shoppingCartLink span.cart")
                )
        );

        wait.until(currentDriver -> {
            String counterValue = currentDriver.findElement(
                    By.cssSelector("#shoppingCartLink span.cart")
            ).getText().trim();

            return counterValue.equals("3");
        });

        Assert.assertEquals(
                cartCounter.getText().trim(),
                "3",
                "The cart counter should display the selected quantity."
        );

        openMiniCart();

        WebElement miniCartQuantity = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(
                                "ul.desktop-handler #shoppingCartLink ~ ul " +
                                        "tool-tip-cart tbody tr:first-child " +
                                        "td:nth-child(2) > a > label:first-of-type"
                        )
                )
        );

        Assert.assertEquals(
                miniCartQuantity.getText()
                        .replace("QTY:", "")
                        .trim(),
                "3",
                "The Mini-Cart should display quantity 3."
        );

        driver.navigate().to(
                "https://www.advantageonlineshopping.com/#/shoppingCart"
        );

        waitForLoader();

        WebElement cartQuantity = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(
                                "#shoppingCart table tbody tr:first-child " +
                                        "td.quantityMobile > label.ng-binding"
                        )
                )
        );

        Assert.assertEquals(
                cartQuantity.getText().trim(),
                "3",
                "The Shopping Cart page should display quantity 3."
        );
    }

    @Test(priority = 3)
    public void testCase3() throws InterruptedException {
        addProductToCart(17, 1);

        driver.navigate().to(
                "https://www.advantageonlineshopping.com/#/shoppingCart"
        );

        waitForLoader();

        WebElement productRow = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(
                                "#shoppingCart table tbody tr:first-child"
                        )
                )
        );

        int rowsBeforeEdit = driver.findElements(
                By.cssSelector("#shoppingCart table tbody tr")
        ).size();

        String productName = productRow.findElement(
                By.cssSelector("label.productName")
        ).getText().trim();

        int originalQuantity = Integer.parseInt(
                productRow.findElement(
                        By.cssSelector(
                                "td.quantityMobile > label.ng-binding"
                        )
                ).getText().trim()
        );

        WebElement editLink = productRow.findElement(
                By.cssSelector("a.edit[href*='pageState=edit']")
        );

        editLink.click();

        wait.until(
                ExpectedConditions.urlContains("pageState=edit")
        );

        waitForLoader();

        WebElement quantityInput = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(
                                "#productProperties input[name='quantity']"
                        )
                )
        );

        String originalInputValue = quantityInput.getAttribute("value");

        Assert.assertNotNull(
                originalInputValue,
                "The quantity input should have a value."
        );

        Assert.assertEquals(
                Integer.parseInt(originalInputValue.trim()),
                originalQuantity,
                "The edit page should display the current cart quantity."
        );

        WebElement plusButton = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector(
                                "#productProperties .e-sec-plus-minus .plus"
                        )
                )
        );

        plusButton.click();

        int expectedQuantity = originalQuantity + 1;

        wait.until(currentDriver -> {
            String value = currentDriver.findElement(
                    By.cssSelector(
                            "#productProperties input[name='quantity']"
                    )
            ).getAttribute("value");

            return value != null
                    && value.trim().equals(
                    String.valueOf(expectedQuantity)
            );
        });

        WebElement addToCartButton = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector(
                                "#productProperties button[name='save_to_cart']"
                        )
                )
        );

        scrollToElement(addToCartButton);
        waitForLoader();
        addToCartButton.click();

        wait.until(currentDriver -> {
            List<WebElement> cartCounters =
                    currentDriver.findElements(
                            By.cssSelector(
                                    "#shoppingCartLink span.cart"
                            )
                    );

            if (cartCounters.isEmpty()) {
                return false;
            }

            return cartCounters.get(0)
                    .getText()
                    .trim()
                    .equals(String.valueOf(expectedQuantity));
        });

        openMiniCart();

        WebElement miniCartRow = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(
                                "ul.desktop-handler " +
                                        "#shoppingCartLink ~ ul " +
                                        "tool-tip-cart tbody tr:first-child"
                        )
                )
        );

        String miniCartProductName = miniCartRow.findElement(
                By.cssSelector("td:nth-child(2) h3")
        ).getText().trim();

        WebElement miniCartQuantityElement = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath(
                                "//ul[contains(@class,'desktop-handler')]" +
                                        "//tool-tip-cart" +
                                        "//tbody/tr[1]" +
                                        "//td[2]//label[contains(normalize-space(.),'QTY:')]"
                        )
                )
        );

        wait.until(currentDriver ->
                !miniCartQuantityElement.getText().trim().isEmpty()
        );

        String miniCartQuantity = miniCartQuantityElement
                .getText()
                .replace("QTY:", "")
                .trim();

        Assert.assertEquals(
                miniCartProductName,
                productName,
                "The Mini-Cart should display the edited product."
        );

        Assert.assertEquals(
                miniCartQuantity,
                String.valueOf(expectedQuantity),
                "The Mini-Cart should display the updated quantity."
        );

        WebElement miniCartTotalElement = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(
                                "ul.desktop-handler " +
                                        "#shoppingCartLink ~ ul " +
                                        "tool-tip-cart span.cart-total"
                        )
                )
        );

        wait.until(currentDriver ->
                !miniCartTotalElement.getText().trim().isEmpty()
        );

        String miniCartTotal =
                miniCartTotalElement.getText().trim();

        driver.navigate().to(
                "https://www.advantageonlineshopping.com/#/shoppingCart"
        );

        waitForLoader();

        List<WebElement> rowsAfterEdit = wait.until(
                ExpectedConditions.visibilityOfAllElementsLocatedBy(
                        By.cssSelector(
                                "#shoppingCart table tbody tr"
                        )
                )
        );

        Assert.assertEquals(
                rowsAfterEdit.size(),
                rowsBeforeEdit,
                "Editing an existing product should not create a duplicate row."
        );

        WebElement updatedProductRow = null;
        int matchingProducts = 0;

        for (WebElement row : rowsAfterEdit) {
            String currentProductName = row.findElement(
                    By.cssSelector("label.productName")
            ).getText().trim();

            if (currentProductName.equals(productName)) {
                updatedProductRow = row;
                matchingProducts++;
            }
        }

        Assert.assertEquals(
                matchingProducts,
                1,
                "The edited product should appear only once in the Shopping Cart."
        );

        Assert.assertNotNull(
                updatedProductRow,
                "The edited product should remain in the Shopping Cart."
        );

        int shoppingCartQuantity = Integer.parseInt(
                updatedProductRow.findElement(
                        By.cssSelector(
                                "td.quantityMobile > label.ng-binding"
                        )
                ).getText().trim()
        );

        Assert.assertEquals(
                shoppingCartQuantity,
                expectedQuantity,
                "The Shopping Cart should display the updated quantity."
        );

        String shoppingCartTotal = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(
                                "#shoppingCart table tfoot " +
                                        "tr:first-child td:last-child " +
                                        "span.roboto-medium"
                        )
                )
        ).getText().trim();

        Assert.assertEquals(
                shoppingCartTotal,
                miniCartTotal,
                "The Mini-Cart and Shopping Cart totals should match."
        );
    }
    @Test(priority = 4)
    public void testCase4() throws InterruptedException {
        addProductToCart(17, 1);
        addProductToCart(10, 1);

        openMiniCart();

        List<WebElement> miniCartRows = wait.until(
                ExpectedConditions.visibilityOfAllElementsLocatedBy(
                        By.cssSelector(
                                "ul.desktop-handler #shoppingCartLink ~ ul " +
                                        "tool-tip-cart tbody tr"
                        )
                )
        );

        Assert.assertEquals(
                miniCartRows.size(),
                2,
                "The Mini-Cart should contain two products."
        );

        String removedProductName = miniCartRows.get(0).findElement(
                By.cssSelector("td:nth-child(2) h3")
        ).getText().trim();

        int rowsBeforeRemoval = miniCartRows.size();

        WebElement removeButton = miniCartRows.get(0).findElement(
                By.cssSelector("div.removeProduct")
        );

        removeButton.click();

        wait.until(currentDriver ->
                currentDriver.findElements(
                        By.cssSelector(
                                "ul.desktop-handler #shoppingCartLink ~ ul " +
                                        "tool-tip-cart tbody tr"
                        )
                ).size() < rowsBeforeRemoval
        );

        driver.navigate().to(
                "https://www.advantageonlineshopping.com/#/shoppingCart"
        );

        waitForLoader();

        List<WebElement> cartRows = wait.until(
                ExpectedConditions.visibilityOfAllElementsLocatedBy(
                        By.cssSelector("#shoppingCart table tbody tr")
                )
        );

        Assert.assertEquals(
                cartRows.size(),
                1,
                "The Shopping Cart should contain one product after removal."
        );

        for (WebElement row : cartRows) {
            String currentProductName = row.findElement(
                    By.cssSelector("label.productName")
            ).getText().trim();

            Assert.assertNotEquals(
                    currentProductName,
                    removedProductName,
                    "The removed Mini-Cart product should not appear in the Shopping Cart."
            );
        }

        int cartCounter = Integer.parseInt(
                wait.until(
                        ExpectedConditions.visibilityOfElementLocated(
                                By.cssSelector("#shoppingCartLink span.cart")
                        )
                ).getText().trim()
        );

        int cartQuantity = getShoppingCartQuantity();

        Assert.assertEquals(
                cartCounter,
                cartQuantity,
                "The cart counter should match the remaining quantity."
        );

        String shoppingCartTotal = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(
                                "#shoppingCart table tfoot " +
                                        "tr:first-child td:last-child " +
                                        "span.roboto-medium"
                        )
                )
        ).getText().trim();

        String miniCartTotal = getMiniCartTotal();

        Assert.assertEquals(
                miniCartTotal,
                shoppingCartTotal,
                "The Mini-Cart and Shopping Cart totals should match."
        );
    }

    @Test(priority = 5)
    public void testCase5() throws InterruptedException {
        addProductToCart(17, 1);
        addProductToCart(10, 1);

        driver.navigate().to(
                "https://www.advantageonlineshopping.com/#/shoppingCart"
        );

        waitForLoader();

        List<WebElement> cartRows = wait.until(
                ExpectedConditions.visibilityOfAllElementsLocatedBy(
                        By.cssSelector("#shoppingCart table tbody tr")
                )
        );

        Assert.assertEquals(
                cartRows.size(),
                2,
                "The Shopping Cart should contain two products."
        );

        String removedProductName = cartRows.get(0).findElement(
                By.cssSelector("label.productName")
        ).getText().trim();

        cartRows.get(0)
                .findElement(By.cssSelector("a.remove"))
                .click();

        wait.until(currentDriver ->
                currentDriver.findElements(
                        By.cssSelector(
                                "#shoppingCart table tbody tr"
                        )
                ).size() == 1
        );

        int remainingQuantity = getShoppingCartQuantity();

        String shoppingCartTotal = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(
                                "#shoppingCart table tfoot " +
                                        "tr:first-child td:last-child " +
                                        "span.roboto-medium"
                        )
                )
        ).getText().trim();

        openMiniCart();

        List<WebElement> miniCartRows = wait.until(
                ExpectedConditions.visibilityOfAllElementsLocatedBy(
                        By.cssSelector(
                                "ul.desktop-handler #shoppingCartLink ~ ul " +
                                        "tool-tip-cart tbody tr"
                        )
                )
        );

        Assert.assertEquals(
                miniCartRows.size(),
                1,
                "The Mini-Cart should contain one remaining product."
        );

        for (WebElement row : miniCartRows) {
            String currentProductName = row.findElement(
                    By.cssSelector("td:nth-child(2) h3")
            ).getText().trim();

            Assert.assertNotEquals(
                    currentProductName,
                    removedProductName,
                    "The removed Shopping Cart product should not appear in the Mini-Cart."
            );
        }

        int cartCounter = Integer.parseInt(
                wait.until(
                        ExpectedConditions.visibilityOfElementLocated(
                                By.cssSelector("#shoppingCartLink span.cart")
                        )
                ).getText().trim()
        );

        Assert.assertEquals(
                cartCounter,
                remainingQuantity,
                "The cart counter should match the remaining quantity."
        );

        String miniCartTotal = getMiniCartTotal();

        Assert.assertEquals(
                miniCartTotal,
                shoppingCartTotal,
                "The Mini-Cart and Shopping Cart totals should match after removal."
        );
    }

    @Test(priority = 6)
    public void testCase6() throws InterruptedException {
        addProductToCart(17, 1);

        driver.navigate().to(
                "https://www.advantageonlineshopping.com/#/shoppingCart"
        );

        waitForLoader();

        List<WebElement> cartRows = wait.until(
                ExpectedConditions.visibilityOfAllElementsLocatedBy(
                        By.cssSelector("#shoppingCart table tbody tr")
                )
        );

        int cartQuantity = getShoppingCartQuantity();

        String cartTotal = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(
                                "#shoppingCart table tfoot " +
                                        "tr:first-child td:last-child " +
                                        "span.roboto-medium"
                        )
                )
        ).getText().trim();

        WebElement checkoutButton = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector("#checkOutButton")
                )
        );

        Assert.assertTrue(
                checkoutButton.getText()
                        .replaceAll("\\s+", " ")
                        .contains(cartTotal),
                "The CHECKOUT button should display the cart total."
        );

        checkoutButton.click();

        waitForLoader();

        WebElement orderPayment = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("#orderPayment")
                )
        );

        WebElement orderSummary = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("#userCart")
                )
        );

        String orderItemsCount = orderSummary.findElement(
                By.cssSelector("span.itemsCount")
        ).getText().trim();

        String orderTotal = orderSummary.findElement(
                By.cssSelector("span.totalValue")
        ).getText().trim();

        List<WebElement> orderRows = orderSummary.findElements(
                By.cssSelector("tool-tip-cart tbody tr")
        );

        Assert.assertTrue(
                driver.getCurrentUrl().contains("#/login"),
                "Guest checkout should navigate to the login route."
        );

        Assert.assertTrue(
                orderPayment.isDisplayed(),
                "The ORDER PAYMENT section should be displayed."
        );

        Assert.assertEquals(
                orderRows.size(),
                cartRows.size(),
                "The Order Summary should contain the same product rows as the Shopping Cart."
        );

        Assert.assertEquals(
                orderItemsCount,
                cartQuantity + (cartQuantity == 1 ? " ITEM" : " ITEMS"),
                "The Order Summary should display the cart quantity."
        );

        Assert.assertEquals(
                orderTotal,
                cartTotal,
                "The Order Summary total should match the Shopping Cart total."
        );

        Assert.assertTrue(
                driver.findElement(
                        By.cssSelector(
                                "#orderPayment " +
                                        "input[name='usernameInOrderPayment']"
                        )
                ).isDisplayed(),
                "The Username field should be displayed."
        );

        Assert.assertTrue(
                driver.findElement(
                        By.cssSelector(
                                "#orderPayment " +
                                        "input[name='passwordInOrderPayment']"
                        )
                ).isDisplayed(),
                "The Password field should be displayed."
        );

        Assert.assertTrue(
                driver.findElement(
                        By.cssSelector("#registration_btn")
                ).isDisplayed(),
                "The REGISTRATION button should be displayed."
        );
    }

    private void addProductToCart(int productId, int quantity)
            throws InterruptedException {
        driver.navigate().to(
                "https://www.advantageonlineshopping.com/#/product/"
                        + productId
        );

        waitForLoader();

        WebElement quantityInput = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(
                                "#productProperties input[name='quantity']"
                        )
                )
        );

        quantityInput.clear();
        quantityInput.sendKeys(String.valueOf(quantity));

        WebElement addToCartButton = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector(
                                "#productProperties " +
                                        "button[name='save_to_cart']"
                        )
                )
        );

        scrollToElement(addToCartButton);
        waitForLoader();
        addToCartButton.click();

        wait.until(currentDriver -> {
            List<WebElement> counters = currentDriver.findElements(
                    By.cssSelector("#shoppingCartLink span.cart")
            );

            return !counters.isEmpty()
                    && !counters.get(0)
                    .getText()
                    .trim()
                    .isEmpty();
        });

        Thread.sleep(500);
    }

    private void clearCart() throws InterruptedException {
        driver.navigate().to(
                "https://www.advantageonlineshopping.com/#/shoppingCart"
        );

        waitForLoader();

        List<WebElement> removeLinks = driver.findElements(
                By.cssSelector("#shoppingCart table tbody a.remove")
        );

        while (!removeLinks.isEmpty()) {
            int numberOfProducts = removeLinks.size();

            scrollToElement(removeLinks.get(0));

            wait.until(
                    ExpectedConditions.elementToBeClickable(
                            removeLinks.get(0)
                    )
            ).click();

            wait.until(currentDriver ->
                    currentDriver.findElements(
                            By.cssSelector(
                                    "#shoppingCart table tbody a.remove"
                            )
                    ).size() < numberOfProducts
            );

            removeLinks = driver.findElements(
                    By.cssSelector(
                            "#shoppingCart table tbody a.remove"
                    )
            );
        }

        Thread.sleep(500);
    }

    private void openMiniCart() {
        WebElement shoppingCartLink = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("#shoppingCartLink")
                )
        );

        new Actions(driver)
                .moveToElement(shoppingCartLink)
                .perform();

        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(
                                "ul.desktop-handler #shoppingCartLink ~ ul " +
                                        "tool-tip-cart table"
                        )
                )
        );
    }

    private int getShoppingCartQuantity() {
        List<WebElement> quantityElements = driver.findElements(
                By.cssSelector(
                        "#shoppingCart table tbody " +
                                "td.quantityMobile > label.ng-binding"
                )
        );

        int totalQuantity = 0;

        for (WebElement quantityElement : quantityElements) {
            totalQuantity += Integer.parseInt(
                    quantityElement.getText().trim()
            );
        }

        return totalQuantity;
    }

    private String getMiniCartTotal() {
        openMiniCart();

        WebElement miniCartTotal = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(
                                "ul.desktop-handler #shoppingCartLink ~ ul " +
                                        "tool-tip-cart span.cart-total"
                        )
                )
        );

        wait.until(currentDriver ->
                !miniCartTotal.getText().trim().isEmpty()
        );

        return miniCartTotal.getText().trim();
    }

    private void waitForLoader() {
        wait.until(
                ExpectedConditions.invisibilityOfElementLocated(
                        By.cssSelector("body > div.loader")
                )
        );
    }

    private void scrollToElement(WebElement element) {
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'});",
                element
        );
    }

    @AfterMethod
    public void afterMethod() {
    }

    @AfterClass
    public void afterClass() {
        if (driver != null) {
            driver.quit();
        }
    }
}