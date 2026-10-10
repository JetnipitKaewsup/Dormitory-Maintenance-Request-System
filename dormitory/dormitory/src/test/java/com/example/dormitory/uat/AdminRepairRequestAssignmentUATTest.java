package com.example.dormitory.uat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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

class AdminRepairRequestAssignmentUATTest {

    private WebDriver driver;
    private WebDriverWait wait;

    private static final String BASE_URL =
            "http://localhost:8080";

    private static final String ADMIN_EMAIL =
            "lucaenen03@gmail.com";

    private static final String ADMIN_PASSWORD =
            "adminnaib";

    // =========================================================
    // Setup
    // =========================================================

    @BeforeEach
    void setUp() {

        driver = new ChromeDriver();

        driver.manage()
                .window()
                .maximize();

        wait = new WebDriverWait(
                driver,
                Duration.ofSeconds(30)
        );

        driver.manage()
                .timeouts()
                .implicitlyWait(Duration.ofSeconds(2));
    }

    // =========================================================
    // Teardown
    // =========================================================

    @AfterEach
    void tearDown() {

        if (driver != null) {
            try {
                driver.quit();
            } catch (Exception ignored) {
                // ป้องกันกรณี Chrome ปิดตัวเองก่อน test จบ
            }
        }
    }

    // =========================================================
    // TC-UAT-12-01
    // เปิดหน้า Login
    // =========================================================

