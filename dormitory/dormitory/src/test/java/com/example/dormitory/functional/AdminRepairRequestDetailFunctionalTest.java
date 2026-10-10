package com.example.dormitory.functional;

import java.net.URI;
import java.time.Duration;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
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

/**
 * TS-10
 * ดูรายละเอียดคำร้อง
 *
 * Functional Test สำหรับ Admin
 * ใช้ Selenium ทดสอบการทำงานผ่าน Browser จริง
 */
class AdminRepairRequestDetailFunctionalTest {

    private static final String BASE_URL =
            "http://localhost:8080";

    private static final String ADMIN_EMAIL =
            "lucaenen03@gmail.com";

    private static final String ADMIN_PASSWORD =
            "adminnaib";

    private static final Duration WAIT_TIMEOUT =
            Duration.ofSeconds(40);

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    void setUp() {

        driver = new ChromeDriver();

        wait = new WebDriverWait(
                driver,
                WAIT_TIMEOUT
        );

        driver.manage()
                .window()
                .maximize();
    }

    @AfterEach
    void tearDown() {

        if (driver != null) {
            driver.quit();
        }
    }

    /**
     * TC-FT-10-01
     * ตรวจสอบการเปิดหน้า Login
     */
    @Test
    void TC_FT_10_01_adminCanOpenLoginPage() {

        driver.get(BASE_URL + "/login");

        WebElement email = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.name("email")
                )
        );

        WebElement password = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.name("password")
                )
        );

        assertTrue(email.isDisplayed());
        assertTrue(password.isDisplayed());
    }

    /**
     * TC-FT-10-02
     * ตรวจสอบการกรอกข้อมูลเข้าสู่ระบบของ Admin
     */
    @Test
    void TC_FT_10_02_adminCanEnterLoginInformation() {

        driver.get(BASE_URL + "/login");

        WebElement email = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.name("email")
                )
        );

        WebElement password = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.name("password")
                )
        );

        email.clear();
        email.sendKeys(ADMIN_EMAIL);

        password.clear();
        password.sendKeys(ADMIN_PASSWORD);

        assertEquals(
                ADMIN_EMAIL,
                email.getAttribute("value")
        );

        assertEquals(
                ADMIN_PASSWORD,
                password.getAttribute("value")
        );
    }

    /**
     * TC-FT-10-03
     * ตรวจสอบการเข้าสู่ระบบ Admin สำเร็จ
     */
    @Test
    void TC_FT_10_03_adminCanLoginSuccessfully() {

        performAdminLogin();

        assertEquals(
                BASE_URL + "/admin/requests",
                driver.getCurrentUrl()
        );
    }

    /**
     * TC-FT-10-04
     * ตรวจสอบการเปิดหน้ารายละเอียดคำร้อง
     */
    @Test
    void TC_FT_10_04_adminCanOpenRepairRequestDetail() {

        performAdminLogin();

        openFirstRepairRequest();

        assertTrue(
                isRepairRequestDetailUrl(
                        driver.getCurrentUrl()
                ),
                "URL ควรเป็นหน้ารายละเอียดคำร้อง /admin/requests/{id}"
        );
    }

    /**
     * TC-FT-10-05
     * ตรวจสอบการแสดงรายละเอียดของคำร้อง
     */
    @Test
    void TC_FT_10_05_adminCanViewRepairRequestDetail() {

        performAdminLogin();

        openFirstRepairRequest();

        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(".page-title")
                )
        );

        String pageSource = driver.getPageSource();

        assertTrue(
                pageSource.contains("รายละเอียดคำร้องแจ้งซ่อม"),
                "ควรแสดงหัวข้อรายละเอียดคำร้องแจ้งซ่อม"
        );

        assertTrue(
                pageSource.contains("ข้อมูลผู้แจ้ง"),
                "ควรแสดงส่วนข้อมูลผู้แจ้ง"
        );

        assertTrue(
                pageSource.contains("รายละเอียดงานซ่อม"),
                "ควรแสดงส่วนรายละเอียดงานซ่อม"
        );

        assertTrue(
                pageSource.contains("รายละเอียดปัญหา"),
                "ควรแสดงส่วนรายละเอียดปัญหา"
        );

        assertFalse(
                pageSource.contains("Whitelabel Error Page"),
                "ไม่ควรแสดงหน้า Whitelabel Error"
        );

        assertFalse(
                pageSource.contains("There was an unexpected error"),
                "ไม่ควรแสดงข้อความข้อผิดพลาดของระบบ"
        );
    }

    /**
     * Login ด้วยบัญชี Admin
     * และรอให้ระบบ redirect ไปหน้ารายการคำร้อง
     */
    private void performAdminLogin() {

        driver.get(BASE_URL + "/login");

        WebElement email = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.name("email")
                )
        );

        WebElement password = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.name("password")
                )
        );

        email.clear();
        email.sendKeys(ADMIN_EMAIL);

        password.clear();
        password.sendKeys(ADMIN_PASSWORD);

        WebElement loginButton = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector("button[type='submit']")
                )
        );

        loginButton.click();

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

    /**
     * เปิดรายละเอียดคำร้องรายการแรกจากหน้ารายการ
     */
    private void openFirstRepairRequest() {

        wait.until(
                ExpectedConditions.urlToBe(
                        BASE_URL + "/admin/requests"
                )
        );

        WebElement requestLink = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector(
                                "a[href^='/admin/requests/']"
                        )
                )
        );

        String detailUrl = requestLink.getAttribute("href");

        assertTrue(
                isRepairRequestDetailUrl(detailUrl),
                "Link ต้องชี้ไปยังหน้ารายละเอียดคำร้อง"
        );

        requestLink.click();

        wait.until(
                driver -> isRepairRequestDetailUrl(
                        driver.getCurrentUrl()
                )
        );
    }

    /**
     * ตรวจสอบว่า URL เป็น /admin/requests/{id}
     * โดยไม่รวม URL ของหน้ารายการ /admin/requests
     */
    private boolean isRepairRequestDetailUrl(String url) {

        if (url == null || url.isBlank()) {
            return false;
        }

        try {
            URI uri = URI.create(url);

            String expectedOrigin = URI.create(BASE_URL).getAuthority();

            String actualOrigin = uri.getAuthority();

            String path = uri.getPath();

            if (!URI.create(BASE_URL).getScheme().equals(uri.getScheme())
                    || !expectedOrigin.equals(actualOrigin)
                    || path == null) {
                return false;
            }

            return path.matches(
                    "/admin/requests/[^/]+/?"
            );

        } catch (IllegalArgumentException exception) {
            return false;
        }
    }
}
