package com.example.dormitory.uat;

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

class AdminLoginUATTest {

    private WebDriver driver;
    private WebDriverWait wait;

    private final String BASE_URL = "http://localhost:8080";

    // ============================================================
    // บัญชี Admin จริงสำหรับ UAT
    // ============================================================
    private final String ADMIN_EMAIL = "lucaenen03@gmail.com";
    private final String ADMIN_PASSWORD = "adminnaib";

    @BeforeEach
    void setUp() {

        driver = new ChromeDriver();

        driver.manage().window().maximize();

        wait = new WebDriverWait(
                driver,
                Duration.ofSeconds(30)
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

        // ========================================================
        // รอให้เข้าสู่หน้าจัดการคำร้องของ Admin โดยตรง
        // ========================================================

        wait.until(
                ExpectedConditions.urlToBe(
                        BASE_URL + "/admin/requests"
                )
        );
    }

    // ============================================================
    // TC-UAT-08-01
    // เจ้าหน้าที่สามารถเปิดหน้า Login ได้
    // ============================================================

    @Test
    void TC_UAT_08_01_adminCanOpenLoginPage() {

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
    // TC-UAT-08-02
    // เจ้าหน้าที่สามารถกรอกข้อมูล Login ได้
    // ============================================================

    @Test
    void TC_UAT_08_02_adminCanEnterLoginInformation() {

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
    // TC-UAT-08-03
    // เจ้าหน้าที่สามารถเข้าสู่ระบบด้วยบัญชี Admin ได้
    // ============================================================

    @Test
    void TC_UAT_08_03_adminCanLoginSuccessfully() {

        loginAsAdmin();

        assertEquals(
                BASE_URL + "/admin/requests",
                driver.getCurrentUrl()
        );
    }

    // ============================================================
    // TC-UAT-08-04
    // ระบบนำ Admin ไปยังหน้าจัดการคำร้อง
    // ============================================================

    @Test
    void TC_UAT_08_04_adminCanAccessRequestManagement() {

        loginAsAdmin();

        assertEquals(
                BASE_URL + "/admin/requests",
                driver.getCurrentUrl()
        );
    }

    // ============================================================
    // TC-UAT-08-05
    // Admin สามารถใช้งานหน้าจัดการคำร้องหลัง Login ได้
    // ============================================================

    @Test
    void TC_UAT_08_05_adminCanUseRequestManagementPage() {

        loginAsAdmin();

        WebElement body =
                wait.until(
                        ExpectedConditions.visibilityOfElementLocated(
                                By.tagName("body")
                        )
                );

        assertTrue(
                body.isDisplayed(),
                "Request management page should be displayed"
        );

        assertEquals(
                BASE_URL + "/admin/requests",
                driver.getCurrentUrl()
        );
    }
}