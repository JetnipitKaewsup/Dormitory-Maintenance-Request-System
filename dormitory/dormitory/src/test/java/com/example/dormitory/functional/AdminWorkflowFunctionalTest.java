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
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

class AdminWorkflowFunctionalTest {

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
     * เข้าสู่ระบบด้วยบัญชีแอดมินก่อนเริ่มทดสอบทุกกรณี
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
                        By.cssSelector("input[name='password'], input[type='password']")
                )
        );

        emailField.clear();
        emailField.sendKeys(ADMIN_EMAIL);

        passwordField.clear();
        passwordField.sendKeys(ADMIN_PASSWORD);

        // คลิกปุ่ม Submit จริง แทนการเรียก form.submit()
        WebElement submitButton = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector("button[type='submit'], input[type='submit']")
                )
        );

        submitButton.click();

        wait.until(driver ->
                driver.getCurrentUrl().contains("/admin")
                        && !driver.getCurrentUrl().contains("/login")
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
     * อ่านจำนวนรายการจากข้อความ เช่น "3 งาน"
     */
    private int extractCount(String text) {
        Matcher matcher = Pattern.compile("\\d+").matcher(text);

        assertTrue(
                matcher.find(),
                "ไม่พบตัวเลขจำนวนรายการในข้อความ: " + text
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
     * TC-FT-14-01
     * แอดมินสามารถเปิดหน้าตรวจสอบสถานะงานได้
     */
    @Test
    void TC_FT_14_01_adminCanOpenInspectionPage() {
        openInspectionPage();

        assertTrue(
                driver.getCurrentUrl().contains("/admin/inspections"),
                "URL ต้องเป็นหน้าตรวจสอบสถานะงาน"
        );

        assertTrue(
                driver.findElement(By.cssSelector(".work-page")).isDisplayed(),
                "หน้าตรวจสอบงานต้องแสดงผล"
        );
    }

    /**
     * TC-FT-14-02
     * ตรวจสอบองค์ประกอบหลักของหน้ารายการตรวจสอบงาน
     */
    @Test
    void TC_FT_14_02_inspectionPageDisplaysMainComponents() {
        openInspectionPage();

        assertTrue(
                driver.findElement(By.cssSelector(".work-head h1")).isDisplayed(),
                "ต้องแสดงหัวข้อหน้าตรวจสอบงาน"
        );

        assertTrue(
                driver.findElement(By.cssSelector(".work-list")).isDisplayed(),
                "ต้องแสดงส่วนรายการงาน"
        );

        assertTrue(
                driver.findElement(By.cssSelector(".tabs")).isDisplayed(),
                "ต้องแสดงเมนูนำทางของแอดมิน"
        );
    }

    /**
     * TC-FT-14-03
     * จำนวนรายการที่แสดงต้องตรงกับจำนวนแถวงานบนหน้าจอ
     */
    @Test
    void TC_FT_14_03_displayedCountMatchesWorkRows() {
        openInspectionPage();

        WebElement countElement = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(".work-count")
                )
        );

        int displayedCount = extractCount(countElement.getText());

        List<WebElement> workRows = driver.findElements(
                By.cssSelector(".work-list .work-row")
        );

        assertEquals(
                displayedCount,
                workRows.size(),
                "จำนวนงานที่แสดงต้องตรงกับจำนวนแถวในรายการ"
        );
    }

    /**
     * TC-FT-14-04
     * ตรวจสอบการแสดงสถานะงานที่พร้อมให้แอดมินตรวจสอบ
     */
    @Test
    void TC_FT_14_04_readyJobsAreDisplayedConsistently() {
        openInspectionPage();

        List<WebElement> readyRows = driver.findElements(
                By.cssSelector(".work-list .work-row.is-ready")
        );

        List<WebElement> readyCountElements = driver.findElements(
                By.cssSelector(".work-ready-count")
        );

        if (readyRows.isEmpty()) {
            // หากไม่มีงานพร้อมตรวจสอบ ต้องไม่มีแถวที่ถูกทำเครื่องหมาย is-ready
            assertEquals(
                    0,
                    readyRows.size(),
                    "ต้องไม่มีแถวงานพร้อมตรวจสอบเมื่อไม่พบงานสถานะดังกล่าว"
            );

            // Template ใช้ th:if แสดงตัวนับเมื่อมีงานพร้อมตรวจสอบ
            assertTrue(
                    readyCountElements.isEmpty(),
                    "ไม่ควรแสดงตัวนับงานพร้อมตรวจสอบเมื่อไม่มีงานพร้อมตรวจสอบ"
            );
        } else {
            assertEquals(
                    1,
                    readyCountElements.size(),
                    "ต้องมีตัวนับงานพร้อมตรวจสอบหนึ่งตำแหน่ง"
            );

            int displayedReadyCount =
                    extractCount(readyCountElements.get(0).getText());

            assertEquals(
                    readyRows.size(),
                    displayedReadyCount,
                    "จำนวนงานพร้อมตรวจสอบต้องตรงกับจำนวนแถว is-ready"
            );
        }
    }

    /**
     * TC-FT-14-05
     * ตรวจสอบลิงก์ไปยังหน้ารายละเอียดการตรวจสอบของแต่ละงาน
     */
    @Test
    void TC_FT_14_05_workRowsHaveValidInspectionLinks() {
        openInspectionPage();

        List<WebElement> workRows = driver.findElements(
                By.cssSelector(".work-list .work-row")
        );

        assertFalse(
                workRows.isEmpty(),
                "ต้องมีรายการงานสำหรับตรวจสอบลิงก์"
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
                    "ลิงก์ต้องชี้ไปยังหน้าตรวจสอบคำร้อง: " + href
            );
        }
    }
}
