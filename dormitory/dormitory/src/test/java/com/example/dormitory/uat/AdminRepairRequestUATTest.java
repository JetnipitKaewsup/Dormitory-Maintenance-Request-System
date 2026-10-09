package com.example.dormitory.uat;

import java.time.Duration;

import org.junit.jupiter.api.AfterEach;
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
 * TS-09
 * ดูรายการแจ้งซ่อมทั้งหมด
 *
 * UAT Test
 *
 * ทดสอบการใช้งานจริงของ Admin ผ่าน Web Browser
 * โดยใช้ระบบจริงและบัญชี Admin จริง
 */
public class AdminRepairRequestUATTest {

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

        wait = new WebDriverWait(
                driver,
                Duration.ofSeconds(40)
        );

        driver.manage().window().maximize();

        driver.get(BASE_URL + "/login");
    }

    @AfterEach
    void tearDown() {

        if (driver != null) {
            driver.quit();
        }
    }

    /**
     * Login ด้วยบัญชี Admin จริง
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

        /*
        * ตรวจสอบว่าข้อมูลถูกกรอกก่อน Submit
        */
        wait.until(driver ->
                ADMIN_EMAIL.equals(
                        emailInput.getAttribute("value")
                )
        );

        wait.until(driver ->
                ADMIN_PASSWORD.equals(
                        passwordInput.getAttribute("value")
                )
        );

        loginButton.click();

        /*
        * รอให้ระบบ Login และ Redirect ไปหน้าของ Admin
        */
        wait.until(
                ExpectedConditions.urlToBe(
                        BASE_URL + "/admin/requests"
                )
        );
    }

    /**
     * TC-UAT-09-01
     * ตรวจสอบการเปิดหน้า Login
     */
    @Test
    void TC_UAT_09_01_adminCanOpenLoginPage() {

        assertTrue(
                driver.getCurrentUrl().contains("/login"),
                "ควรเปิดหน้า Login ได้"
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
    /**
     * TC-UAT-09-02
     * ตรวจสอบการกรอกข้อมูลเข้าสู่ระบบของ Admin
     */
    @Test
    void TC_UAT_09_02_adminCanEnterLoginInformation() {

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

        emailInput.sendKeys(ADMIN_EMAIL);
        passwordInput.sendKeys(ADMIN_PASSWORD);

        assertTrue(
                emailInput.getAttribute("value")
                        .equals(ADMIN_EMAIL),
                "Email ต้องถูกกรอกถูกต้อง"
        );

        assertTrue(
                passwordInput.getAttribute("value")
                        .equals(ADMIN_PASSWORD),
                "Password ต้องถูกกรอก"
        );
    }

    /**
     * TC-UAT-09-03
     * ตรวจสอบการเข้าสู่ระบบ Admin สำเร็จ
     */
    @Test
    void TC_UAT_09_03_adminCanLoginSuccessfully() {

        performAdminLogin();

        assertTrue(
                driver.getCurrentUrl().equals(
                        BASE_URL + "/admin/requests"
                ),
                "หลัง Login ต้องเข้าสู่หน้าจัดการคำร้อง"
        );
    }

    /**
     * TC-UAT-09-04
     * ตรวจสอบการเข้าถึงหน้ารายการแจ้งซ่อมทั้งหมด
     */
    @Test
    void TC_UAT_09_04_adminCanAccessRepairRequestList() {

        performAdminLogin();

        wait.until(
                ExpectedConditions.presenceOfElementLocated(
                        By.tagName("body")
                )
        );

        assertTrue(
                driver.getCurrentUrl().equals(
                        BASE_URL + "/admin/requests"
                ),
                "Admin ต้องเข้าถึงหน้ารายการแจ้งซ่อมได้"
        );

        assertTrue(
                driver.getPageSource().contains("แจ้งซ่อม")
                        || driver.getPageSource().contains("คำร้อง"),
                "หน้าต้องมีข้อมูลเกี่ยวกับรายการแจ้งซ่อม"
        );
    }


    /**
     * TC-UAT-09-05
     * ตรวจสอบการแสดงรายการแจ้งซ่อมทั้งหมด
     */
    @Test
    void TC_UAT_09_05_adminCanViewAllRepairRequests() {

        performAdminLogin();

        /*
        * รอให้หน้า Admin โหลดเสร็จ
        */
        wait.until(
                ExpectedConditions.presenceOfElementLocated(
                        By.tagName("body")
                )
        );

        /*
        * ตรวจสอบ URL ว่าเป็นหน้ารายการแจ้งซ่อม
        */
        assertTrue(
                driver.getCurrentUrl().equals(
                        BASE_URL + "/admin/requests"
                ),
                "Admin ต้องอยู่ที่หน้ารายการแจ้งซ่อม"
        );

        String pageSource = driver.getPageSource();

        /*
        * ตรวจสอบว่าหน้ามีเนื้อหาที่เกี่ยวข้องกับ
        * รายการแจ้งซ่อม
        */
        boolean hasRepairRequestContent =
                pageSource.contains("แจ้งซ่อม")
                        || pageSource.contains("คำร้อง")
                        || pageSource.contains("สถานะ");

        assertTrue(
                hasRepairRequestContent,
                "หน้าต้องแสดงข้อมูลรายการแจ้งซ่อม"
        );

        /*
        * ตรวจสอบว่าไม่ใช่หน้า Error ของ Spring Boot
        */
        assertTrue(
                !pageSource.contains("Whitelabel Error Page")
                        && !pageSource.contains(
                                "There was an unexpected error"
                        ),
                "หน้ารายการแจ้งซ่อมต้องไม่เกิด Error"
        );
    }

}
