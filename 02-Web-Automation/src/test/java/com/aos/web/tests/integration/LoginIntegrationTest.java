package com.aos.web.tests.integration;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

public class LoginIntegrationTest {

    WebDriver driver;

    // ===== Selectors (from the provided HTML) =====
    private static final String USER_ICON        = "#menuUserLink";
    private static final String CART_LINK        = "#shoppingCartLink";
    private static final String USERNAME_INPUT   = "login-modal input[name='username']";
    private static final String PASSWORD_INPUT   = "login-modal input[name='password']";
    private static final String SIGN_IN_BTN      = "#sign_in_btn";
    private static final String FORGOT_LINK      = "login-modal a.forgot-Passwowd";
    private static final String CREATE_ACCOUNT   = "login-modal a.create-new-account";
    private static final String FACEBOOK_BTN     = "login-modal span.facebook";
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

    // ---------------------------------------------------------------- helpers
    private void jsClick(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }

    private void openLoginWindow() throws InterruptedException {
        jsClick(driver.findElement(By.cssSelector(USER_ICON)));
        Thread.sleep(1500);
    }

    private boolean isLoggedIn() {
        List<WebElement> found = driver.findElements(By.cssSelector(HEADER_USERNAME));
        return !found.isEmpty() && found.get(0).isDisplayed() && !found.get(0).getText().trim().isEmpty();
    }

    // ------------------------------------------------------------------ tests

    /** Integration TC4 - Forgot password must start the reset flow (known bug: nothing happens -> fails). */
    @Test
    public void testCase1() throws InterruptedException {
        openLoginWindow();
        driver.findElement(By.cssSelector(USERNAME_INPUT)).sendKeys("mostafanabhan");
        String urlBefore = driver.getCurrentUrl();
        String textBefore = driver.findElement(By.tagName("body")).getText().toLowerCase();

        driver.findElement(By.cssSelector(FORGOT_LINK)).click();
        Thread.sleep(2500);

        String urlAfter = driver.getCurrentUrl();
        String textAfter = driver.findElement(By.tagName("body")).getText().toLowerCase();

        boolean urlChanged = !urlAfter.equals(urlBefore);
        boolean resetMessageShown = (textAfter.contains("reset") && !textBefore.contains("reset"))
                || (textAfter.contains("sent") && !textBefore.contains("sent"));

        Assert.assertTrue(urlChanged || resetMessageShown,
                "Forgot password did nothing: no reset page, window or confirmation message appeared");
    }

    /** Component TC5 - Login window <-> Facebook: must open Facebook login (known bug: 403 error -> fails). */
    @Test
    public void testCase2() throws InterruptedException {
        openLoginWindow();
        String mainWindow = driver.getWindowHandle();
        driver.findElement(By.cssSelector(FACEBOOK_BTN)).click();
        Thread.sleep(3000);

        String pageText = driver.findElement(By.tagName("body")).getText().toLowerCase();
        boolean facebookOpened = driver.getWindowHandles().size() > 1
                || driver.getCurrentUrl().contains("facebook.com");

        for (String handle : driver.getWindowHandles()) {
            if (!handle.equals(mainWindow)) {
                driver.switchTo().window(handle).close();
            }
        }
        driver.switchTo().window(mainWindow);

        Assert.assertFalse(pageText.contains("403 forbidden") || pageText.contains("currently unavailable"),
                "Facebook sign-in shows an error instead of opening the Facebook login");
        Assert.assertTrue(facebookOpened, "Facebook login window/page did not open");
    }

    /** Login window + header: with the login window open, the cart icon still navigates to the cart page. */
    @Test
    public void testCase3() throws InterruptedException {
        openLoginWindow();
        jsClick(driver.findElement(By.cssSelector(CART_LINK)));
        Thread.sleep(2500);

        Assert.assertTrue(driver.getCurrentUrl().toLowerCase().contains("shoppingcart"),
                "Cart page did not open from the header while the login window was open");
        Assert.assertFalse(isLoggedIn(), "User must not be logged in");
    }

    /** Login form + Sign In button: enabled only when BOTH fields are filled, disabled again when one is cleared. */
    @Test
    public void testCase4() throws InterruptedException {
        openLoginWindow();
        WebElement username = driver.findElement(By.cssSelector(USERNAME_INPUT));
        WebElement password = driver.findElement(By.cssSelector(PASSWORD_INPUT));
        WebElement signIn = driver.findElement(By.cssSelector(SIGN_IN_BTN));

        Assert.assertFalse(signIn.isEnabled(), "Sign In must be disabled when both fields are empty");

        username.sendKeys("mostafanabhan");
        Thread.sleep(500);
        Assert.assertFalse(signIn.isEnabled(), "Sign In must be disabled when only username is filled");

        password.sendKeys("Test@1234");
        Thread.sleep(500);
        Assert.assertTrue(signIn.isEnabled(), "Sign In must be enabled when both fields are filled");

        username.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
        Thread.sleep(500);
        Assert.assertFalse(signIn.isEnabled(), "Sign In must be disabled again after clearing the username");
    }

    /** Failed login -> error shown -> the login window is still usable: Create new account opens registration. */
    @Test
    public void testCase5() throws InterruptedException {
        openLoginWindow();
        driver.findElement(By.cssSelector(USERNAME_INPUT)).sendKeys("mostafanabhan");
        driver.findElement(By.cssSelector(PASSWORD_INPUT)).sendKeys("WrongPass@999");
        driver.findElement(By.cssSelector(SIGN_IN_BTN)).click();
        Thread.sleep(3000);

        String error = driver.findElement(By.cssSelector(LOGIN_RESULT_MSG)).getText().trim();
        Assert.assertEquals(error, "Incorrect user name or password.");
        Assert.assertFalse(isLoggedIn(), "User must not be logged in after a failed login");

        driver.findElement(By.cssSelector(CREATE_ACCOUNT)).click();
        Thread.sleep(2500);

        boolean registerUrl = driver.getCurrentUrl().toLowerCase().contains("register");
        boolean registerFormShown = !driver.findElements(By.cssSelector("input[name='usernameRegisterPage']")).isEmpty();
        Assert.assertTrue(registerUrl || registerFormShown,
                "Create new account did not open the registration screen after a failed login");
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
