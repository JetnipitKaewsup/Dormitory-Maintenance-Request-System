package com.example.dormitory.uat;

import java.time.Duration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
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

class AdminWorkflowUATTest {

    private static final String BASE_URL = "http://localhost:8080";
    private static final String ADMIN_EMAIL = "lucaenen03@gmail.com";
    private static final String ADMIN_PASSWORD = "adminnaib";

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        loginAsAdmin();
    }


    /**
     * เข้าสู่ระบบด้วยบัญชี Admin ก่อนเริ่มทดสอบ
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

        // คลิกปุ่มเข้าสู่ระบบจริง
        submitButton.click();

        try {
            // รอจนออกจากหน้า Login
            wait.until(currentDriver ->
                    !currentDriver.getCurrentUrl()
                            .contains("/login")
            );
        } catch (org.openqa.selenium.TimeoutException e) {
            throw new AssertionError(
                    "ไม่สามารถเข้าสู่ระบบได้ภายในเวลาที่กำหนด"
                            + "\nCurrent URL: "
                            + driver.getCurrentUrl()
                            + "\nPage text: "
                            + driver.findElement(By.tagName("body")).getText(),
                    e
            );
        }

        // ตรวจสอบว่าเข้าสู่ส่วน Admin จริง
        assertTrue(
                driver.getCurrentUrl().contains("/admin"),
                "เข้าสู่ระบบแล้ว แต่ไม่ได้เข้าสู่หน้า Admin"
                        + "\nCurrent URL: "
                        + driver.getCurrentUrl()
        );
    }

    /**
     * เปิดหน้าตรวจสอบสถานะการดำเนินงานของช่าง
     */
    private void openInspectionPage() {
        driver.get(BASE_URL + "/admin/inspections");

        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(".work-page")
                )
        );
    }

    /**
     * ดึงตัวเลขจากข้อความแสดงจำนวนรายการ
     */
    private int extractCount(String text) {
        Matcher matcher = Pattern.compile("\\d+").matcher(text);

        assertTrue(
                matcher.find(),
                "ไม่พบจำนวนรายการในข้อความ: " + text
        );

        return Integer.parseInt(matcher.group());
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    /**
     * TC-UAT-14-01
     * แอดมินเปิดหน้าตรวจสอบสถานะงานได้
     */
    @Test
    void TC_UAT_14_01_adminCanOpenInspectionPage() {
        openInspectionPage();

        assertTrue(
                driver.getCurrentUrl().contains("/admin/inspections"),
                "ต้องอยู่บนหน้าตรวจสอบสถานะงาน"
        );

        assertTrue(
                driver.findElement(
                        By.cssSelector(".work-page")
                ).isDisplayed(),
                "หน้าตรวจสอบสถานะงานต้องแสดงผล"
        );
    }

    /**
     * TC-UAT-14-02
     * แอดมินมองเห็นองค์ประกอบหลักของหน้าตรวจสอบงาน
     */
    @Test
    void TC_UAT_14_02_adminCanViewInspectionPageComponents() {
        openInspectionPage();

        assertTrue(
                driver.findElement(
                        By.cssSelector(".work-head h1")
                ).isDisplayed(),
                "ต้องแสดงหัวข้อหน้าตรวจสอบงาน"
        );

        assertTrue(
                driver.findElement(
                        By.cssSelector(".work-list")
                ).isDisplayed(),
                "ต้องแสดงรายการงาน"
        );

        assertTrue(
                driver.findElement(
                        By.cssSelector(".tabs")
                ).isDisplayed(),
                "ต้องแสดงเมนูนำทางของแอดมิน"
        );
    }

    /**
     * TC-UAT-14-03
     * แอดมินตรวจสอบจำนวนงานที่แสดงได้
     */
    @Test
    void TC_UAT_14_03_adminCanVerifyDisplayedWorkCount() {
        openInspectionPage();

        WebElement countElement = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(".work-count")
                )
        );

        int displayedCount = extractCount(
                countElement.getText()
        );

        List<WebElement> workRows = driver.findElements(
                By.cssSelector(".work-list .work-row")
        );

        assertEquals(
                displayedCount,
                workRows.size(),
                "จำนวนงานต้องตรงกับจำนวนแถวที่แสดง"
        );
    }

    /**
     * TC-UAT-14-04
     * แอดมินตรวจสอบงานที่พร้อมให้ตรวจรับได้
     */
    @Test
    void TC_UAT_14_04_adminCanIdentifyReadyJobs() {
        openInspectionPage();

        List<WebElement> readyRows = driver.findElements(
                By.cssSelector(".work-list .work-row.is-ready")
        );

        List<WebElement> readyCountElements = driver.findElements(
                By.cssSelector(".work-ready-count")
        );

        if (readyRows.isEmpty()) {
            assertTrue(
                    readyCountElements.isEmpty(),
                    "เมื่อไม่มีงานพร้อมตรวจสอบ ต้องไม่มีตัวนับงานพร้อมตรวจสอบ"
            );
        } else {
            assertEquals(
                    1,
                    readyCountElements.size(),
                    "ต้องมีตัวนับงานพร้อมตรวจสอบหนึ่งตำแหน่ง"
            );

            int displayedReadyCount = extractCount(
                    readyCountElements.get(0).getText()
            );

            assertEquals(
                    readyRows.size(),
                    displayedReadyCount,
                    "จำนวนงานพร้อมตรวจสอบต้องตรงกับรายการที่แสดง"
            );
        }
    }

    /**
     * TC-UAT-14-05
     * แอดมินสามารถเปิดลิงก์ตรวจสอบรายละเอียดของงานได้
     */
    @Test
    void TC_UAT_14_05_adminCanAccessInspectionLinks() {
        openInspectionPage();

        List<WebElement> workRows = driver.findElements(
                By.cssSelector(".work-list .work-row")
        );

        assertFalse(
                workRows.isEmpty(),
                "ต้องมีรายการงานในระบบเพื่อทดสอบลิงก์"
        );

        for (WebElement row : workRows) {
            WebElement inspectLink = row.findElement(
                    By.cssSelector("a.work-btn")
            );

            assertTrue(
                    inspectLink.isDisplayed(),
                    "ลิงก์ตรวจสอบงานต้องแสดงผล"
            );

            String href = inspectLink.getAttribute("href");

            assertNotNull(href);

            assertTrue(
                    href.matches(
                            ".*/admin/requests/[0-9a-fA-F-]+/inspect$"
                    ),
                    "URL ต้องชี้ไปหน้าตรวจสอบคำร้อง: " + href
            );
        }
    }
}
