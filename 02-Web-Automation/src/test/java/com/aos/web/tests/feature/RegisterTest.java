package com.aos.web.tests.feature;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.function.Consumer;

public class RegisterTest {
    WebDriver driver;

    private static final String USERNAME = "#registerPage input[name='usernameRegisterPage']";
    private static final String EMAIL = "#registerPage input[name='emailRegisterPage']";
    private static final String PASSWORD = "#registerPage input[name='passwordRegisterPage']";
    private static final String CONFIRM = "#registerPage input[name='confirm_passwordRegisterPage']";
    private static final String FIRST_NAME = "#registerPage input[name='first_nameRegisterPage']";
    private static final String AGREE_BOX = "#registerPage input[name='i_agree']";
    private static final String REGISTER = "#register_btn";

    private static final String VISIBLE_HINT_SCRIPT =
            "var label = arguments[1].toLowerCase();"
                    + "return arguments[0].innerText.split('\\n')"
                    + "  .map(function (line) { return line.trim(); })"
                    + "  .filter(function (line) { return line && line !== '*' && line.toLowerCase() !== label; })"
                    + "  .join(' | ');";

    @BeforeClass
    public void beforeClass() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    @BeforeMethod
    public void beforeMethod() throws InterruptedException {
        driver.navigate().to("https://www.advantageonlineshopping.com/#/register");
        Thread.sleep(2000);
    }

    @Test(description = "TC-007 | Confirm password empty validation")
    public void testCase1() throws InterruptedException {
        Thread.sleep(1000);
        click(CONFIRM);
        click(FIRST_NAME);
        Thread.sleep(500);

        String message = visibleHint("Confirm password").toLowerCase();
        Assert.assertTrue(message.contains("required"),
                "Expected a 'required' error under Confirm Password after leaving it empty, but found: '" + message + "'");
    }

    @Test(description = "TC-016 | Username sixteen-character boundary")
    public void testCase2() throws InterruptedException {
        Thread.sleep(1000);
        type(USERNAME, "abcdefghijklmnop");
        click(EMAIL);
        Thread.sleep(500);

        String message = visibleHint("Username");
        Assert.assertTrue(message.contains("Use maximum 15 character"),
                "Expected 'Use maximum 15 character' for a 16-character username, but found: '" + message + "'");
    }

    @Test(description = "TC-040 | Email leading space")
    public void testCase3() throws InterruptedException {
        Thread.sleep(1000);
        type(EMAIL, " ahmed@gmail.com");
        click(USERNAME);
        Thread.sleep(500);

        String value = find(EMAIL).getAttribute("value");
        String message = visibleHint("Email");
        boolean trimmed = value.equals(value.trim());
        boolean rejected = !message.isEmpty();
        Assert.assertTrue(trimmed || rejected,
                "Leading space was neither trimmed nor rejected. Value: '" + value + "', error: '" + message + "'");
    }

    @Test(description = "TC-054 | Confirm Password mismatch")
    public void testCase4() throws InterruptedException {
        Thread.sleep(1000);
        type(PASSWORD, "Abcd1234");
        type(CONFIRM, "Abcd1235");
        click(FIRST_NAME);
        Thread.sleep(500);

        String message = visibleHint("Confirm password").toLowerCase();
        Assert.assertTrue(message.contains("not match"),
                "Expected 'Passwords do not match' but found: '" + message + "'");
    }

    @Test(description = "TC-073 | Terms checkbox enables Register")
    public void testCase5() throws InterruptedException {
        Thread.sleep(1000);
        type(USERNAME, "qauser261029c");
        type(EMAIL, "qauser261029c@example.com");
        type(PASSWORD, "Test1234");
        type(CONFIRM, "Test1234");
        click(FIRST_NAME);
        Thread.sleep(500);
        Assert.assertFalse(find(REGISTER).isEnabled(), "REGISTER must be disabled while terms are unchecked");

        setCheckbox(AGREE_BOX, true);
        Thread.sleep(500);
        Assert.assertTrue(find(REGISTER).isEnabled(), "REGISTER must be enabled after accepting terms with valid required fields");

        setCheckbox(AGREE_BOX, false);
        Thread.sleep(500);
        Assert.assertFalse(find(REGISTER).isEnabled(), "REGISTER must be disabled again after unchecking terms");
    }

    @AfterMethod
    public void afterMethod() {
    }

    @AfterClass
    public void afterClass() {
        driver.quit();
    }

    private WebElement find(String css) {
        return driver.findElement(By.cssSelector(css));
    }

    private void withElement(String css, Consumer<WebElement> action) {
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .ignoring(StaleElementReferenceException.class)
                .until(d -> {
                    action.accept(d.findElement(By.cssSelector(css)));
                    return true;
                });
    }

    private void clickElement(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", element);
        try {
            element.click();
        } catch (ElementClickInterceptedException e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].focus(); arguments[0].click();", element);
        }
    }

    private void click(String css) {
        withElement(css, this::clickElement);
    }

    private void type(String css, String text) {
        withElement(css, element -> {
            clickElement(element);
            element.clear();
            element.sendKeys(text);
        });
    }

    private void setCheckbox(String css, boolean checked) {
        withElement(css, box -> {
            if (box.isSelected() != checked) {
                clickElement(box);
            }
        });
    }

    private String visibleHint(String hint) {
        String css = "#registerPage sec-view[a-hint='" + hint + "']";
        try {
            return new WebDriverWait(driver, Duration.ofSeconds(3))
                    .ignoring(StaleElementReferenceException.class)
                    .until(d -> {
                        String text = (String) ((JavascriptExecutor) d)
                                .executeScript(VISIBLE_HINT_SCRIPT, d.findElement(By.cssSelector(css)), hint);
                        return text.isEmpty() ? null : text;
                    });
        } catch (TimeoutException e) {
            return "";
        }
    }
}