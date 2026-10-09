package com.example.dormitory.uat;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * TS-11
 * ตรวจสอบและพิจารณาคำร้อง
 *
 * UAT Test สำหรับการตรวจสอบและพิจารณาคำร้องของ Admin
 */
class AdminRepairRequestReviewUATTest {

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

        ChromeOptions options = new ChromeOptions();

        driver = new ChromeDriver(options);

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

    /**
     * TC-UAT-11-01
     * เปิดหน้า Login
     */
    @Test
    void TC_UAT_11_01_adminCanOpenLoginPage() {

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

        assertTrue(
                driver.getCurrentUrl().contains("/login")
        );
    }

    /**
     * TC-UAT-11-02
     * กรอก Email และ Password ของ Admin
     */
    @Test
    void TC_UAT_11_02_adminCanEnterLoginInformation() {

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
        password.clear();

        email.sendKeys(ADMIN_EMAIL);
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

    /**
     * TC-UAT-11-03
     * ตรวจสอบการเข้าสู่ระบบด้วยบัญชี Admin สำเร็จ
     */
    @Test
    void TC_UAT_11_03_adminCanLoginSuccessfully() {

        performAdminLogin();

        assertTrue(
                driver.getCurrentUrl()
                        .equals(BASE_URL + "/admin/requests")
        );
    }

    /**
     * TC-UAT-11-04
     * ตรวจสอบการเข้าถึงรายละเอียดคำร้อง
     */
    @Test
    void TC_UAT_11_04_adminCanAccessRepairRequestDetail() {

        performAdminLogin();

        openFirstRepairRequest();

        assertTrue(
                driver.getCurrentUrl()
                        .contains("/admin/requests/")
        );

        String pageSource =
                driver.getPageSource();

        assertTrue(
                pageSource.contains(
                        "รายละเอียดคำร้องแจ้งซ่อม"
                )
                        || pageSource.contains(
                                "ข้อมูลผู้แจ้ง"
                        )
                        || pageSource.contains(
                                "รายละเอียดงานซ่อม"
                        )
        );
    }

    /**
     * TC-UAT-11-05
     * ตรวจสอบการพิจารณาคำร้อง
     */
    @Test
    void TC_UAT_11_05_adminCanReviewRepairRequest() {

        performAdminLogin();

        openFirstRepairRequest();

        String pageSource =
                driver.getPageSource();

        boolean hasApprove =
                pageSource.contains("approve")
                        || pageSource.contains("อนุมัติ");

        boolean hasReject =
                pageSource.contains("reject")
                        || pageSource.contains("ปฏิเสธ");

        assertTrue(
                hasApprove || hasReject,
                "ไม่พบปุ่มอนุมัติหรือปฏิเสธคำร้อง"
        );

        assertTrue(
                !pageSource.contains(
                        "Whitelabel Error Page"
                )
                        && !pageSource.contains(
                                "There was an unexpected error"
                        )
        );
    }

    /**
     * Login ด้วยบัญชี Admin จริง
     */
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
        password.clear();

        email.sendKeys(ADMIN_EMAIL);
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

        wait.until(
                ExpectedConditions.urlToBe(
                        BASE_URL + "/admin/requests"
                )
        );
    }

    /**
     * เปิดรายละเอียดคำร้องรายการแรก
     */
    private void openFirstRepairRequest() {

        wait.until(
                ExpectedConditions.urlToBe(
                        BASE_URL + "/admin/requests"
                )
        );

        WebElement firstRequest =
                wait.until(
                        ExpectedConditions.elementToBeClickable(
                                By.cssSelector(
                                        "a[href^='/admin/requests/']"
                                )
                        )
                );

        firstRequest.click();

        wait.until(
                ExpectedConditions.urlContains(
                        "/admin/requests/"
                )
        );
    }
}