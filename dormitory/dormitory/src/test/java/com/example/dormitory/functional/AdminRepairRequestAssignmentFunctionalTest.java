package com.example.dormitory.functional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;

class AdminRepairRequestAssignmentFunctionalTest {

    private static final String BASE_URL =
            "http://localhost:8080";

    private static final String ADMIN_EMAIL =
            "lucaenen03@gmail.com";

    private static final String ADMIN_PASSWORD =
            "adminnaib";

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    void setUp() {

        driver = new ChromeDriver();

        wait = new WebDriverWait(
                driver,
                Duration.ofSeconds(60)
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

    // =========================================================
    // TC-FT-12-01
    // ตรวจสอบการเปิดหน้า Login
    // =========================================================
    @Test
    void adminCanOpenLoginPage() {

        driver.get(BASE_URL + "/login");

        WebElement email =
                wait.until(
                        ExpectedConditions.visibilityOfElementLocated(
                                By.name("email")
                        )
                );

        WebElement password =
                wait.until(
                        ExpectedConditions.visibilityOfElementLocated(
                                By.name("password")
                        )
                );

        assertTrue(
                driver.getCurrentUrl().contains("/login")
        );

        assertTrue(email.isDisplayed());
        assertTrue(password.isDisplayed());
    }

    // =========================================================
    // TC-FT-12-02
    // ตรวจสอบการกรอกข้อมูลเข้าสู่ระบบของ Admin
    // =========================================================
    @Test
    void adminCanEnterLoginInformation() {

        driver.get(BASE_URL + "/login");

        WebElement email =
                wait.until(
                        ExpectedConditions.visibilityOfElementLocated(
                                By.name("email")
                        )
                );

        WebElement password =
                wait.until(
                        ExpectedConditions.visibilityOfElementLocated(
                                By.name("password")
                        )
                );

        email.clear();
        email.sendKeys(ADMIN_EMAIL);

        password.clear();
        password.sendKeys(ADMIN_PASSWORD);

        assertTrue(
                ADMIN_EMAIL.equals(
                        email.getAttribute("value")
                )
        );

        assertTrue(
                ADMIN_PASSWORD.equals(
                        password.getAttribute("value")
                )
        );
    }

    // =========================================================
    // TC-FT-12-03
    // ตรวจสอบการเข้าสู่ระบบ Admin สำเร็จ
    // =========================================================
    @Test
    void adminCanLoginSuccessfully() {

        performAdminLogin();

        assertTrue(
                driver.getCurrentUrl()
                        .contains("/admin/requests")
        );

        assertTrue(
                !driver.getCurrentUrl()
                        .contains("/login")
        );
    }

    // =========================================================
    // TC-FT-12-04
    // ตรวจสอบการเปิดหน้ามอบหมายงานให้ช่าง
    // =========================================================
    @Test
    void adminCanOpenAssignmentPage() {

        performAdminLogin();

        openFirstRepairRequest();

        String requestUrl =
                driver.getCurrentUrl();

        assertTrue(
                requestUrl.matches(
                        ".*/admin/requests/[0-9a-fA-F-]{36}$"
                )
        );

        driver.get(
                requestUrl + "/assign"
        );

        wait.until(
                ExpectedConditions.urlMatches(
                        ".*/admin/requests/[0-9a-fA-F-]{36}/assign$"
                )
        );

        String pageSource =
                driver.getPageSource();

        assertTrue(
                pageSource.contains("มอบหมาย")
                        || pageSource.contains("ช่าง")
                        || pageSource.contains("เลือกช่าง")
        );

        assertTrue(
                !pageSource.contains(
                        "Whitelabel Error Page"
                )
        );

        assertTrue(
                !pageSource.contains(
                        "There was an unexpected error"
                )
        );
    }

    // =========================================================
    // TC-FT-12-05
    // ตรวจสอบการแสดงข้อมูลสำหรับมอบหมายงาน
    // =========================================================
    @Test
    void adminCanViewTechnicianAssignmentForm() {

        performAdminLogin();

        openFirstRepairRequest();

        String requestUrl =
                driver.getCurrentUrl();

        driver.get(
                requestUrl + "/assign"
        );

        wait.until(
                ExpectedConditions.urlMatches(
                        ".*/admin/requests/[0-9a-fA-F-]{36}/assign$"
                )
        );

        String pageSource =
                driver.getPageSource();

        boolean hasAssignmentContent =
                pageSource.contains("มอบหมาย")
                        || pageSource.contains("เลือกช่าง")
                        || pageSource.contains("ช่าง");

        assertTrue(
                hasAssignmentContent
        );

        assertTrue(
                !pageSource.contains(
                        "Whitelabel Error Page"
                )
        );

        assertTrue(
                !pageSource.contains(
                        "There was an unexpected error"
                )
        );
    }

    // =========================================================
    // Helper: Login Admin
    // =========================================================
    private void performAdminLogin() {

        driver.get(BASE_URL + "/login");

        WebElement email =
                wait.until(
                        ExpectedConditions.visibilityOfElementLocated(
                                By.name("email")
                        )
                );

        WebElement password =
                wait.until(
                        ExpectedConditions.visibilityOfElementLocated(
                                By.name("password")
                        )
                );

        email.clear();
        email.sendKeys(ADMIN_EMAIL);

        password.clear();
        password.sendKeys(ADMIN_PASSWORD);

        WebElement loginButton =
                wait.until(
                        ExpectedConditions.elementToBeClickable(
                                By.cssSelector(
                                        "button[type='submit']"
                                )
                        )
                );

        loginButton.click();

        // รอให้ Login สำเร็จและออกจาก /login
        wait.until(
                ExpectedConditions.not(
                        ExpectedConditions.urlContains("/login")
                )
        );

        // รอให้เข้าสู่หน้า Admin Request Management
        wait.until(
                ExpectedConditions.urlContains(
                        "/admin/requests"
                )
        );
    }

    // =========================================================
    // Helper: เปิดคำร้องรายการแรก
    // =========================================================
    private void openFirstRepairRequest() {

        wait.until(
                ExpectedConditions.urlToBe(
                        BASE_URL + "/admin/requests"
                )
        );

        WebElement requestLink =
                wait.until(
                        ExpectedConditions.elementToBeClickable(
                                By.cssSelector(
                                        "a[href^='/admin/requests/']"
                                )
                        )
                );

        String href =
                requestLink.getAttribute("href");

        assertTrue(
                href != null
                        && href.matches(
                        ".*/admin/requests/[0-9a-fA-F-]{36}$"
                )
        );

        driver.get(href);

        // รอ URL รายละเอียดคำร้อง
        wait.until(
                ExpectedConditions.urlMatches(
                        ".*/admin/requests/[0-9a-fA-F-]{36}$"
                )
        );
    }
}
