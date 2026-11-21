package com.aos.web.tests.integration;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;
import org.testng.Assert;
import org.testng.annotations.*;
import java.time.Duration;
import java.util.Locale;

public class HomeIntegrationTest {
    WebDriver driver;
    WebDriverWait wait;
    final String HOME = "https://www.advantageonlineshopping.com/#/";

    @BeforeClass
    public void startBrowser() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        wait.ignoring(StaleElementReferenceException.class);
    }

    @BeforeMethod
    public void openHome() {
        driver.get(HOME);
        wait.until(ExpectedConditions.numberOfElementsToBe(
                By.cssSelector("#our_products .categoryCell"), 5));
    }

    void openCategory(String name) {
        String lower = name.toLowerCase(Locale.ROOT);
        wait.until(ExpectedConditions.elementToBeClickable(By.id(lower + "Img"))).click();
        wait.until(d -> d.getCurrentUrl().toLowerCase(Locale.ROOT)
                .contains("/category/" + lower + "/"));
        Assert.assertTrue(wait.until(d -> d.findElement(By.cssSelector("div[ui-view]"))
                        .getText().toUpperCase(Locale.ROOT).contains(name.toUpperCase(Locale.ROOT))),
                name + " category did not load");
    }

    String openPopularProduct() {
        String name = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("#popular_items [name='popular_item_21_name']"))).getText().trim();
        wait.until(ExpectedConditions.elementToBeClickable(By.id("details_21"))).click();
        wait.until(ExpectedConditions.urlContains("/product/"));
        return name;
    }

    // Home -> Speakers listing
    @Test public void test01_Speakers() {
        openCategory("Speakers");
    }

    // Home -> Laptops listing
    @Test public void test02_Laptops() {
        openCategory("Laptops");
    }

    // Home -> Headphones listing
    @Test public void test03_Headphones() {
        openCategory("Headphones");
    }

    // Home search -> matching search results
    @Test public void test04_SearchResults() {
        wait.until(ExpectedConditions.elementToBeClickable(By.id("menuSearch"))).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("autoComplete")))
                .sendKeys("Pavilion", Keys.ENTER);
        Assert.assertTrue(wait.until(d -> d.getCurrentUrl().contains("/search")
                && d.findElement(By.cssSelector("div[ui-view]"))
                .getText().toUpperCase(Locale.ROOT).contains("HP PAVILION")));
    }

    // Home popular item -> matching product details
    @Test public void test05_ProductDetails() {
        String name = openPopularProduct();
        Assert.assertTrue(wait.until(d -> d.findElement(By.cssSelector("div[ui-view]"))
                        .getText().toUpperCase(Locale.ROOT).contains(name.toUpperCase(Locale.ROOT))),
                "Opened a different product");
    }

    // Product details -> browser Back -> Home listing
    @Test public void test06_BackToHome() {
        openPopularProduct();
        driver.navigate().back();
        wait.until(d -> d.getCurrentUrl().endsWith("#/"));
        Assert.assertTrue(wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("#popular_items [name='popular_item_21_name']"))).isDisplayed());
    }

    @AfterClass(alwaysRun = true)
    public void stopBrowser() {
        if (driver != null) driver.quit();
    }
}
