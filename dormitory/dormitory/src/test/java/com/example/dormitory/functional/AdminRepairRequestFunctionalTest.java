package com.example.dormitory.functional;

import java.time.Duration;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

class AdminRepairRequestFunctionalTest {

    private WebDriver driver;
    private WebDriverWait wait;

    private static final String BASE_URL =
            "http://localhost:8080";

    private static final String ADMIN_EMAIL =
            "lucaenen03@gmail.com";

    private static final String ADMIN_PASSWORD =
            "adminnaib";

    @BeforeEach
    void setUp() {

        driver = new ChromeDriver();

        driver.manage().window().maximize();

        wait = new WebDriverWait(
                driver,
                Duration.ofSeconds(30)
        );
    }

    @AfterEach
    void tearDown() {

        if (driver != null) {
            driver.quit();
        }
    }

    /*
     * ============================================================
     * TC-FT-09-01
     * ตรวจสอบการเปิดหน้า Login
     * ============================================================
     */
    @Test
    void TC_FT_09_01_adminCanOpenLoginPage() {

        driver.get(BASE_URL + "/login");

        wait.until(
                ExpectedConditions.urlContains("/login")
        );

        assertTrue(
                driver.getCurrentUrl().contains("/login"),
                "ควรอยู่ที่หน้า Login"
        );

        WebElement emailInput = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.name("email")
                )
        );

        WebElement passwordInput = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.name("password")
                )
        );

        assertTrue(
                emailInput.isDisplayed(),
                "ควรแสดงช่อง Email"
        );

        assertTrue(
                passwordInput.isDisplayed(),
                "ควรแสดงช่อง Password"
        );
    }

    /*
     * ============================================================
     * TC-FT-09-02
     * ตรวจสอบการกรอกข้อมูลเข้าสู่ระบบของ Admin
     * ============================================================
     */
    @Test
    void TC_FT_09_02_adminCanEnterLoginInformation() {

        loginPage();

        WebElement emailInput = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.name("email")
                )
        );

        WebElement passwordInput = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.name("password")
                )
        );

        emailInput.clear();
        emailInput.sendKeys(ADMIN_EMAIL);

        passwordInput.clear();
        passwordInput.sendKeys(ADMIN_PASSWORD);

        assertFalse(
                emailInput.getAttribute("value").isBlank(),
                "Email ต้องถูกกรอก"
        );

        assertFalse(
                passwordInput.getAttribute("value").isBlank(),
                "Password ต้องถูกกรอก"
        );
    }

    /*
     * ============================================================
     * TC-FT-09-03
     * ตรวจสอบการเข้าสู่ระบบ Admin สำเร็จ
     * ============================================================
     */
    @Test
    void TC_FT_09_03_adminCanLoginSuccessfully() {

        loginPage();

        performAdminLogin();

        wait.until(
                ExpectedConditions.urlToBe(
                        BASE_URL + "/admin/requests"
                )
        );

        assertTrue(
                driver.getCurrentUrl()
                        .equals(BASE_URL + "/admin/requests"),
                "Admin ควรถูก redirect ไปหน้า /admin/requests"
        );
    }

    /*
     * ============================================================
     * TC-FT-09-04
     * ตรวจสอบการเข้าถึงหน้ารายการแจ้งซ่อมทั้งหมด
     * ============================================================
     */
    @Test
    void TC_FT_09_04_adminCanAccessRepairRequestList() {

        loginPage();

        performAdminLogin();

        wait.until(
                ExpectedConditions.urlToBe(
                        BASE_URL + "/admin/requests"
                )
        );

        WebElement pageContent = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.tagName("body")
                )
        );

        assertTrue(
                pageContent.isDisplayed(),
                "หน้ารายการแจ้งซ่อมต้องแสดงผล"
        );

        assertTrue(
                driver.getCurrentUrl()
                        .equals(BASE_URL + "/admin/requests"),
                "URL ต้องเป็น /admin/requests"
        );
    }

    /*
     * ============================================================
     * TC-FT-09-05
     * ตรวจสอบการแสดงรายการแจ้งซ่อมทั้งหมด
     * ============================================================
     */
    @Test
    void TC_FT_09_05_adminCanViewAllRepairRequests() {

        loginPage();

        performAdminLogin();

        wait.until(
                ExpectedConditions.urlToBe(
                        BASE_URL + "/admin/requests"
                )
        );

        WebElement body = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.tagName("body")
                )
        );

        String pageText = body.getText();

        assertTrue(
                pageText.contains("ทั้งหมด"),
                "หน้าควรแสดงตัวกรองรายการทั้งหมด"
        );

        assertTrue(
                pageText.contains("รอดำเนินการ")
                        || pageText.contains("รออนุมัติ")
                        || pageText.contains("กำลังดำเนินการ")
                        || pageText.contains("เสร็จสิ้น")
                        || pageText.contains("ปฏิเสธ")
                        || pageText.contains("ยกเลิก"),
                "หน้าควรแสดงข้อมูลหรือสถานะของคำร้อง"
        );
    }

    /*
     * ============================================================
     * เปิดหน้า Login
     * ============================================================
     */
    private void loginPage() {

        driver.get(BASE_URL + "/login");

        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.name("email")
                )
        );

        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.name("password")
                )
        );
    }

    /*
     * ============================================================
     * Login ด้วยบัญชี Admin จริง
     * ============================================================
     */
    private void performAdminLogin() {

        WebElement emailInput = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.name("email")
                )
        );

        WebElement passwordInput = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.name("password")
                )
        );

        emailInput.clear();
        emailInput.sendKeys(ADMIN_EMAIL);

        passwordInput.clear();
        passwordInput.sendKeys(ADMIN_PASSWORD);

        WebElement loginButton = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector(
                                "button[type='submit']"
                        )
                )
        );

        loginButton.click();

        /*
        * รอให้ระบบ Login สำเร็จและเข้าสู่หน้า
        * Admin Repair Request Management โดยตรง
        */
        wait.until(
                ExpectedConditions.urlToBe(
                        BASE_URL + "/admin/requests"
                )
        );
    }
}