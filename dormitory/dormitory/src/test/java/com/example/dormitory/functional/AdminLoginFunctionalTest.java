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

class AdminLoginFunctionalTest {

    private WebDriver driver;
    private WebDriverWait wait;

    private final String BASE_URL = "http://localhost:8080";

    // ============================================================
    // บัญชี Admin จริงสำหรับ Functional Test
    // ============================================================
    private final String ADMIN_EMAIL = "lucaenen03@gmail.com";
    private final String ADMIN_PASSWORD = "adminnaib";

    @BeforeEach
    void setUp() {

        driver = new ChromeDriver();

        driver.manage().window().maximize();

        wait = new WebDriverWait(
                driver,
                Duration.ofSeconds(20)
        );

        driver.get(BASE_URL + "/login");

        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.name("email")
                )
        );
    }

    @AfterEach
    void tearDown() {

        if (driver != null) {
            driver.quit();
        }
    }

    // ============================================================
    // Helper Method
    // ============================================================

    private void loginAsAdmin() {

        WebElement emailInput =
                wait.until(
                        ExpectedConditions.visibilityOfElementLocated(
                                By.name("email")
                        )
                );

        WebElement passwordInput =
                wait.until(
                        ExpectedConditions.visibilityOfElementLocated(
                                By.name("password")
                        )
                );

        emailInput.clear();
        emailInput.sendKeys(ADMIN_EMAIL);

        passwordInput.clear();
        passwordInput.sendKeys(ADMIN_PASSWORD);

        WebElement loginButton =
                wait.until(
                        ExpectedConditions.elementToBeClickable(
                                By.cssSelector(
                                        "button[type='submit']"
                                )
                        )
                );

        loginButton.click();

        /*
         * รอให้เกิดการเปลี่ยนหน้า
         * ถ้า Login ไม่สำเร็จ ระบบจะยังอยู่ /login
         */
        wait.until(
                driver -> !driver.getCurrentUrl()
                        .contains("/login")
        );
    }

    // ============================================================
    // TC-FT-08-01
    // ตรวจสอบการเปิดหน้า Login
    // ============================================================

    @Test
    void TC_FT_08_01_userCanOpenLoginPage() {

        assertTrue(
                driver.getCurrentUrl().contains("/login"),
                "Login page should be displayed"
        );

        assertTrue(
                driver.findElement(By.name("email"))
                        .isDisplayed(),
                "Email field should be displayed"
        );

        assertTrue(
                driver.findElement(By.name("password"))
                        .isDisplayed(),
                "Password field should be displayed"
        );
    }

    // ============================================================
    // TC-FT-08-02
    // ตรวจสอบการกรอก Email และ Password
    // ============================================================

    @Test
    void TC_FT_08_02_adminCanEnterLoginInformation() {

        WebElement emailInput =
                wait.until(
                        ExpectedConditions.visibilityOfElementLocated(
                                By.name("email")
                        )
                );

        WebElement passwordInput =
                wait.until(
                        ExpectedConditions.visibilityOfElementLocated(
                                By.name("password")
                        )
                );

        emailInput.clear();
        emailInput.sendKeys(ADMIN_EMAIL);

        passwordInput.clear();
        passwordInput.sendKeys(ADMIN_PASSWORD);

        assertEquals(
                ADMIN_EMAIL,
                emailInput.getAttribute("value")
        );

        assertEquals(
                ADMIN_PASSWORD,
                passwordInput.getAttribute("value")
        );
    }

    // ============================================================
    // TC-FT-08-03
    // ตรวจสอบการ Login ด้วยบัญชี Admin
    // ============================================================

    @Test
    void TC_FT_08_03_adminCanLoginSuccessfully() {

        loginAsAdmin();

        String currentUrl =
                driver.getCurrentUrl();

        assertTrue(
                currentUrl.contains("/admin"),
                "Admin should be redirected to an admin page"
        );
    }

    // ============================================================
    // TC-FT-08-04
    // ตรวจสอบการ Redirect ไปยังหน้าจัดการคำร้อง
    // ============================================================

    @Test
    void TC_FT_08_04_adminIsRedirectedToRequestManagement() {

        loginAsAdmin();

        wait.until(
                ExpectedConditions.urlToBe(
                        BASE_URL + "/admin/requests"
                )
        );

        assertEquals(
                BASE_URL + "/admin/requests",
                driver.getCurrentUrl()
        );
    }

    // ============================================================
    // TC-FT-08-05
    // ตรวจสอบว่า Admin สามารถเข้าถึงหน้าจัดการคำร้องได้
    // ============================================================

    @Test
    void TC_FT_08_05_adminCanAccessRequestManagementPage() {

        loginAsAdmin();

        wait.until(
                ExpectedConditions.urlToBe(
                        BASE_URL + "/admin/requests"
                )
        );

        WebElement body =
                wait.until(
                        ExpectedConditions.visibilityOfElementLocated(
                                By.tagName("body")
                        )
                );

        assertTrue(
                body.isDisplayed(),
                "Admin request management page should be displayed"
        );

        assertTrue(
                driver.getCurrentUrl()
                        .equals(BASE_URL + "/admin/requests"),
                "URL should be /admin/requests"
        );
    }
}