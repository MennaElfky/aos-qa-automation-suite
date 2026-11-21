package com.aos.web.tests.feature;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class LoginTest {

    WebDriver driver;

    // ===== Selectors (from the provided HTML) =====
    private static final String USER_ICON        = "#menuUserLink";
    private static final String LOGO             = "login-modal img[src*='logo']";
    private static final String FACEBOOK_BTN     = "login-modal span.facebook";
    private static final String USERNAME_INPUT   = "login-modal input[name='username']";
    private static final String PASSWORD_INPUT   = "login-modal input[name='password']";
    private static final String REMEMBER_ME      = "login-modal input[name='remember_me']";
    private static final String SIGN_IN_BTN      = "#sign_in_btn";
    private static final String FORGOT_LINK      = "login-modal a.forgot-Passwowd";
    private static final String CREATE_ACCOUNT   = "login-modal a.create-new-account";
    private static final String CLOSE_BTN        = "login-modal div.loginPopUpCloseBtn";
    private static final String USERNAME_ERROR   = "login-modal sec-view[sec-name='username'] label.invalid";
    private static final String LOGIN_RESULT_MSG = "#signInResultMessage";
    private static final String HEADER_USERNAME  = "span.hi-user.containMiniTitle";

    @BeforeClass
    public void beforeClass() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    @BeforeMethod
    public void beforeMethod() throws InterruptedException {
        driver.navigate().to("https://www.advantageonlineshopping.com/#/");
        Thread.sleep(3000);
    }

    // ------------------------------------------------------------------ helpers
    private void openLoginWindow() throws InterruptedException {
        WebElement icon = driver.findElement(By.cssSelector(USER_ICON));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", icon);
        Thread.sleep(1500);
    }

    private boolean isLoggedIn() {
        try {
            WebElement hi = driver.findElement(By.cssSelector(HEADER_USERNAME));
            return hi.isDisplayed() && !hi.getText().trim().isEmpty();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    // ------------------------------------------------------------------ tests

    /** TC1 - Login window shows all required elements; Sign In is disabled while fields are empty. */
    @Test
    public void testCase1() throws InterruptedException {
        openLoginWindow();
        Assert.assertTrue(driver.findElement(By.cssSelector(LOGO)).isDisplayed(), "Logo missing");
        Assert.assertTrue(driver.findElement(By.cssSelector(FACEBOOK_BTN)).isDisplayed(), "Facebook button missing");
        Assert.assertTrue(driver.findElement(By.cssSelector(USERNAME_INPUT)).isDisplayed(), "Username field missing");
        Assert.assertTrue(driver.findElement(By.cssSelector(PASSWORD_INPUT)).isDisplayed(), "Password field missing");
        Assert.assertTrue(driver.findElement(By.cssSelector(REMEMBER_ME)).isDisplayed(), "Remember me missing");
        Assert.assertTrue(driver.findElement(By.cssSelector(SIGN_IN_BTN)).isDisplayed(), "Sign In button missing");
        Assert.assertTrue(driver.findElement(By.cssSelector(FORGOT_LINK)).isDisplayed(), "Forgot password link missing");
        Assert.assertTrue(driver.findElement(By.cssSelector(CREATE_ACCOUNT)).isDisplayed(), "Create new account missing");
        Assert.assertFalse(driver.findElements(By.cssSelector(CLOSE_BTN)).isEmpty(), "Close (X) button missing");
        Assert.assertFalse(driver.findElement(By.cssSelector(SIGN_IN_BTN)).isEnabled(),
                "Sign In must be disabled while fields are empty");
        Thread.sleep(1000);
    }

    /** TC2 - Empty fields: "Username field is required" and user not logged in. */
    @Test
    public void testCase2() throws InterruptedException {
        openLoginWindow();
        driver.findElement(By.cssSelector(USERNAME_INPUT)).click();
        driver.findElement(By.cssSelector(PASSWORD_INPUT)).click();   // blur username -> validation fires
        Thread.sleep(800);

        String message = driver.findElement(By.cssSelector(USERNAME_ERROR)).getText().trim();
        Assert.assertEquals(message, "Username field is required");
        Assert.assertFalse(isLoggedIn(), "User must not be logged in");
        Thread.sleep(1000);
    }

    /** TC3 - Forgot password with empty username shows the required message. */
    @Test
    public void testCase3() throws InterruptedException {
        openLoginWindow();
        // focus the empty Username field, then click Forgot password (this blurs the field like a real user would)
        driver.findElement(By.cssSelector(USERNAME_INPUT)).click();
        driver.findElement(By.cssSelector(FORGOT_LINK)).click();
        Thread.sleep(1500);

        // the message may be rendered in the username label or in the general result label
        String usernameLabel = driver.findElement(
                By.cssSelector("login-modal sec-view[sec-name='username'] label")).getText().trim();
        String resultLabel = driver.findElement(By.cssSelector(LOGIN_RESULT_MSG)).getText().trim();

        Assert.assertTrue(usernameLabel.equals("Username field is required")
                        || resultLabel.equals("Username field is required"),
                "Expected 'Username field is required' but found: username label='" + usernameLabel
                        + "', result label='" + resultLabel + "'");
        Thread.sleep(1000);
    }

    /** TC11 - Non-existent username: generic error, user not logged in. */
    @Test
    public void testCase4() throws InterruptedException {
        openLoginWindow();
        driver.findElement(By.cssSelector(USERNAME_INPUT)).sendKeys("l0101js");
        driver.findElement(By.cssSelector(PASSWORD_INPUT)).sendKeys("Test@1234");
        driver.findElement(By.cssSelector(SIGN_IN_BTN)).click();
        Thread.sleep(3000);

        String error = driver.findElement(By.cssSelector(LOGIN_RESULT_MSG)).getText().trim();
        Assert.assertEquals(error, "Incorrect user name or password.");
        Assert.assertFalse(isLoggedIn(), "User must not be logged in");
        Thread.sleep(1000);
    }

    /** TC12 - Password field is masked. */
    @Test
    public void testCase5() throws InterruptedException {
        openLoginWindow();
        WebElement password = driver.findElement(By.cssSelector(PASSWORD_INPUT));
        password.sendKeys("Test@1234");
        Thread.sleep(500);

        Assert.assertEquals(password.getAttribute("type"), "password", "Password must be masked");
        Thread.sleep(1000);
    }

    @AfterMethod
    public void afterMethod() {
        try {
            driver.manage().deleteAllCookies();
            ((JavascriptExecutor) driver)
                    .executeScript("window.localStorage.clear(); window.sessionStorage.clear();");
        } catch (Exception ignored) {
        }
    }

    @AfterClass
    public void afterClass() {
        driver.quit();
    }
}
