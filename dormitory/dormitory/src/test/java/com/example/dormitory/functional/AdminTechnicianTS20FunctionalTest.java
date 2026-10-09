
package com.example.dormitory.functional;

import java.time.Duration;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

class AdminTechnicianTS20FunctionalTest {

    private static final String BASE_URL = "http://localhost:8080";

    // ใช้บัญชี Admin ชุดเดียวกับ Functional Test ของ TS-17
    private static final String ADMIN_EMAIL = "lucaenen03@gmail.com";
    private static final String ADMIN_PASSWORD = "adminnaib";

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    void setUp() {
        ChromeOptions options = new ChromeOptions();

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(60));

        driver.manage().window().maximize();

        loginAsAdmin();
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    // เข้าสู่ระบบด้วยบัญชี Admin
    private void loginAsAdmin() {
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

        wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector("button[type='submit']")
                )
        ).click();

        try {
            wait.until(d ->
                    d.getCurrentUrl().startsWith(BASE_URL + "/admin")
            );
        } catch (org.openqa.selenium.TimeoutException e) {
            throw new AssertionError(
                    "Admin login ไม่สำเร็จ"
                            + "\nCurrent URL: " + driver.getCurrentUrl()
                            + "\nPage text: "
                            + driver.findElement(By.tagName("body")).getText(),
                    e
            );
        }
    }

    // เปิดรายการช่างและเลือกหน้าประวัติจากลิงก์ที่มีอยู่จริง
    private void openFirstTechnicianHistoryPage() {
        driver.get(BASE_URL + "/admin/technicians");

        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.tagName("main")
                )
        );

        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(".page-title")
                )
        );

        List<WebElement> historyLinks = wait.until(d -> {
            List<WebElement> links = d.findElements(
                    By.cssSelector("a[href*='/history']")
            );

            List<WebElement> visibleLinks = links.stream()
                    .filter(WebElement::isDisplayed)
                    .filter(link -> {
                        String href = link.getAttribute("href");
                        return href != null
                                && href.matches(
                                    ".*/admin/technicians/[0-9a-fA-F-]{36}/history"
                                );
                    })
                    .toList();

            return visibleLinks.isEmpty() ? null : visibleLinks;
        });

        String historyUrl = historyLinks.get(0).getAttribute("href");

        assertTrue(
                historyUrl != null && !historyUrl.isBlank(),
                "ต้องพบ URL สำหรับเปิดประวัติของช่าง"
        );

        driver.get(historyUrl);

        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("main .page-title")
                )
        );
    }

    // TC-FT-20-01: ตรวจสอบการเปิดหน้าประวัติการซ่อมของช่าง
    @Test
    void TC_FT_20_01_adminCanOpenTechnicianRepairHistory() {
        openFirstTechnicianHistoryPage();

        assertTrue(
                driver.getCurrentUrl().matches(
                        ".*/admin/technicians/[0-9a-fA-F-]{36}/history"
                ),
                "ต้องอยู่บนหน้า URL ประวัติของช่าง"
        );

        assertTrue(
                driver.findElement(By.tagName("main")).isDisplayed(),
                "เนื้อหาหลักของหน้าประวัติต้องแสดง"
        );

        assertTrue(
                driver.findElement(By.cssSelector(".page-title")).isDisplayed(),
                "ต้องแสดงหัวข้อประวัติการซ่อม"
        );
    }

    // TC-FT-20-02: ตรวจสอบข้อมูลช่างบนหน้าประวัติ
    @Test
    void TC_FT_20_02_technicianInformationIsDisplayed() {
        openFirstTechnicianHistoryPage();

        WebElement title = driver.findElement(
                By.cssSelector("main .page-title")
        );

        WebElement subtitle = driver.findElement(
                By.cssSelector("main .page-sub")
        );

        assertTrue(
                title.isDisplayed(),
                "ต้องแสดงชื่อช่างในหัวข้อหน้าประวัติ"
        );

        assertFalse(
                title.getText().isBlank(),
                "หัวข้อประวัติต้องมีข้อมูล"
        );

        assertTrue(
                subtitle.isDisplayed(),
                "ต้องแสดงข้อมูลบัญชีและความเชี่ยวชาญของช่าง"
        );

        assertFalse(
                subtitle.getText().isBlank(),
                "ข้อมูลบัญชีและความเชี่ยวชาญต้องไม่ว่าง"
        );
    }

    // TC-FT-20-03: ตรวจสอบประวัติหรือข้อความกรณีไม่มีประวัติ
    @Test
    void TC_FT_20_03_repairHistoryOrEmptyStateIsDisplayed() {
        openFirstTechnicianHistoryPage();

        List<WebElement> historyCards = driver.findElements(
                By.cssSelector("main .tech-list .tech-card")
        );

        List<WebElement> emptyStates = driver.findElements(
                By.cssSelector("main .empty-state")
        );

        if (!historyCards.isEmpty()) {
            assertTrue(
                    historyCards.stream().anyMatch(WebElement::isDisplayed),
                    "ต้องแสดงรายการประวัติการซ่อม"
            );
        } else {
            assertFalse(
                    emptyStates.isEmpty(),
                    "เมื่อไม่มีประวัติ ต้องมีข้อความแสดงสถานะรายการว่าง"
            );

            assertTrue(
                    emptyStates.get(0).isDisplayed(),
                    "ข้อความรายการว่างต้องมองเห็นได้"
            );

            assertFalse(
                    emptyStates.get(0).getText().isBlank(),
                    "ข้อความรายการว่างต้องมีเนื้อหา"
            );
        }
    }

    // TC-FT-20-04: ตรวจสอบรายละเอียดของรายการซ่อม
    @Test
    void TC_FT_20_04_repairHistoryDetailsAreDisplayedWhenAvailable() {
        openFirstTechnicianHistoryPage();

        List<WebElement> historyCards = driver.findElements(
                By.cssSelector("main .tech-list .tech-card")
        );

        if (historyCards.isEmpty()) {
            WebElement emptyState = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            By.cssSelector("main .empty-state")
                    )
            );

            assertTrue(
                    emptyState.isDisplayed(),
                    "เมื่อไม่มีประวัติ ต้องแสดงข้อความรายการว่าง"
            );

            return;
        }

        for (WebElement card : historyCards) {
            assertTrue(
                    card.isDisplayed(),
                    "รายการประวัติการซ่อมต้องมองเห็นได้"
            );

            WebElement repairType = card.findElement(
                    By.cssSelector(".tech-spec-badge")
            );

            WebElement status = card.findElement(
                    By.cssSelector(".badge")
            );

            List<WebElement> descriptions = card.findElements(
                    By.cssSelector("p")
            );

            WebElement assignDate = card.findElement(
                    By.cssSelector(".tech-meta")
            );

            assertFalse(
                    repairType.getText().isBlank(),
                    "ต้องแสดงประเภทงานซ่อม"
            );

            assertFalse(
                    status.getText().isBlank(),
                    "ต้องแสดงสถานะงานซ่อม"
            );

            assertFalse(
                    descriptions.isEmpty(),
                    "ต้องมีรายละเอียดงานซ่อม"
            );

            assertFalse(
                    descriptions.get(0).getText().isBlank(),
                    "รายละเอียดงานซ่อมต้องไม่ว่าง"
            );

            assertFalse(
                    assignDate.getText().isBlank(),
                    "ต้องแสดงวันที่มอบหมายงาน"
            );
        }
    }

    // TC-FT-20-05: ตรวจสอบการกลับไปยังหน้ารายการช่าง
    @Test
    void TC_FT_20_05_adminCanReturnToTechnicianList() {
        openFirstTechnicianHistoryPage();

        WebElement backLink = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector(
                                "main .page-head a[href='/admin/technicians']"
                        )
                )
        );

        backLink.click();

        wait.until(
                ExpectedConditions.urlToBe(
                        BASE_URL + "/admin/technicians"
                )
        );

        assertTrue(
                driver.getCurrentUrl().endsWith("/admin/technicians"),
                "ต้องกลับมายังหน้ารายการช่าง"
        );

        assertTrue(
                driver.findElement(By.cssSelector(".page-title")).isDisplayed(),
                "ต้องแสดงหัวข้อหน้ารายการช่าง"
        );
    }
}
