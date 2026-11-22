package com.aos.web.tests.feature;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;
import org.testng.Assert;
import org.testng.annotations.*;
import java.time.Duration;

public class HomePageTest {
    WebDriver driver;
    WebDriverWait wait;
    final String HOME = "https://www.advantageonlineshopping.com/#/";

    @BeforeMethod
    public void start() {
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        wait.ignoring(StaleElementReferenceException.class);
        driver.get(HOME);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("our_products")));
    }

    void search(String word) {
        wait.until(ExpectedConditions.elementToBeClickable(By.id("menuSearch"))).click();
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("autoComplete")));
        input.clear();
        input.sendKeys(word, Keys.ENTER);
        // No URL wait here: the search results (including no-results) are verified in each test.
    }

    String page() {
        return driver.findElement(By.cssSelector("div[ui-view]")).getText().toUpperCase();
    }

    // Component TC-01: full product name
    @Test public void TC01_FullNameSearch() {
        search("HP Pavilion 15z Laptop");
        Assert.assertTrue(wait.until(d -> d.getCurrentUrl().contains("/search")
                && page().contains("HP PAVILION 15Z LAPTOP")));
    }

    // Component TC-02: partial keyword
    @Test public void TC02_PartialSearch() {
        search("Pavilion");
        Assert.assertTrue(wait.until(d -> d.getCurrentUrl().contains("/search")
                && page().contains("HP PAVILION")));
    }

    // Component TC-07: five Home categories
    @Test public void TC07_CategoriesVisible() {
        for (String name : new String[]{"speakers", "tablets", "laptops", "mice", "headphones"})
            Assert.assertTrue(driver.findElement(By.id(name + "Img")).isDisplayed(), name);
    }

    // Component TC-09: category page title
    @Test public void TC09_CategoryTitle() {
        driver.findElement(By.id("speakersImg")).click();
        wait.until(d -> d.getCurrentUrl().toLowerCase().contains("/category/speakers/"));
        Assert.assertTrue(page().contains("SPEAKERS"));
    }

    // Component TC-12: filter section on category page
    @Test public void TC12_FilterSection() {
        driver.findElement(By.id("speakersImg")).click();
        wait.until(d -> d.getCurrentUrl().toLowerCase().contains("/category/speakers/"));
        Assert.assertTrue(wait.until(d -> page().contains("FILTER")),
                "TC-12: Filter section should be visible");
    }

    // Component TC-39: searching for an unknown product shows the no-results message
    @Test public void TC39_NoResults() {
        search("unicorn");
        String result;
        try {
            result = wait.until(d -> page().contains("NO RESULTS FOR") ? page() : null);
        } catch (TimeoutException e) {
            Assert.fail("TC-39: No-results message did not appear. URL: " + driver.getCurrentUrl());
            return;
        }
        Assert.assertTrue(result.contains("UNICORN"),
                "TC-39: No-results message must include the search keyword");
    }

    @AfterMethod(alwaysRun = true)
    public void stop() { if (driver != null) driver.quit(); }
}
