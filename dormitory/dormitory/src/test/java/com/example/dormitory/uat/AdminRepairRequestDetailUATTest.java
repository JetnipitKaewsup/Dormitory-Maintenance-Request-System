package com.example.dormitory.uat;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;

import org.junit.jupiter.api.AfterEach;
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
 * User Acceptance Test (UAT)
 * สำหรับผู้ใช้งานระบบในบทบาท Admin
 */
class AdminRepairRequestDetailUATTest {

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
                Duration.ofSeconds(40)
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
     * TC-UAT-10-01
     * ตรวจสอบการเปิดหน้า Login
     */
    @Test
    void TC_UAT_10_01_adminCanOpenLoginPage() {

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

        assertTrue(email.isDisplayed());
        assertTrue(password.isDisplayed());
    }

    /**
     * TC-UAT-10-02
     * ตรวจสอบการกรอกข้อมูลเข้าสู่ระบบของ Admin
     */
    @Test
    void TC_UAT_10_02_adminCanEnterLoginInformation() {

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
                email.getAttribute("value")
                        .equals(ADMIN_EMAIL)
        );

        assertTrue(
                password.getAttribute("value")
                        .equals(ADMIN_PASSWORD)
        );
    }

    /**
     * TC-UAT-10-03
     * ตรวจสอบการเข้าสู่ระบบ Admin สำเร็จ
     */
    @Test
    void TC_UAT_10_03_adminCanLoginSuccessfully() {

        performAdminLogin();

        assertTrue(
                driver.getCurrentUrl()
                        .equals(
                                BASE_URL + "/admin/requests"
                        )
        );
    }

    /**
     * TC-UAT-10-04
     * ตรวจสอบการเปิดหน้ารายละเอียดคำร้อง
     */
    @Test
    void TC_UAT_10_04_adminCanOpenRepairRequestDetail() {

        performAdminLogin();

        openFirstRepairRequest();

        assertTrue(
                driver.getCurrentUrl()
                        .contains(
                                "/admin/requests/"
                        )
        );

        assertFalse(
                driver.getCurrentUrl()
                        .equals(
                                BASE_URL + "/login"
                        )
        );
    }

    /**
     * TC-UAT-10-05
     * ตรวจสอบการแสดงรายละเอียดคำร้อง
     */
    @Test
    void TC_UAT_10_05_adminCanViewRepairRequestDetail() {

        performAdminLogin();

        openFirstRepairRequest();

        wait.until(
                ExpectedConditions.urlContains(
                        "/admin/requests/"
                )
        );

        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(".page-title")
                )
        );

        String pageSource =
                driver.getPageSource();

        /*
         * ตรวจสอบว่าหน้ารายละเอียดคำร้อง
         * แสดงข้อมูลสำคัญสำหรับผู้ใช้งาน Admin
         */
        assertTrue(
                pageSource.contains(
                        "รายละเอียดคำร้องแจ้งซ่อม"
                )
        );

        assertTrue(
                pageSource.contains(
                        "ข้อมูลผู้แจ้ง"
                )
        );

        assertTrue(
                pageSource.contains(
                        "รายละเอียดงานซ่อม"
                )
        );

        assertTrue(
                pageSource.contains(
                        "ประเภทงานซ่อม"
                )
        );

        assertTrue(
                pageSource.contains(
                        "รายละเอียดปัญหา"
                )
        );

        assertTrue(
                pageSource.contains(
                        "สถานะปัจจุบัน"
                )
        );

        /*
         * ตรวจสอบว่าไม่ใช่ Error Page
         */
        assertFalse(
                pageSource.contains(
                        "Whitelabel Error Page"
                )
        );

        assertFalse(
                pageSource.contains(
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

        /*
         * รอให้ Login สำเร็จและเข้าสู่หน้าจัดการคำร้อง
         */
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

        WebElement requestLink =
                wait.until(
                        ExpectedConditions.elementToBeClickable(
                                By.cssSelector(
                                        "a[href^='/admin/requests/']"
                                )
                        )
                );

        String detailUrl =
                requestLink.getAttribute("href");

        assertTrue(
                detailUrl.startsWith(
                        BASE_URL + "/admin/requests/"
                )
        );

        requestLink.click();

        wait.until(
                ExpectedConditions.urlContains(
                        "/admin/requests/"
                )
        );
    }
}