    @Test
    @DisplayName("TC-UAT-12-01 เปิดหน้า Login")
    void adminCanOpenLoginPage() {

        driver.get(BASE_URL + "/login");

        wait.until(
                ExpectedConditions.urlContains("/login")
        );

        assertTrue(
                driver.getCurrentUrl().contains("/login"),
                "ต้องสามารถเปิดหน้า Login ได้"
        );

        WebElement body = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.tagName("body")
                )
        );

        assertTrue(
                body.isDisplayed(),
                "หน้า Login ต้องแสดงผล"
        );
    }

    // =========================================================
    // TC-UAT-12-02
    // กรอก Email และ Password ของ Admin
    // =========================================================

    @Test
    @DisplayName("TC-UAT-12-02 กรอกข้อมูลเข้าสู่ระบบของ Admin")
    void adminCanEnterLoginInformation() {

        driver.get(BASE_URL + "/login");

        wait.until(
                ExpectedConditions.urlContains("/login")
        );

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
                email.getAttribute("value"),
                "Email ต้องตรงกับข้อมูลที่กรอก"
        );

        assertEquals(
                ADMIN_PASSWORD,
                password.getAttribute("value"),
                "Password ต้องตรงกับข้อมูลที่กรอก"
        );
    }

    // =========================================================
    // TC-UAT-12-03
    // ตรวจสอบการเข้าสู่ระบบ Admin สำเร็จ
    // =========================================================

    @Test
    @DisplayName("TC-UAT-12-03 Admin สามารถเข้าสู่ระบบสำเร็จ")
    void adminCanLoginSuccessfully() {

        performAdminLogin();

        String currentUrl =
                driver.getCurrentUrl();

        assertFalse(
                currentUrl.contains("/login"),
                "หลัง Login สำเร็จต้องไม่อยู่หน้า Login"
        );

        assertTrue(
                currentUrl.contains("/admin/requests"),
                "หลัง Login สำเร็จต้องเข้าสู่หน้า Admin Requests"
        );
    }

    // =========================================================
    // TC-UAT-12-04
    // เปิดหน้ามอบหมายงานให้ช่าง
    // =========================================================

    @Test
    @DisplayName("TC-UAT-12-04 เปิดหน้ามอบหมายงานให้ช่าง")
    void adminCanAccessAssignmentPage() {

        performAdminLogin();

        // เปิดคำร้องรายการแรก
        openFirstRepairRequest();

        // เปิด endpoint Assign โดยตรง
        // Controller:
        // GET /admin/requests/{id}/assign
        openAssignmentPage();

        WebElement assignPage = wait.until(
                ExpectedConditions.presenceOfElementLocated(
                        By.cssSelector(".assign-page")
                )
        );

        assertTrue(
                assignPage.isDisplayed(),
                "ต้องแสดงหน้ามอบหมายงาน"
        );

        WebElement heading = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(".assign-head h1")
                )
        );

        assertEquals(
                "มอบหมายงานให้ช่าง",
                heading.getText().trim(),
                "หัวข้อหน้าต้องเป็น มอบหมายงานให้ช่าง"
        );
    }

    // =========================================================
    // TC-UAT-12-05
    // ตรวจสอบแบบฟอร์มมอบหมายงานให้ช่าง
    // =========================================================

    @Test
    @DisplayName("TC-UAT-12-05 ตรวจสอบและกรอกแบบฟอร์มมอบหมายงาน")
    void adminCanViewAndFillTechnicianAssignmentForm() {

        performAdminLogin();

        // เปิดคำร้องรายการแรก
        openFirstRepairRequest();

        // เปิดหน้า Assign โดยตรง
        openAssignmentPage();

        // -----------------------------------------------------
        // ตรวจสอบหน้า
        // -----------------------------------------------------

        WebElement assignPage = wait.until(
                ExpectedConditions.presenceOfElementLocated(
                        By.cssSelector(".assign-page")
                )
        );

        assertTrue(
                assignPage.isDisplayed(),
                "ต้องแสดงหน้ามอบหมายงาน"
        );

        WebElement heading = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(".assign-head h1")
                )
        );

        assertEquals(
                "มอบหมายงานให้ช่าง",
                heading.getText().trim()
        );

        // -----------------------------------------------------
        // ตรวจสอบส่วนเลือกช่าง
        // -----------------------------------------------------

        WebElement techList = wait.until(
                ExpectedConditions.presenceOfElementLocated(
                        By.cssSelector(".tech-list")
                )
        );

        assertTrue(
                techList.isDisplayed(),
                "ต้องแสดงรายการช่าง"
        );

        List<WebElement> technicianRows = wait.until(
                ExpectedConditions.presenceOfAllElementsLocatedBy(
                        By.cssSelector(".tech-row")
                )
        );

        assertFalse(
                technicianRows.isEmpty(),
                "ต้องมีรายการช่างอย่างน้อย 1 คน"
        );

        // -----------------------------------------------------
        // ตรวจสอบ input technicianId
        // -----------------------------------------------------

        List<WebElement> technicianInputs = wait.until(
                ExpectedConditions.presenceOfAllElementsLocatedBy(
                        By.cssSelector(
                                "input[name='technicianId']"
                        )
                )
        );

        assertFalse(
                technicianInputs.isEmpty(),
                "ต้องมี technicianId"
        );

        WebElement firstRow =
                technicianRows.get(0);

        WebElement firstInput =
                technicianInputs.get(0);

        assertEquals(
                "radio",
                firstInput.getAttribute("type"),
                "technicianId ต้องเป็น radio button"
        );

        String technicianId =
                firstInput.getAttribute("value");

        assertNotNull(
                technicianId,
                "technicianId ต้องไม่เป็น null"
        );

        assertFalse(
                technicianId.isBlank(),
                "technicianId ต้องมีค่า"
        );

        assertTrue(
                firstInput.isEnabled(),
                "radio technicianId ต้องสามารถเลือกได้"
        );

        // -----------------------------------------------------
        // ตรวจสอบชื่อช่าง
        // -----------------------------------------------------

        WebElement technicianName =
                firstRow.findElement(
                        By.cssSelector(".tech-name")
                );

        assertFalse(
                technicianName.getText()
                        .trim()
                        .isEmpty(),
                "ต้องแสดงชื่อช่าง"
        );

        // -----------------------------------------------------
        // ตรวจสอบความเชี่ยวชาญ
        // -----------------------------------------------------

        WebElement technicianSpec =
                firstRow.findElement(
                        By.cssSelector(".tech-spec")
                );

        assertNotNull(
                technicianSpec,
                "ต้องมีข้อมูลความเชี่ยวชาญของช่าง"
        );

        // -----------------------------------------------------
        // เลือกช่าง
        //
        // ใช้ JavaScript click ที่ label .tech-row
        // เนื่องจาก native radio อาจถูกซ่อนด้วย CSS
        // -----------------------------------------------------

        JavascriptExecutor js =
                (JavascriptExecutor) driver;

        js.executeScript(
                "arguments[0].click();",
                firstRow
        );

        wait.until(
                driver -> firstInput.isSelected()
        );

        assertTrue(
                firstInput.isSelected(),
                "ต้องสามารถเลือกช่างได้"
        );

        // -----------------------------------------------------
        // ตรวจสอบช่องหมายเหตุ
        // -----------------------------------------------------

        WebElement adminNote = wait.until(
                ExpectedConditions.presenceOfElementLocated(
                        By.cssSelector(
                                "textarea[name='adminNote']"
                        )
                )
        );

        assertTrue(
                adminNote.isDisplayed(),
                "ต้องแสดงช่องหมายเหตุ"
        );

        assertTrue(
                adminNote.isEnabled(),
                "ช่องหมายเหตุต้องสามารถกรอกได้"
        );

        // -----------------------------------------------------
        // กรอกหมายเหตุ
        // -----------------------------------------------------

        String note =
                "กรุณาดำเนินการตรวจสอบและแก้ไขตามรายละเอียดคำร้อง";

        adminNote.clear();

        adminNote.sendKeys(note);

        assertEquals(
                note,
                adminNote.getDomProperty("value"),
                "ข้อความหมายเหตุต้องตรงกับข้อมูลที่กรอก"
        );

        // -----------------------------------------------------
        // ตรวจสอบปุ่มยืนยัน
        // -----------------------------------------------------

        WebElement confirmButton = wait.until(
                ExpectedConditions.presenceOfElementLocated(
                        By.cssSelector(
                                "button.confirm-btn[type='submit']"
                        )
                )
        );

        assertTrue(
                confirmButton.isDisplayed(),
                "ต้องแสดงปุ่มยืนยันมอบหมายงาน"
        );

        assertTrue(
                confirmButton.isEnabled(),
                "ปุ่มยืนยันต้องสามารถกดได้"
        );

        assertTrue(
                confirmButton.getText()
                        .contains("ยืนยันมอบหมายงาน"),
                "ปุ่มต้องมีข้อความยืนยันมอบหมายงาน"
        );

        // -----------------------------------------------------
        // ตรวจสอบรายละเอียดคำร้อง
        // -----------------------------------------------------

        WebElement assignSide = wait.until(
                ExpectedConditions.presenceOfElementLocated(
                        By.cssSelector(".assign-side")
                )
        );

        assertTrue(
                assignSide.isDisplayed(),
                "ต้องแสดงรายละเอียดคำร้อง"
        );

        String sideText =
                assignSide.getText();

        assertTrue(
                sideText.contains("รายละเอียดคำร้อง"),
                "ต้องแสดงหัวข้อรายละเอียดคำร้อง"
        );

        assertTrue(
                sideText.contains("ประเภทงาน"),
                "ต้องแสดงประเภทงาน"
        );

        assertTrue(
                sideText.contains("รายละเอียด"),
                "ต้องแสดงรายละเอียด"
        );

        assertTrue(
                sideText.contains("ห้อง"),
                "ต้องแสดงห้อง"
        );

        assertTrue(
                sideText.contains("ผู้แจ้ง"),
                "ต้องแสดงผู้แจ้ง"
        );

        assertTrue(
                sideText.contains("สถานะปัจจุบัน"),
                "ต้องแสดงสถานะปัจจุบัน"
        );

        // -----------------------------------------------------
        // ตรวจสอบว่าไม่มี Error Page
        // -----------------------------------------------------

        String pageSource =
                driver.getPageSource();

        assertFalse(
                pageSource.contains("Whitelabel Error Page"),
                "ต้องไม่พบ Whitelabel Error Page"
        );

        assertFalse(
                pageSource.contains("Unexpected error"),
                "ต้องไม่พบ Unexpected error"
        );
    }

    // =========================================================
    // Helper: Login Admin
    // =========================================================

    private void performAdminLogin() {

        driver.get(
                BASE_URL + "/login"
        );

        wait.until(
                ExpectedConditions.urlContains("/login")
        );

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

        email.sendKeys(
                ADMIN_EMAIL
        );

        password.clear();

        password.sendKeys(
                ADMIN_PASSWORD
        );

        WebElement loginButton = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector(
                                "button[type='submit']"
                        )
                )
        );

        loginButton.click();

        // รอให้ออกจาก Login
        wait.until(
                ExpectedConditions.not(
                        ExpectedConditions.urlContains("/login")
                )
        );

        // รอหน้า Admin
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
                ExpectedConditions.urlContains(
                        "/admin/requests"
                )
        );

        List<WebElement> requestLinks = wait.until(
                ExpectedConditions.presenceOfAllElementsLocatedBy(
                        By.cssSelector(
                                "a[href^='/admin/requests/']"
                        )
                )
        );

        assertFalse(
                requestLinks.isEmpty(),
                "ต้องมีคำร้องแจ้งซ่อมอย่างน้อย 1 รายการ"
        );

        WebElement firstRequestLink =
                requestLinks.get(0);

        String href =
                firstRequestLink.getAttribute("href");

        assertNotNull(
                href,
                "ลิงก์คำร้องต้องไม่เป็น null"
        );

        assertTrue(
                href.matches(
                        ".*/admin/requests/[0-9a-fA-F-]{36}"
                ),
                "ลิงก์คำร้องต้องเป็น UUID"
        );

        driver.get(href);

        wait.until(
                ExpectedConditions.urlMatches(
                        ".*/admin/requests/[0-9a-fA-F-]{36}$"
                )
        );

        wait.until(
                ExpectedConditions.presenceOfElementLocated(
                        By.tagName("body")
                )
        );
    }

    // =========================================================
    // Helper: เปิดหน้ามอบหมายงาน
    //
    // Controller จริง:
    //
    // @GetMapping("/{id}/assign")
    //
    // ดังนั้น URL คือ:
    //
    // /admin/requests/{id}/assign
    //
    // ไม่ค้นหา a[href$='/assign']
    // =========================================================

    private void openAssignmentPage() {

        String requestDetailUrl =
                driver.getCurrentUrl();

        assertTrue(
                requestDetailUrl.matches(
                        ".*/admin/requests/[0-9a-fA-F-]{36}$"
                ),
                "ต้องอยู่ที่หน้ารายละเอียดคำร้องก่อน"
        );

        String assignmentUrl =
                requestDetailUrl + "/assign";

        driver.get(
                assignmentUrl
        );

        wait.until(
                ExpectedConditions.urlMatches(
                        ".*/admin/requests/[0-9a-fA-F-]{36}/assign$"
                )
        );

        wait.until(
                ExpectedConditions.presenceOfElementLocated(
                        By.cssSelector(".assign-page")
                )
        );
    }
}
