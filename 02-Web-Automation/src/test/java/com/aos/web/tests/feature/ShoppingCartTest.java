package com.aos.web.tests.feature;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
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
import org.testng.asserts.SoftAssert;

import java.time.Duration;
import java.util.List;

public class ShoppingCartTest {

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
        driver.navigate().to(
                "https://www.advantageonlineshopping.com/#/product/17"
        );

        wait.until(
                ExpectedConditions.presenceOfElementLocated(
                        By.cssSelector("button[name='save_to_cart']")
                )
        );

        wait.until(
                ExpectedConditions.invisibilityOfElementLocated(
                        By.cssSelector("body > div.loader")
                )
        );

        WebElement addToCartButton = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("button[name='save_to_cart']")
                )
        );

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'});",
                addToCartButton
        );

        wait.until(
                ExpectedConditions.invisibilityOfElementLocated(
                        By.cssSelector("body > div.loader")
                )
        );

        wait.until(
                ExpectedConditions.elementToBeClickable(
                        addToCartButton
                )
        ).click();

        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("#shoppingCartLink span.cart")
                )
        );

        wait.until(currentDriver -> {
            String cartCount = currentDriver.findElement(
                    By.cssSelector("#shoppingCartLink span.cart")
            ).getText().trim();

            return !cartCount.isEmpty()
                    && Integer.parseInt(cartCount) > 0;
        });

        driver.navigate().to(
                "https://www.advantageonlineshopping.com/#/shoppingCart"
        );

        wait.until(
                ExpectedConditions.invisibilityOfElementLocated(
                        By.cssSelector("body > div.loader")
                )
        );

        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("#shoppingCart table tbody tr")
                )
        );

        Thread.sleep(1000);
    }

    @Test(priority = 1)
    public void testCase1() throws InterruptedException {
        Thread.sleep(1000);

        WebElement productRow = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("#shoppingCart table tbody tr:first-child")
                )
        );

        WebElement productImage = productRow.findElement(
                By.cssSelector("img.imageUrl")
        );

        WebElement productName = productRow.findElement(
                By.cssSelector("label.productName")
        );

        WebElement productColor = productRow.findElement(
                By.cssSelector("span.productColor[title]")
        );

        WebElement quantity = productRow.findElement(
                By.cssSelector("td.quantityMobile > label.ng-binding")
        );

        WebElement price = productRow.findElement(
                By.cssSelector("p.price")
        );

        WebElement editLink = productRow.findElement(
                By.cssSelector("a.edit[href*='pageState=edit']")
        );

        WebElement removeLink = productRow.findElement(
                By.cssSelector("a.remove")
        );

        Assert.assertTrue(
                productImage.isDisplayed(),
                "The product image should be displayed."
        );

        Assert.assertFalse(
                productImage.getAttribute("src").isBlank(),
                "The product image source should not be empty."
        );

        Assert.assertTrue(
                productName.isDisplayed(),
                "The product name should be displayed."
        );

        Assert.assertFalse(
                productName.getText().trim().isEmpty(),
                "The product name should not be empty."
        );

        Assert.assertTrue(
                productColor.isDisplayed(),
                "The selected product color should be displayed."
        );

        Assert.assertFalse(
                productColor.getAttribute("title").isBlank(),
                "The selected product color should have a valid name."
        );

        Assert.assertTrue(
                Integer.parseInt(quantity.getText().trim()) > 0,
                "The product quantity should be greater than zero."
        );

        Assert.assertTrue(
                price.getText().trim().matches("\\$[\\d,]+\\.\\d{2}"),
                "The product price should use a valid US dollar format."
        );

        Assert.assertTrue(
                editLink.isDisplayed(),
                "The EDIT link should be displayed."
        );

        Assert.assertTrue(
                removeLink.isDisplayed(),
                "The REMOVE link should be displayed."
        );
    }

    @Test(priority = 2)
    public void testCase2() throws InterruptedException {
        Thread.sleep(1000);

        List<WebElement> productRows = driver.findElements(
                By.cssSelector("#shoppingCart table tbody tr")
        );

        Assert.assertFalse(
                productRows.isEmpty(),
                "The shopping cart should contain at least one product."
        );

        double calculatedTotal = 0.0;
        int calculatedQuantity = 0;

        for (WebElement productRow : productRows) {
            String priceText = productRow.findElement(
                            By.cssSelector("p.price")
                    )
                    .getText()
                    .replace("$", "")
                    .replace(",", "")
                    .trim();

            String quantityText = productRow.findElement(
                            By.cssSelector(
                                    "td.quantityMobile > label.ng-binding"
                            )
                    )
                    .getText()
                    .trim();

            calculatedTotal += Double.parseDouble(priceText);
            calculatedQuantity += Integer.parseInt(quantityText);
        }

        String displayedTotalText = driver.findElement(
                        By.cssSelector(
                                "#shoppingCart table tfoot tr:first-child " +
                                        "td:last-child span.roboto-medium"
                        )
                )
                .getText()
                .replace("$", "")
                .replace(",", "")
                .trim();

        String checkoutButtonText = driver.findElement(
                        By.cssSelector("#checkOutButton")
                )
                .getText()
                .replace(",", "")
                .trim();

        String headingCountText = driver.findElement(
                        By.cssSelector("article.sp > h3.sticky span")
                )
                .getText()
                .replace("(", "")
                .replace(")", "")
                .trim();

        double displayedTotal = Double.parseDouble(
                displayedTotalText
        );

        Assert.assertEquals(
                displayedTotal,
                calculatedTotal,
                0.01,
                "The displayed total should equal the sum of all product prices."
        );

        Assert.assertTrue(
                checkoutButtonText.contains(
                        String.format("$%.2f", calculatedTotal)
                ),
                "The CHECKOUT button should display the current cart total."
        );

        Assert.assertEquals(
                Integer.parseInt(headingCountText),
                calculatedQuantity,
                "The shopping cart heading should display the total quantity."
        );
    }

    @Test(priority = 3)
    public void testCase3() throws InterruptedException {
        Thread.sleep(1000);

        List<WebElement> rowsBeforeEdit = driver.findElements(
                By.cssSelector("#shoppingCart table tbody tr")
        );

        Assert.assertFalse(
                rowsBeforeEdit.isEmpty(),
                "The shopping cart should contain at least one product."
        );

        int numberOfRowsBeforeEdit = rowsBeforeEdit.size();

        WebElement productRow = rowsBeforeEdit.get(0);

        String expectedProductName = productRow.findElement(
                By.cssSelector("label.productName")
        ).getText().trim();

        int oldQuantity = Integer.parseInt(
                productRow.findElement(
                        By.cssSelector("td.quantityMobile > label.ng-binding")
                ).getText().trim()
        );

        WebElement editLink = productRow.findElement(
                By.cssSelector("a.edit[href*='pageState=edit']")
        );

        String editUrl = editLink.getAttribute("href");

        Assert.assertNotNull(
                editUrl,
                "The EDIT link should have a valid destination."
        );

        Assert.assertTrue(
                editUrl.contains("/product/"),
                "The EDIT link should open the selected product page."
        );

        Assert.assertTrue(
                editUrl.contains("quantity=" + oldQuantity),
                "The EDIT link should preserve the current product quantity."
        );

        Assert.assertTrue(
                editUrl.contains("pageState=edit"),
                "The EDIT link should open the product in edit mode."
        );

        editLink.click();

        wait.until(ExpectedConditions.urlContains("/product/"));
        wait.until(ExpectedConditions.urlContains("pageState=edit"));

        wait.until(ExpectedConditions.invisibilityOfElementLocated(
                By.cssSelector("body > div.loader")
        ));

        WebElement openedProductName = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("#Description h1")
                )
        );

        Assert.assertEquals(
                openedProductName.getText().trim(),
                expectedProductName,
                "The EDIT link should open the selected cart product."
        );

        WebElement quantityInput = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(
                                "#productProperties .e-sec-plus-minus input[name='quantity']"
                        )
                )
        );

        int quantityBeforeIncrease = Integer.parseInt(
                quantityInput.getAttribute("value").trim()
        );

        Assert.assertEquals(
                quantityBeforeIncrease,
                oldQuantity,
                "The product page should display the quantity stored in the cart."
        );

        WebElement plusButton = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector(
                                "#productProperties .e-sec-plus-minus .plus[increment-value-attr='+']"
                        )
                )
        );

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'});",
                plusButton
        );

        plusButton.click();

        int expectedUpdatedQuantity = quantityBeforeIncrease + 1;

        wait.until(currentDriver -> {
            String currentValue = currentDriver.findElement(
                    By.cssSelector(
                            "#productProperties .e-sec-plus-minus input[name='quantity']"
                    )
            ).getAttribute("value");

            return currentValue != null
                    && Integer.parseInt(currentValue.trim())
                    == expectedUpdatedQuantity;
        });

        WebElement addToCartButton = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector(
                                "#productProperties button[name='save_to_cart']"
                        )
                )
        );

        Assert.assertEquals(
                addToCartButton.getText().trim(),
                "ADD TO CART",
                "The cart action button should display ADD TO CART."
        );

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'});",
                addToCartButton
        );

        wait.until(ExpectedConditions.invisibilityOfElementLocated(
                By.cssSelector("body > div.loader")
        ));

        addToCartButton.click();

        wait.until(currentDriver -> {
            List<WebElement> cartCounters = currentDriver.findElements(
                    By.cssSelector("#shoppingCartLink span.cart")
            );

            if (cartCounters.isEmpty()) {
                return false;
            }

            String cartCount = cartCounters.get(0).getText().trim();

            return !cartCount.isEmpty()
                    && Integer.parseInt(cartCount) == expectedUpdatedQuantity;
        });

        driver.navigate().to(
                "https://www.advantageonlineshopping.com/#/shoppingCart"
        );

        wait.until(ExpectedConditions.invisibilityOfElementLocated(
                By.cssSelector("body > div.loader")
        ));

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("#shoppingCart table tbody tr")
        ));

        List<WebElement> rowsAfterEdit = driver.findElements(
                By.cssSelector("#shoppingCart table tbody tr")
        );

        Assert.assertEquals(
                rowsAfterEdit.size(),
                numberOfRowsBeforeEdit,
                "Editing an existing product should not create another cart row."
        );

        WebElement updatedProductRow = null;
        int matchingProducts = 0;

        for (WebElement row : rowsAfterEdit) {
            String productName = row.findElement(
                    By.cssSelector("label.productName")
            ).getText().trim();

            if (productName.equals(expectedProductName)) {
                updatedProductRow = row;
                matchingProducts++;
            }
        }

        Assert.assertEquals(
                matchingProducts,
                1,
                "The edited product should appear only once in the shopping cart."
        );

        Assert.assertNotNull(
                updatedProductRow,
                "The edited product should remain in the shopping cart."
        );

        int actualUpdatedQuantity = Integer.parseInt(
                updatedProductRow.findElement(
                        By.cssSelector("td.quantityMobile > label.ng-binding")
                ).getText().trim()
        );

        Assert.assertEquals(
                actualUpdatedQuantity,
                expectedUpdatedQuantity,
                "The existing product quantity should be updated without creating a duplicate."
        );
    }

    @Test(priority = 4)
    public void testCase4() throws InterruptedException {
        Thread.sleep(1000);

        WebElement totalElement = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(
                                "#shoppingCart table tfoot tr:first-child " +
                                        "td:last-child span.roboto-medium"
                        )
                )
        );

        WebElement checkoutButton = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("#checkOutButton")
                )
        );

        String cartTotal = totalElement.getText().trim();

        Assert.assertTrue(
                checkoutButton.isDisplayed(),
                "The CHECKOUT button should be displayed."
        );

        Assert.assertTrue(
                checkoutButton.isEnabled(),
                "The CHECKOUT button should be enabled."
        );

        Assert.assertTrue(
                checkoutButton.getText()
                        .replaceAll("\\s+", " ")
                        .contains(cartTotal),
                "The CHECKOUT button amount should match the cart total."
        );

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'});",
                checkoutButton
        );

        wait.until(
                ExpectedConditions.elementToBeClickable(checkoutButton)
        ).click();

        wait.until(
                ExpectedConditions.invisibilityOfElementLocated(
                        By.cssSelector("body > div.loader")
                )
        );

        WebElement orderPayment = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("#orderPayment")
                )
        );

        WebElement orderPaymentHeading = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("h3[translate='ORDER_PAYMENT']")
                )
        );

        WebElement orderSummary = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("#userCart")
                )
        );

        WebElement orderItemsCount = orderSummary.findElement(
                By.cssSelector("span.itemsCount")
        );

        WebElement orderTotal = orderSummary.findElement(
                By.cssSelector("span.totalValue")
        );

        WebElement usernameInput = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(
                                "#orderPayment input[name='usernameInOrderPayment']"
                        )
                )
        );

        WebElement passwordInput = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(
                                "#orderPayment input[name='passwordInOrderPayment']"
                        )
                )
        );

        WebElement loginButton = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("#login_btn")
                )
        );

        WebElement registrationButton = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("#registration_btn")
                )
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
                orderPaymentHeading.getText().trim(),
                "ORDER PAYMENT",
                "The page heading should be ORDER PAYMENT."
        );

        Assert.assertTrue(
                orderSummary.isDisplayed(),
                "The ORDER SUMMARY should be displayed."
        );

        Assert.assertFalse(
                orderItemsCount.getText().trim().isEmpty(),
                "The ORDER SUMMARY should display the item count."
        );

        Assert.assertEquals(
                orderTotal.getText().trim(),
                cartTotal,
                "The ORDER PAYMENT total should match the Shopping Cart total."
        );

        Assert.assertTrue(
                usernameInput.isDisplayed(),
                "The Username field should be displayed."
        );

        Assert.assertTrue(
                passwordInput.isDisplayed(),
                "The Password field should be displayed."
        );

        Assert.assertTrue(
                loginButton.isDisplayed(),
                "The LOGIN button should be displayed."
        );

        Assert.assertTrue(
                registrationButton.isDisplayed(),
                "The REGISTRATION button should be displayed."
        );
    }

    @Test(priority = 5)
    public void testCase5() throws InterruptedException {
        Thread.sleep(1000);

        List<WebElement> removeLinks = driver.findElements(
                By.cssSelector("#shoppingCart table tbody a.remove")
        );

        Assert.assertFalse(
                removeLinks.isEmpty(),
                "The cart should contain at least one removable product."
        );

        while (!removeLinks.isEmpty()) {
            int productsBeforeRemoval = removeLinks.size();

            wait.until(
                    ExpectedConditions.elementToBeClickable(
                            removeLinks.get(0)
                    )
            ).click();

            wait.until(currentDriver -> currentDriver.findElements(
                    By.cssSelector("#shoppingCart table tbody a.remove")
            ).size() < productsBeforeRemoval);

            removeLinks = driver.findElements(
                    By.cssSelector("#shoppingCart table tbody a.remove")
            );
        }

        WebElement emptyCartContainer = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("#shoppingCart div.bigEmptyCart")
                )
        );

        WebElement emptyCartMessage = emptyCartContainer.findElement(
                By.cssSelector("label")
        );

        WebElement continueShoppingButton = emptyCartContainer.findElement(
                By.cssSelector("a.a-button")
        );

        Assert.assertTrue(
                emptyCartContainer.isDisplayed(),
                "The empty-cart section should be displayed after removing all products."
        );

        Assert.assertEquals(
                emptyCartMessage.getText().trim(),
                "Your shopping cart is empty",
                "The correct empty-cart message should be displayed."
        );

        Assert.assertTrue(
                continueShoppingButton.isDisplayed(),
                "The CONTINUE SHOPPING button should be displayed."
        );

        Assert.assertEquals(
                continueShoppingButton.getText().trim(),
                "CONTINUE SHOPPING",
                "The empty-cart action should be CONTINUE SHOPPING."
        );

        Assert.assertTrue(
                driver.findElements(
                        By.cssSelector("#shoppingCart table tbody tr")
                ).isEmpty(),
                "No product rows should remain after removing all products."
        );

        WebElement cartTable = driver.findElement(
                By.cssSelector("#shoppingCart table")
        );

        Assert.assertFalse(
                cartTable.isDisplayed(),
                "The shopping cart table should be hidden when the cart is empty."
        );
    }
    @Test(priority = 6)
    public void testCase6() throws InterruptedException {
        driver.navigate().to(
                "https://www.advantageonlineshopping.com/#/product/17"
        );

        wait.until(
                ExpectedConditions.invisibilityOfElementLocated(
                        By.cssSelector("body > div.loader")
                )
        );

        WebElement quantityInput = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(
                                "#productProperties .e-sec-plus-minus input[name='quantity']"
                        )
                )
        );

        WebElement addToCartButton = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(
                                "#productProperties button[name='save_to_cart']"
                        )
                )
        );

        String cartCountBeforeText = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("#shoppingCartLink span.cart")
                )
        ).getText().trim();

        int cartCountBefore = Integer.parseInt(cartCountBeforeText);

        quantityInput.clear();
        quantityInput.sendKeys("abc");

        Thread.sleep(1000);

        String quantityValue = quantityInput.getAttribute("value");

        if (addToCartButton.isEnabled()) {
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView({block:'center'});",
                    addToCartButton
            );

            addToCartButton.click();

            Thread.sleep(2000);
        }

        List<WebElement> cartCounters = driver.findElements(
                By.cssSelector("#shoppingCartLink span.cart")
        );

        int cartCountAfter = cartCounters.isEmpty()
                ? 0
                : Integer.parseInt(
                cartCounters.get(0).getText().trim()
        );

        SoftAssert softAssert = new SoftAssert();

        softAssert.assertFalse(
                quantityValue.matches(".*[a-zA-Z].*"),
                "Bug detected: The Quantity field accepted alphabetic characters."
        );

        softAssert.assertFalse(
                quantityValue.equalsIgnoreCase("abc"),
                "Bug detected: The invalid value abc remained visible in the Quantity field."
        );

        softAssert.assertEquals(
                cartCountAfter,
                cartCountBefore,
                "A product must not be added when the Quantity field contains alphabetic input."
        );

        softAssert.assertAll();
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
