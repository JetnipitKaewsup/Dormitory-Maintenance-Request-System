package com.example.dormitory.uat;

import java.time.Duration;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
 * TS-16: ดูประวัติการซ่อม
 *
 * User Acceptance Test (UAT)
 *
 * ทดสอบการตรวจสอบประวัติการดำเนินงาน
 * และคำร้องแจ้งซ่อมที่ผ่านมาของผู้แจ้งซ่อม
 *
 * TC-UAT-16-01 เปิดหน้าจัดการผู้แจ้งซ่อม
 * TC-UAT-16-02 เปิดหน้าประวัติผู้แจ้งซ่อม
 * TC-UAT-16-03 ตรวจสอบข้อมูลผู้แจ้งซ่อม
 * TC-UAT-16-04 ตรวจสอบประวัติคำร้องและรายละเอียด
 * TC-UAT-16-05 กลับไปหน้าจัดการผู้แจ้งซ่อม
 */
class AdminReporterHistoryTS16UATTest {

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

        driver.manage().window().maximize();

        wait = new WebDriverWait(
                driver,
                Duration.ofSeconds(30)
        );

        loginAsAdmin();
    }

    /**
     * เข้าสู่ระบบด้วยบัญชี Admin ก่อนเริ่ม UAT
     */
    private void loginAsAdmin() {
        driver.get(BASE_URL + "/login");

        WebElement emailField = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(
                                "input[name='email'], input[type='email']"
                        )
                )
        );

        WebElement passwordField = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(
                                "input[name='password'], input[type='password']"
                        )
                )
        );

        emailField.clear();
        emailField.sendKeys(ADMIN_EMAIL);

        passwordField.clear();
        passwordField.sendKeys(ADMIN_PASSWORD);

        WebElement submitButton = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector(
                                "button[type='submit'], input[type='submit']"
                        )
                )
        );

        submitButton.click();

        try {
            wait.until(currentDriver ->
                    !currentDriver.getCurrentUrl().contains("/login")
            );
        } catch (org.openqa.selenium.TimeoutException e) {
            throw new AssertionError(
                    "Admin ไม่สามารถเข้าสู่ระบบได้ภายในเวลาที่กำหนด"
                            + "\nCurrent URL: "
                            + driver.getCurrentUrl()
                            + "\nPage text: "
                            + driver.findElement(
                                    By.tagName("body")
                            ).getText(),
                    e
            );
        }

        assertTrue(
                driver.getCurrentUrl().contains("/admin"),
                "หลังเข้าสู่ระบบต้องเข้าถึงหน้าส่วน Admin ได้"
                        + "\nCurrent URL: "
                        + driver.getCurrentUrl()
        );
    }

    /**
     * เปิดหน้าจัดการผู้แจ้งซ่อม
     */
    private void openReporterManagementPage() {
        driver.get(BASE_URL + "/admin/reporters");

        wait.until(
                ExpectedConditions.urlContains(
                        "/admin/reporters"
                )
        );

        wait.until(
                ExpectedConditions.presenceOfElementLocated(
                        By.cssSelector(
                                "a[href*='/admin/reporters/']"
                                        + "[href$='/history']"
                        )
                )
        );
    }

    /**
     * เปิดหน้าประวัติของผู้แจ้งซ่อมคนแรก
     * ที่มีลิงก์ดูประวัติ
     */
    private void openFirstReporterHistory() {
        openReporterManagementPage();

        WebElement historyLink = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector(
                                "a[href*='/admin/reporters/']"
                                        + "[href$='/history']"
                        )
                )
        );

        String expectedUrl =
                historyLink.getAttribute("href");

        assertNotNull(
                expectedUrl,
                "ลิงก์ดูประวัติต้องมี URL"
        );

        historyLink.click();

        wait.until(
                ExpectedConditions.urlContains("/history")
        );

        assertTrue(
                driver.getCurrentUrl().matches(
                        ".*/admin/reporters/"
                                + "[0-9a-fA-F-]+/history$"
                ),
                "ต้องเปิดประวัติของผู้แจ้งซ่อมผ่าน URL ที่ถูกต้อง"
                        + "\nCurrent URL: "
                        + driver.getCurrentUrl()
        );
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    /**
     * TC-UAT-16-01
     * Admin สามารถเปิดหน้าจัดการผู้แจ้งซ่อมได้
     */
    @Test
    void TC_UAT_16_01_adminCanOpenReporterManagementPage() {
        openReporterManagementPage();

        assertTrue(
                driver.getCurrentUrl().contains(
                        "/admin/reporters"
                ),
                "ต้องอยู่บนหน้าจัดการผู้แจ้งซ่อม"
        );

        List<WebElement> historyLinks =
                driver.findElements(
                        By.cssSelector(
                                "a[href*='/admin/reporters/']"
                                        + "[href$='/history']"
                        )
                );

        assertFalse(
                historyLinks.isEmpty(),
                "ต้องมีลิงก์สำหรับเปิดดูประวัติผู้แจ้งซ่อม"
        );
    }

    /**
     * TC-UAT-16-02
     * Admin สามารถเปิดหน้าประวัติของผู้แจ้งซ่อมได้
     */
    @Test
    void TC_UAT_16_02_adminCanOpenReporterHistory() {
        openFirstReporterHistory();

        assertTrue(
                driver.getCurrentUrl().matches(
                        ".*/admin/reporters/"
                                + "[0-9a-fA-F-]+/history$"
                ),
                "URL ต้องตรงกับหน้าประวัติของผู้แจ้งซ่อมที่เลือก"
        );

        WebElement body = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.tagName("body")
                )
        );

        assertFalse(
                body.getText().trim().isEmpty(),
                "หน้าประวัติต้องแสดงเนื้อหา"
        );
    }

    /**
     * TC-UAT-16-03
     * Admin สามารถตรวจสอบข้อมูลผู้แจ้งซ่อมบนหน้าประวัติได้
     */
    @Test
    void TC_UAT_16_03_adminCanViewReporterInformation() {
        openFirstReporterHistory();

        WebElement body = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.tagName("body")
                )
        );

        String pageText = body.getText();

        assertTrue(
                pageText.contains("ประวัติ")
                        || pageText.contains("ผู้แจ้ง")
                        || pageText.contains("Reporter"),
                "หน้าประวัติต้องแสดงหัวข้อหรือข้อมูลที่เกี่ยวข้อง"
                        + "กับผู้แจ้งซ่อม"
                        + "\nข้อความบนหน้าเว็บ:\n"
                        + pageText
        );

        assertTrue(
                body.isDisplayed(),
                "หน้าแสดงข้อมูลผู้แจ้งซ่อมต้องปรากฏบนหน้าจอ"
        );
    }

    /**
     * TC-UAT-16-04
     * Admin สามารถตรวจสอบประวัติคำร้องและรายละเอียดได้
     *
     * รองรับทั้งกรณีมีประวัติคำร้อง
     * และกรณีไม่มีประวัติคำร้อง
     */
    @Test
    void TC_UAT_16_04_adminCanReviewRepairHistory() {
        openFirstReporterHistory();

        WebElement body = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.tagName("body")
                )
        );

        String pageText = body.getText();

        // ตรวจสอบว่าหน้าแสดงหัวข้อหรือข้อมูลประวัติ
        assertTrue(
                pageText.contains("ประวัติ")
                        || pageText.contains("ผู้แจ้ง")
                        || pageText.contains("Reporter"),
                "ต้องแสดงหัวข้อหน้าประวัติผู้แจ้งซ่อม"
                        + "\nข้อความบนหน้าเว็บ:\n"
                        + pageText
        );

        // ตรวจสอบรายการในกรณีที่หน้าเว็บใช้ตาราง
        boolean hasHistoryRows = !driver.findElements(
                By.cssSelector("table tbody tr")
        ).isEmpty();

        // ตรวจสอบรายการในกรณีที่หน้าเว็บใช้ element อื่น
        // เช่น div หรือ article ในการแสดงคำร้อง
        boolean hasRepairDetails =
                pageText.contains("สถานะ")
                        || pageText.contains("ประเภทงาน")
                        || pageText.contains("รายละเอียด")
                        || pageText.contains("วันที่")
                        || pageText.contains("PENDING")
                        || pageText.contains("APPROVED")
                        || pageText.contains("IN_PROGRESS")
                        || pageText.contains("COMPLETED")
                        || pageText.contains("REJECTED")
                        || pageText.contains("CANCELLED");

        // ตรวจสอบข้อความกรณีไม่มีประวัติ
        boolean hasEmptyHistoryMessage =
                pageText.contains("ไม่เคย")
                        || pageText.contains("ไม่มีประวัติ")
                        || pageText.contains("ยังไม่มี");

        assertTrue(
                hasHistoryRows
                        || hasRepairDetails
                        || hasEmptyHistoryMessage,
                "ต้องแสดงรายการประวัติ รายละเอียดคำร้อง "
                        + "หรือข้อความเมื่อไม่มีประวัติ"
                        + "\nข้อความบนหน้าเว็บ:\n"
                        + pageText
        );
    }

    /**
     * TC-UAT-16-05
     * Admin สามารถกลับไปยังหน้าจัดการผู้แจ้งซ่อมได้
     */
    @Test
    void TC_UAT_16_05_adminCanReturnToReporterManagementPage() {
        openFirstReporterHistory();

        WebElement backLink = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector(
                                "a[href='/admin/reporters'], "
                                        + "a[href$='/admin/reporters']"
                        )
                )
        );

        backLink.click();

        wait.until(
                ExpectedConditions.urlToBe(
                        BASE_URL + "/admin/reporters"
                )
        );

        assertTrue(
                driver.getCurrentUrl().endsWith(
                        "/admin/reporters"
                ),
                "ต้องกลับมายังหน้าจัดการผู้แจ้งซ่อมได้"
        );
    }
}
