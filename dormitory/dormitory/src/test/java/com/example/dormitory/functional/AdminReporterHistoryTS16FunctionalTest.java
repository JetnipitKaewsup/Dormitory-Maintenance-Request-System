package com.example.dormitory.functional;

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
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

class AdminReporterHistoryTS16FunctionalTest {

    private static final String BASE_URL = "http://localhost:8080";
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

    private void openReporterList() {
        driver.get(BASE_URL + "/admin/reporters");

        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.tagName("main")
                )
        );
    }

    private WebElement findFirstHistoryLink() {
        openReporterList();

        List<WebElement> historyLinks = wait.until(d -> {
            List<WebElement> links = d.findElements(
                    By.cssSelector(
                            "a[href*='/admin/reporters/'][href$='/history']"
                    )
            );

            return links.isEmpty() ? null : links;
        });

        assertFalse(
                historyLinks.isEmpty(),
                "ต้องมีผู้แจ้งซ่อมอย่างน้อยหนึ่งรายที่สามารถเปิดประวัติได้"
        );

        return historyLinks.get(0);
    }

    private void openFirstReporterHistory() {
        WebElement historyLink = findFirstHistoryLink();

        String historyUrl = historyLink.getAttribute("href");
        assertNotNull(historyUrl);
        assertTrue(historyUrl.matches(
                ".*/admin/reporters/[0-9a-fA-F-]+/history$"
        ));

        historyLink.click();

        wait.until(
                ExpectedConditions.urlMatches(
                        ".*/admin/reporters/[0-9a-fA-F-]+/history$"
                )
        );

        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(".page-head")
                )
        );
    }

    // TC-FT-16-01: เปิดหน้ารายชื่อผู้แจ้งซ่อม
    @Test
    void TC_FT_16_01_adminCanOpenReporterList() {
        openReporterList();

        assertTrue(
                driver.getCurrentUrl().endsWith("/admin/reporters"),
                "ต้องอยู่ที่หน้ารายชื่อผู้แจ้งซ่อม"
        );

        assertTrue(
                driver.findElement(By.tagName("main")).isDisplayed(),
                "หน้ารายชื่อผู้แจ้งซ่อมต้องแสดงผล"
        );
    }

    // TC-FT-16-02: เปิดหน้าประวัติของผู้แจ้งซ่อม
    @Test
    void TC_FT_16_02_adminCanOpenReporterHistory() {
        openFirstReporterHistory();

        assertTrue(
                driver.getCurrentUrl().matches(
                        ".*/admin/reporters/[0-9a-fA-F-]+/history$"
                ),
                "URL ต้องเป็นหน้าประวัติของผู้แจ้งซ่อม"
        );

        WebElement heading = driver.findElement(
                By.cssSelector(".page-title")
        );

        assertTrue(
                heading.isDisplayed(),
                "ต้องแสดงหัวข้อประวัติการแจ้งซ่อม"
        );

        assertTrue(
                heading.getText().contains("ประวัติการแจ้งซ่อม"),
                "หัวข้อต้องระบุว่าเป็นหน้าประวัติการแจ้งซ่อม"
        );
    }

    // TC-FT-16-03: ตรวจสอบรายละเอียดประวัติการแจ้งซ่อม
    @Test
    void TC_FT_16_03_historyDetailsAreDisplayedWhenAvailable() {
        openFirstReporterHistory();

        List<WebElement> historyCards = driver.findElements(
                By.cssSelector(".tech-list .tech-card")
        );

        List<WebElement> emptyStates = driver.findElements(
                By.cssSelector(".empty-state")
        );

        if (!historyCards.isEmpty()) {
            WebElement card = historyCards.get(0);

            assertTrue(card.isDisplayed());

            assertTrue(
                    card.findElement(
                            By.cssSelector(".tech-spec-badge")
                    ).isDisplayed(),
                    "ต้องแสดงประเภทงาน"
            );

            assertTrue(
                    card.findElement(
                            By.cssSelector(".badge")
                    ).isDisplayed(),
                    "ต้องแสดงสถานะคำร้อง"
            );

            assertTrue(
                    card.findElement(By.tagName("p")).isDisplayed(),
                    "ต้องแสดงรายละเอียดคำร้อง"
            );
        } else {
            assertFalse(
                    emptyStates.isEmpty(),
                    "เมื่อไม่มีประวัติ ต้องแสดงข้อความสถานะว่าง"
            );

            assertTrue(emptyStates.get(0).isDisplayed());
        }
    }

    // TC-FT-16-04: ตรวจสอบวันเวลาเริ่มซ่อมและซ่อมเสร็จ
    @Test
    void TC_FT_16_04_repairDateFieldsFollowAvailableData() {
        openFirstReporterHistory();

        List<WebElement> historyCards = driver.findElements(
                By.cssSelector(".tech-list .tech-card")
        );

        if (historyCards.isEmpty()) {
            assertTrue(
                    driver.findElement(
                            By.cssSelector(".empty-state")
                    ).isDisplayed(),
                    "เมื่อไม่มีประวัติ ระบบต้องแสดงข้อความสถานะว่าง"
            );
            return;
        }

        WebElement card = historyCards.get(0);

        List<WebElement> createdAt = card.findElements(
                By.xpath(
                        ".//*[contains(text(),'แจ้งเมื่อ:')]"
                )
        );

        assertFalse(
                createdAt.isEmpty(),
                "ประวัติแต่ละรายการต้องแสดงวันที่แจ้ง"
        );

        List<WebElement> startDate = card.findElements(
                By.xpath(
                        ".//*[contains(text(),'เริ่มซ่อม:')]"
                )
        );

        List<WebElement> endDate = card.findElements(
                By.xpath(
                        ".//*[contains(text(),'ซ่อมเสร็จ:')]"
                )
        );

        // วันเวลาเริ่มซ่อมและซ่อมเสร็จจะแสดงเมื่อข้อมูลมีค่าเท่านั้น
        assertTrue(
                startDate.size() <= 1,
                "วันเวลาเริ่มซ่อมไม่ควรซ้ำในรายการเดียว"
        );

        assertTrue(
                endDate.size() <= 1,
                "วันเวลาซ่อมเสร็จไม่ควรซ้ำในรายการเดียว"
        );
    }

    // TC-FT-16-05: กลับไปยังหน้ารายชื่อผู้แจ้งซ่อม
    @Test
    void TC_FT_16_05_adminCanReturnToReporterList() {
        openFirstReporterHistory();

        WebElement backLink = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector(
                                ".page-head a[href='/admin/reporters']"
                        )
                )
        );

        assertTrue(
                backLink.getText().contains("กลับหน้ารายชื่อ"),
                "ต้องมีลิงก์กลับไปหน้ารายชื่อผู้แจ้งซ่อม"
        );

        backLink.click();

        wait.until(
                ExpectedConditions.urlToBe(
                        BASE_URL + "/admin/reporters"
                )
        );

        assertTrue(
                driver.findElement(By.tagName("main")).isDisplayed(),
                "ต้องกลับมาหน้ารายชื่อผู้แจ้งซ่อมได้"
        );
    }
}
