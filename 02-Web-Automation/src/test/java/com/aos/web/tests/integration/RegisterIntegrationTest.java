package com.aos.web.tests.integration;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
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
import java.util.function.Consumer;

public class RegisterIntegrationTest {
    WebDriver driver;

    private static final String SITE_URL = "https://www.advantageonlineshopping.com";
    private static final String REGISTER_URL = SITE_URL + "/#/register";
    private static final String PASSWORD_VALUE = "Test1234";

    private static final String USERNAME = "#registerPage input[name='usernameRegisterPage']";
    private static final String EMAIL = "#registerPage input[name='emailRegisterPage']";
    private static final String PASSWORD = "#registerPage input[name='passwordRegisterPage']";
    private static final String CONFIRM = "#registerPage input[name='confirm_passwordRegisterPage']";
    private static final String FIRST_NAME = "#registerPage input[name='first_nameRegisterPage']";
    private static final String AGREE_BOX = "#registerPage input[name='i_agree']";
    private static final String REGISTER = "#register_btn";
    private static final String FORM_ERROR = "#registerPage label.invalid.smollMargin";
    private static final String HEADER_USER = "#menuUserLink span.hi-user";

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

    @Test(description = "ITC-001 | Successful registration with required fields only")
    public void testCase1() throws InterruptedException {
        Thread.sleep(1000);
        String user = uniqueUser("");

        fillAndSubmit(user, user + "@example.com", PASSWORD_VALUE, PASSWORD_VALUE);

        Assert.assertTrue(waitForRedirect(15),
                "Account was not created: user stayed on the Register page after submitting valid required fields");
    }

    @Test(description = "ITC-002 | Duplicate username rejected")
    public void testCase2() throws InterruptedException {
        Thread.sleep(1000);
        String user = uniqueUser("");

        fillAndSubmit(user, user + "@example.com", PASSWORD_VALUE, PASSWORD_VALUE);
        Assert.assertTrue(waitForRedirect(15), "Precondition failed: the first account was not created");

        resetSession();
        fillAndSubmit(user, user + "b@example.com", PASSWORD_VALUE, PASSWORD_VALUE);

        String error = waitForFormError(10);
        Assert.assertTrue(error.toLowerCase().contains("already exists"),
                "Expected a 'User name already exists' error but found: '" + error + "'");
        Assert.assertTrue(driver.getCurrentUrl().contains("register"),
                "User must remain on the Register page after a duplicate-username attempt");
    }

    @Test(description = "ITC-003 | Duplicate email with a different username")
    public void testCase3() throws InterruptedException {
        Thread.sleep(1000);
        String firstUser = uniqueUser("");
        String secondUser = uniqueUser("b");
        String sharedEmail = firstUser + "@example.com";

        fillAndSubmit(firstUser, sharedEmail, PASSWORD_VALUE, PASSWORD_VALUE);
        Assert.assertTrue(waitForRedirect(15), "Precondition failed: the first account was not created");

        resetSession();
        fillAndSubmit(secondUser, sharedEmail, PASSWORD_VALUE, PASSWORD_VALUE);

        Assert.assertFalse(waitForRedirect(8),
                "A second account was created with an email that is already registered: " + sharedEmail);
    }

    @Test(description = "ITC-009 | Register rejects password mismatch")
    public void testCase4() throws InterruptedException {
        Thread.sleep(1000);
        String user = uniqueUser("");

        type(USERNAME, user);
        type(EMAIL, user + "@example.com");
        type(PASSWORD, PASSWORD_VALUE);
        type(CONFIRM, "Test1235");
        setCheckbox(AGREE_BOX, true);
        click(FIRST_NAME);
        Thread.sleep(500);

        String message = visibleHint("Confirm password").toLowerCase();
        Assert.assertTrue(message.contains("not match"),
                "Expected 'Passwords do not match' but found: '" + message + "'");
        Assert.assertFalse(find(REGISTER).isEnabled(),
                "REGISTER must stay disabled while Password and Confirm Password differ");
    }

    @Test(description = "ITC-012 | Signup automatically starts signed-in session")
    public void testCase5() throws InterruptedException {
        Thread.sleep(1000);
        String user = uniqueUser("");

        fillAndSubmit(user, user + "@example.com", PASSWORD_VALUE, PASSWORD_VALUE);
        Assert.assertTrue(waitForRedirect(15), "Precondition failed: registration did not redirect to home");

        try {
            new WebDriverWait(driver, Duration.ofSeconds(10))
                    .until(ExpectedConditions.textToBePresentInElementLocated(By.cssSelector(HEADER_USER), user));
        } catch (TimeoutException e) {
            Assert.fail("Username '" + user + "' is not shown in the header after registration. Header shows: '"
                    + find(HEADER_USER).getText() + "'");
        }
    }

    @AfterMethod
    public void afterMethod() {
        clearSession();
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

    private String uniqueUser(String suffix) {
        return "qa" + System.currentTimeMillis() % 10_000_000_000L + suffix;
    }

    private void fillAndSubmit(String user, String email, String password, String confirm) {
        type(USERNAME, user);
        type(EMAIL, email);
        type(PASSWORD, password);
        type(CONFIRM, confirm);
        setCheckbox(AGREE_BOX, true);
        click(FIRST_NAME);
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.elementToBeClickable(By.cssSelector(REGISTER)));
        click(REGISTER);
    }

    private boolean waitForRedirect(int seconds) {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(seconds))
                    .until(ExpectedConditions.not(ExpectedConditions.urlContains("register")));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    private String waitForFormError(int seconds) {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(seconds))
                    .until(d -> !d.findElement(By.cssSelector(FORM_ERROR)).getText().trim().isEmpty());
        } catch (TimeoutException e) {
            return "";
        }
        return find(FORM_ERROR).getText().trim();
    }

    private void clearSession() {
        if (!driver.getCurrentUrl().startsWith(SITE_URL)) {
            driver.navigate().to(REGISTER_URL);
        }
        driver.manage().deleteAllCookies();
        ((JavascriptExecutor) driver).executeScript("window.localStorage.clear(); window.sessionStorage.clear();");
        driver.navigate().refresh();
    }

    private void resetSession() throws InterruptedException {
        clearSession();
        driver.navigate().to(REGISTER_URL);
        driver.navigate().refresh();
        Thread.sleep(2000);
    }
}