package com.example.dormitory.functional;

import java.time.Duration;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

class LoginFunctionalTest {

    private WebDriver driver;
    private WebDriverWait wait;

    private static final String BASE_URL =
            "http://localhost:8080";

    @BeforeEach
    void setUp() {

        driver = new ChromeDriver();

        driver.manage()
                .window()
                .maximize();

        wait = new WebDriverWait(
                driver,
                Duration.ofSeconds(15)
        );

        driver.get(BASE_URL + "/login");
    }

    @AfterEach
    void tearDown() {

        if (driver != null) {
            driver.quit();
        }
    }

    /*
     * =========================================================
     * TC-FT-02-01
     * Login ด้วยข้อมูลที่ถูกต้อง
     * =========================================================
     */

    @Test
    void TC_FT_02_01_loginWithValidData_shouldSuccess() {

        WebElement email =
                wait.until(
                        ExpectedConditions.visibilityOfElementLocated(
                                By.id("email")
                        )
                );

        WebElement password =
                wait.until(
                        ExpectedConditions.visibilityOfElementLocated(
                                By.id("password")
                        )
                );

        email.sendKeys(
                "lucaenen01@gmail.com"
        );

        password.sendKeys(
                "lucazaza"
        );

        driver.findElement(
                By.cssSelector(".submit-button")
        ).click();

        /*
         * Login สำเร็จต้องออกจากหน้า /login
         * และเข้าสู่หน้าของ Reporter
         */

        wait.until(
                ExpectedConditions.urlContains(
                        "/reporter/"
                )
        );

        assertTrue(
                driver.getCurrentUrl()
                        .contains("/reporter/"),
                "Login should redirect to reporter page"
        );
    }

    /*
     * =========================================================
     * TC-FT-02-02
     * Password ไม่ถูกต้อง
     * =========================================================
     */

    @Test
    void TC_FT_02_02_loginWithWrongPassword_shouldShowError() {

        WebElement email =
                wait.until(
                        ExpectedConditions.visibilityOfElementLocated(
                                By.id("email")
                        )
                );

        WebElement password =
                wait.until(
                        ExpectedConditions.visibilityOfElementLocated(
                                By.id("password")
                        )
                );

        email.sendKeys(
                "lucaenen01@gmail.com"
        );

        password.sendKeys(
                "wrong123"
        );

        driver.findElement(
                By.cssSelector(".submit-button")
        ).click();

        WebElement error =
                wait.until(
                        ExpectedConditions.visibilityOfElementLocated(
                                By.cssSelector(".error-message")
                        )
                );

        assertEquals(
                "Invalid email or password",
                error.getText()
        );

        assertTrue(
                driver.getCurrentUrl()
                        .contains("/login"),
                "User should remain on login page"
        );
    }

    /*
     * =========================================================
     * TC-FT-02-03
     * ไม่กรอก Password
     * =========================================================
     */

    @Test
    void TC_FT_02_03_loginWithoutPassword_shouldBeRejected() {

        WebElement email =
                wait.until(
                        ExpectedConditions.visibilityOfElementLocated(
                                By.id("email")
                        )
                );

        email.sendKeys(
                "lucaenen01@gmail.com"
        );

        driver.findElement(
                By.cssSelector(".submit-button")
        ).click();

        /*
         * HTML input มี required
         * Browser จึงต้องไม่ submit form
         */

        WebElement password =
                driver.findElement(
                        By.id("password")
                );

        assertTrue(
                password.getAttribute("value").isEmpty(),
                "Password should remain empty"
        );

        assertTrue(
                driver.getCurrentUrl()
                        .contains("/login"),
                "User should remain on login page"
        );
    }

    /*
     * =========================================================
     * TC-FT-02-04
     * ไม่กรอก Email
     * =========================================================
     */

    @Test
    void TC_FT_02_04_loginWithoutEmail_shouldBeRejected() {

        WebElement password =
                wait.until(
                        ExpectedConditions.visibilityOfElementLocated(
                                By.id("password")
                        )
                );

        password.sendKeys(
                "lucazaza"
        );

        driver.findElement(
                By.cssSelector(".submit-button")
        ).click();

        WebElement email =
                driver.findElement(
                        By.id("email")
                );

        assertTrue(
                email.getAttribute("value").isEmpty(),
                "Email should remain empty"
        );

        assertTrue(
                driver.getCurrentUrl()
                        .contains("/login"),
                "User should remain on login page"
        );
    }

    /*
     * =========================================================
     * TC-FT-02-05
     * Email format ไม่ถูกต้อง
     * =========================================================
     */

    @Test
    void TC_FT_02_05_loginWithInvalidEmailFormat_shouldBeRejected() {

        WebElement email =
                wait.until(
                        ExpectedConditions.visibilityOfElementLocated(
                                By.id("email")
                        )
                );

        WebElement password =
                wait.until(
                        ExpectedConditions.visibilityOfElementLocated(
                                By.id("password")
                        )
                );

        email.sendKeys(
                "lucaenen01@"
        );

        password.sendKeys(
                "lucazaza"
        );

        driver.findElement(
                By.cssSelector(".submit-button")
        ).click();

        /*
         * input type="email" + required
         * Browser validation จะป้องกันการ submit
         */

        assertTrue(
                driver.getCurrentUrl()
                        .contains("/login"),
                "Invalid email should not submit"
        );

        assertEquals(
                "lucaenen01@",
                email.getAttribute("value")
        );
    }

    /*
     * =========================================================
     * TC-FT-02-06
     * Logout หลัง Login สำเร็จ
     * =========================================================
     */

    @Test
    void TC_FT_02_06_logoutAfterSuccessfulLogin_shouldReturnToLogin()
            throws InterruptedException {

        WebElement email =
                wait.until(
                        ExpectedConditions.visibilityOfElementLocated(
                                By.id("email")
                        )
                );

        WebElement password =
                wait.until(
                        ExpectedConditions.visibilityOfElementLocated(
                                By.id("password")
                        )
                );

        email.sendKeys(
                "lucaenen01@gmail.com"
        );

        password.sendKeys(
                "lucazaza"
        );

        driver.findElement(
                By.cssSelector(".submit-button")
        ).click();

        /*
         * รอ Login สำเร็จ
         */

        wait.until(
                ExpectedConditions.urlContains(
                        "/reporter/"
                )
        );

        /*
         * หา Logout link/button
         *
         * เนื่องจากหน้า Reporter อาจมี selector
         * แตกต่างกัน จึงค้นจาก href="/logout"
         */

        WebElement logout =
                wait.until(
                        ExpectedConditions.presenceOfElementLocated(
                                By.cssSelector(
                                        "a[href='/logout'], " +
                                        "form[action='/logout'] button, " +
                                        "form[action='/logout'] input[type='submit']"
                                )
                        )
                );

        logout.click();

        /*
         * รอ redirect กลับ /login
         */

        wait.until(
                ExpectedConditions.urlContains(
                        "/login"
                )
        );

        assertTrue(
                driver.getCurrentUrl()
                        .contains("/login"),
                "Logout should redirect to login page"
        );
    }
}