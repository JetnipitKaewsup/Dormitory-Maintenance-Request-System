package com.example.dormitory.functional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Functional Test - TS-13
 *
 * Scenario:
 * อัปเดตสถานะคำร้อง
 *
 * ทดสอบผ่าน Browser จริงด้วย Selenium
 * โดยใช้บัญชี Admin และตรวจสอบหน้า Inspect Work
 */
class AdminRepairRequestStatusFunctionalTest {

    private WebDriver driver;
    private WebDriverWait wait;

    private static final String BASE_URL =
            "http://localhost:8080";

    // ============================================================
    // Admin account ที่ใช้ทดสอบจริง
    // ============================================================
    private static final String ADMIN_EMAIL =
            "lucaenen03@gmail.com";

    private static final String ADMIN_PASSWORD =
            "adminnaib";

    @BeforeEach
    void setUp() {

        driver = new ChromeDriver();

        driver.manage()
                .window()
                .maximize();

        wait = new WebDriverWait(
                driver,
                Duration.ofSeconds(20)
        );
    }

    @AfterEach
    void tearDown() {

        if (driver != null) {
            driver.quit();
        }
    }

    // ============================================================
    // Helper: Login Admin
    // ============================================================
    private void performAdminLogin() {

        driver.get(BASE_URL + "/login");

        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.id("email")
                )
        );

        WebElement email =
                driver.findElement(By.id("email"));

        WebElement password =
                driver.findElement(By.id("password"));

        email.clear();
        email.sendKeys(ADMIN_EMAIL);

        password.clear();
        password.sendKeys(ADMIN_PASSWORD);

        /*
         * ใช้ form submit โดยตรง
         * เพื่อให้ทำงานเหมือนการ submit Login จริง
         */
        WebElement form =
                driver.findElement(
                        By.cssSelector("form")
                );

        ((JavascriptExecutor) driver)
                .executeScript(
                        "arguments[0].submit();",
                        form
                );

        /*
         * รอจนกว่าจะออกจาก /login
         */
        try {

            wait.until(
                    ExpectedConditions.not(
                            ExpectedConditions.urlContains(
                                    "/login"
                            )
                    )
            );

        } catch (Exception e) {

            /*
             * หากยังอยู่หน้า Login ให้ดึงข้อความ Error
             * เพื่อให้รู้สาเหตุจริงแทน Timeout เปล่า ๆ
             */
            String pageText =
                    driver.findElement(
                            By.tagName("body")
                    ).getText();

            fail(
                    "Admin Login ไม่สำเร็จ\n"
                            + "Current URL: "
                            + driver.getCurrentUrl()
                            + "\n"
                            + "Page text:\n"
                            + pageText,
                    e
            );
        }

        assertFalse(
                driver.getCurrentUrl().contains("/login"),
                "หลัง Login ระบบยังอยู่หน้า /login"
        );
    }

    // ============================================================
    // Helper: เปิดหน้า Inspect
    // ============================================================
    private void openFirstInspectPage() {

        /*
         * ใช้หน้า Admin Inspection ตาม Workflow จริง
         */
        driver.get(
                BASE_URL + "/admin/inspections"
        );

        wait.until(
                ExpectedConditions.not(
                        ExpectedConditions.urlContains(
                                "/login"
                        )
                )
        );

        wait.until(
                ExpectedConditions.urlContains(
                        "/admin/inspections"
                )
        );

        /*
         * ค้นหา Link ที่มีรูปแบบ:
         *
         * /admin/requests/{UUID}/inspect
         */
        List<WebElement> inspectLinks =
                driver.findElements(
                        By.cssSelector(
                                "a[href*='/admin/requests/']"
                        )
                );

        String inspectUrl = null;

        for (WebElement link : inspectLinks) {

            String href =
                    link.getAttribute("href");

            if (href != null
                    && href.matches(
                    ".*/admin/requests/"
                            + "[0-9a-fA-F-]{36}"
                            + "/inspect$"
            )) {

                inspectUrl = href;
                break;
            }
        }

        /*
         * หากหน้า assignments ไม่มี Link
         * ให้ตรวจจากทุก element ที่มี href
         */
        if (inspectUrl == null) {

            List<WebElement> allElements =
                    driver.findElements(
                            By.cssSelector("[href]")
                    );

            for (WebElement element : allElements) {

                String href =
                        element.getAttribute("href");

                if (href != null
                        && href.matches(
                        ".*/admin/requests/"
                                + "[0-9a-fA-F-]{36}"
                                + "/inspect$"
                )) {

                    inspectUrl = href;
                    break;
                }
            }
        }

        assertNotNull(
                inspectUrl,
                "ไม่พบคำร้องที่สามารถเข้าสู่หน้าตรวจสอบงานได้ "
                        + "จากหน้า /admin/inspections"
        );

        /*
         * เปิดหน้า Inspect โดยตรง
         */
        driver.get(inspectUrl);

        wait.until(
                ExpectedConditions.urlMatches(
                        ".*/admin/requests/"
                                + "[0-9a-fA-F-]{36}"
                                + "/inspect$"
                )
        );
    }

    // ============================================================
    // TC-FT-13-01
    // ตรวจสอบการเปิดหน้า Login
    // ============================================================
    @Test
    void TC_FT_13_01_userCanOpenLoginPage() {

        driver.get(
                BASE_URL + "/login"
        );

        WebElement email =
                wait.until(
                        ExpectedConditions
                                .visibilityOfElementLocated(
                                        By.id("email")
                                )
                );

        WebElement password =
                wait.until(
                        ExpectedConditions
                                .visibilityOfElementLocated(
                                        By.id("password")
                                )
                );

        assertTrue(email.isDisplayed());
        assertTrue(password.isDisplayed());
    }

    // ============================================================
    // TC-FT-13-02
    // ตรวจสอบการกรอกข้อมูลเข้าสู่ระบบของ Admin
    // ============================================================
    @Test
    void TC_FT_13_02_adminCanEnterLoginCredentials() {

        driver.get(
                BASE_URL + "/login"
        );

        WebElement email =
                wait.until(
                        ExpectedConditions
                                .visibilityOfElementLocated(
                                        By.id("email")
                                )
                );

        WebElement password =
                driver.findElement(
                        By.id("password")
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

    // ============================================================
    // TC-FT-13-03
    // ตรวจสอบการเข้าสู่ระบบ Admin สำเร็จ
    // ============================================================
    @Test
    void TC_FT_13_03_adminCanLoginSuccessfully() {

        performAdminLogin();

        String currentUrl =
                driver.getCurrentUrl();

        assertFalse(
                currentUrl.contains("/login"),
                "Login ไม่สำเร็จ"
        );

        assertTrue(
                currentUrl.contains("/admin"),
                "หลัง Login ไม่ได้เข้าสู่หน้า Admin"
        );
    }

    // ============================================================
    // TC-FT-13-04
    // ตรวจสอบการเปิดหน้าตรวจสอบงาน
    // ============================================================
    @Test
    void TC_FT_13_04_adminCanOpenInspectPage() {

        performAdminLogin();

        openFirstInspectPage();

        String currentUrl =
                driver.getCurrentUrl();

        assertTrue(
                currentUrl.matches(
                        ".*/admin/requests/"
                                + "[0-9a-fA-F-]{36}"
                                + "/inspect$"
                ),
                "ไม่สามารถเปิดหน้า Inspect Work ได้"
        );

        WebElement heading =
                wait.until(
                        ExpectedConditions
                                .visibilityOfElementLocated(
                                        By.tagName("h1")
                                )
                );

        assertTrue(
                heading.isDisplayed(),
                "ไม่พบหัวข้อหน้าตรวจสอบงาน"
        );
    }

    // ============================================================
    // TC-FT-13-05
    // ตรวจสอบข้อมูลสถานะและ Form ยืนยันการดำเนินงาน
    // ============================================================
    @Test
    void TC_FT_13_05_adminCanViewStatusAndCompletionForm() {

        performAdminLogin();

        openFirstInspectPage();

        WebElement body =
                wait.until(
                        ExpectedConditions
                                .visibilityOfElementLocated(
                                        By.tagName("body")
                                )
                );

        String pageText =
                body.getText();

        assertFalse(
                pageText.isBlank(),
                "หน้า Inspect Work ไม่มีข้อมูล"
        );

        /*
         * ตรวจสอบว่ามีข้อมูลเกี่ยวกับสถานะ
         */
        boolean hasStatus =
                pageText.contains("สถานะ")
                        || pageText.contains("ดำเนินการ")
                        || pageText.contains("เสร็จ");

        assertTrue(
                hasStatus,
                "ไม่พบข้อมูลสถานะของคำร้อง"
        );

        /*
         * Controller:
         *
         * POST /admin/requests/{id}/inspect
         *
         * ดังนั้นตรวจสอบ Form ที่ส่งไปยัง endpoint นี้
         */
        List<WebElement> forms =
                driver.findElements(
                        By.tagName("form")
                );

        WebElement inspectForm = null;

        for (WebElement form : forms) {

            String action =
                    form.getAttribute("action");

            if (action != null
                    && action.matches(
                    ".*/admin/requests/"
                            + "[0-9a-fA-F-]{36}"
                            + "/inspect$"
            )) {

                inspectForm = form;
                break;
            }
        }

        assertNotNull(
                inspectForm,
                "ไม่พบ Form สำหรับยืนยันการดำเนินงานเสร็จสิ้น"
        );

        assertTrue(
                inspectForm.isDisplayed(),
                "Form สำหรับยืนยันการดำเนินงานไม่แสดง"
        );

        /*
         * ตรวจสอบว่ามีช่อง Note สำหรับ Admin
         * ซึ่ง Controller รับ @RequestParam("note")
         */
        List<WebElement> noteFields =
                inspectForm.findElements(
                        By.cssSelector(
                                "textarea[name='note'], "
                                        + "input[name='note']"
                        )
                );

        assertFalse(
                noteFields.isEmpty(),
                "ไม่พบช่องหมายเหตุสำหรับการยืนยันงาน"
        );
    }
}
