package com.example.dormitory.functional;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

class AdminTechnicianTS17FunctionalTest {

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

    // เปิดหน้ารายการช่าง
    private void openTechnicianList() {
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
    }

    // TC-FT-17-01: ตรวจสอบการเปิดหน้ารายการช่าง
    @Test
    void TC_FT_17_01_adminCanOpenTechnicianList() {
        openTechnicianList();

        assertTrue(
                driver.getCurrentUrl().endsWith("/admin/technicians"),
                "ต้องอยู่ที่หน้ารายการช่าง"
        );

        assertTrue(
                driver.findElement(By.tagName("main")).isDisplayed(),
                "ส่วนเนื้อหาหลักต้องแสดงผล"
        );

        assertTrue(
                driver.findElement(By.cssSelector(".page-title")).isDisplayed(),
                "ต้องแสดงหัวข้อหน้ารายการช่าง"
        );
    }

    // TC-FT-17-02: ตรวจสอบการแสดงข้อมูลช่าง
    @Test
    void TC_FT_17_02_technicianCardsDisplayAvailableData() {
        openTechnicianList();

        List<WebElement> cards = driver.findElements(
                By.cssSelector("#techList .tech-card")
        );

        List<WebElement> emptyStates = driver.findElements(
                By.cssSelector("#emptyState")
        );

        // กรณีไม่มีข้อมูลช่าง
        if (cards.isEmpty()) {
            assertFalse(
                    emptyStates.isEmpty(),
                    "เมื่อไม่มีข้อมูลช่าง ต้องมีส่วนแสดงสถานะรายการว่าง"
            );

            assertTrue(
                    emptyStates.get(0).isDisplayed(),
                    "ส่วนแสดงสถานะรายการว่างต้องมองเห็นได้"
            );

            return;
        }

        // ตรวจสอบข้อมูลที่แสดงในการ์ดช่างแต่ละรายการ
        for (WebElement card : cards) {
            assertTrue(
                    card.isDisplayed(),
                    "การ์ดช่างต้องมองเห็นได้"
            );

            assertTrue(
                    card.findElement(By.cssSelector(".tech-name")).isDisplayed(),
                    "การ์ดช่างต้องแสดงชื่อ"
            );

            assertTrue(
                    card.findElement(By.cssSelector(".tech-meta")).isDisplayed(),
                    "การ์ดช่างต้องแสดงข้อมูลผู้ใช้และข้อมูลติดต่อ"
            );

            assertTrue(
                    card.findElement(
                            By.cssSelector(".tech-spec-badge")
                    ).isDisplayed(),
                    "การ์ดช่างต้องแสดงความเชี่ยวชาญ"
            );
        }
    }

    // TC-FT-17-03: ค้นหาช่างจากชื่อหรือ username
    @Test
    void TC_FT_17_03_adminCanSearchTechnician() {
        openTechnicianList();

        List<WebElement> cards = driver.findElements(
                By.cssSelector("#techList .tech-card")
        );

        assertFalse(
                cards.isEmpty(),
                "ต้องมีข้อมูลช่างอย่างน้อยหนึ่งรายการเพื่อทดสอบการค้นหา"
        );

        WebElement firstCard = cards.get(0);

        String searchKeyword = firstCard.getAttribute("data-username");

        if (searchKeyword == null || searchKeyword.isBlank()) {
            searchKeyword = firstCard.getAttribute("data-name");
        }

        assertTrue(
                searchKeyword != null && !searchKeyword.isBlank(),
                "ข้อมูลช่างต้องมีชื่อหรือ username สำหรับค้นหา"
        );

        WebElement searchInput = driver.findElement(
                By.id("searchInput")
        );

        searchInput.clear();
        searchInput.sendKeys(searchKeyword);

        final String keyword = searchKeyword.toLowerCase();

        // รอจนการ์ดที่ตรงกับคำค้นปรากฏ
        wait.until(d -> {
            List<WebElement> currentCards = d.findElements(
                    By.cssSelector("#techList .tech-card")
            );

            return currentCards.stream().anyMatch(card ->
                    card.isDisplayed()
                            && cardMatchesKeyword(card, keyword)
            );
        });

        List<WebElement> visibleCards = driver.findElements(
                By.cssSelector("#techList .tech-card")
        ).stream()
                .filter(WebElement::isDisplayed)
                .toList();

        assertFalse(
                visibleCards.isEmpty(),
                "ต้องพบรายการช่างที่ตรงกับคำค้น"
        );

        for (WebElement card : visibleCards) {
            assertTrue(
                    cardMatchesKeyword(card, keyword),
                    "รายการช่างที่มองเห็นต้องตรงกับคำค้น"
            );
        }
    }

    // TC-FT-17-04: กรองช่างตามความเชี่ยวชาญ
    @Test
    void TC_FT_17_04_adminCanFilterTechniciansBySpecialization() {
        openTechnicianList();

        List<WebElement> cards = driver.findElements(
                By.cssSelector("#techList .tech-card")
        );

        assertFalse(
                cards.isEmpty(),
                "ต้องมีข้อมูลช่างอย่างน้อยหนึ่งรายการเพื่อทดสอบตัวกรอง"
        );

        WebElement filter = driver.findElement(
                By.id("specFilter")
        );

        List<WebElement> options = filter.findElements(
                By.tagName("option")
        );

        // เลือกค่าที่มีทั้งในตัวเลือกและข้อมูลช่างจริง
        String selectedSpecialization = options.stream()
                .map(option -> option.getAttribute("value"))
                .filter(value -> value != null && !value.isBlank())
                .filter(value -> cards.stream().anyMatch(card ->
                        value.equals(card.getAttribute("data-spec"))
                ))
                .findFirst()
                .orElseThrow(() -> new AssertionError(
                        "ไม่พบข้อมูลช่างที่มีความเชี่ยวชาญตรงกับตัวเลือกในตัวกรอง"
                ));

        // เลือกความเชี่ยวชาญโดยใช้ value ของ option
        new Select(filter).selectByValue(selectedSpecialization);

        // รอจนพบการ์ดที่ตรงกับความเชี่ยวชาญที่เลือก
        wait.until(d -> {
            List<WebElement> currentCards = d.findElements(
                    By.cssSelector("#techList .tech-card")
            );

            return currentCards.stream().anyMatch(card ->
                    card.isDisplayed()
                            && selectedSpecialization.equals(
                                    card.getAttribute("data-spec")
                            )
            );
        });

        List<WebElement> visibleCards = driver.findElements(
                By.cssSelector("#techList .tech-card")
        ).stream()
                .filter(WebElement::isDisplayed)
                .toList();

        assertFalse(
                visibleCards.isEmpty(),
                "ต้องพบช่างที่ตรงกับความเชี่ยวชาญที่เลือก"
        );

        for (WebElement card : visibleCards) {
            assertEquals(
                    selectedSpecialization,
                    card.getAttribute("data-spec"),
                    "การ์ดช่างที่แสดงต้องตรงกับความเชี่ยวชาญที่เลือก"
            );
        }
    }

    // TC-FT-17-05: ค้นหาแล้วไม่พบรายการช่าง
    @Test
    void TC_FT_17_05_adminSeesNoResultForUnmatchedSearch() {
        openTechnicianList();

        List<WebElement> cards = driver.findElements(
                By.cssSelector("#techList .tech-card")
        );

        assertFalse(
                cards.isEmpty(),
                "ต้องมีข้อมูลช่างเพื่อทดสอบคำค้นที่ไม่ตรง"
        );

        WebElement searchInput = driver.findElement(
                By.id("searchInput")
        );

        String unmatchedKeyword = "NO_MATCH_" + UUID.randomUUID();

        searchInput.clear();
        searchInput.sendKeys(unmatchedKeyword);

        // รอจนหน้าเว็บแสดงสถานะไม่พบผลลัพธ์
        wait.until(d -> {
            WebElement noResult = d.findElement(
                    By.id("noResultState")
            );

            return noResult.isDisplayed();
        });

        List<WebElement> visibleCards = driver.findElements(
                By.cssSelector("#techList .tech-card")
        ).stream()
                .filter(WebElement::isDisplayed)
                .toList();

        assertTrue(
                visibleCards.isEmpty(),
                "ต้องไม่มีการ์ดช่างที่มองเห็นเมื่อไม่พบผลลัพธ์"
        );

        assertTrue(
                driver.findElement(By.id("noResultState")).isDisplayed(),
                "ต้องแสดงข้อความเมื่อไม่พบช่างที่ตรงกับคำค้น"
        );
    }

    // ตรวจสอบว่าข้อมูลในการ์ดตรงกับคำค้นหรือไม่
    private boolean cardMatchesKeyword(
            WebElement card,
            String keyword
    ) {
        return containsIgnoreCase(
                    card.getAttribute("data-name"), keyword
                )
                || containsIgnoreCase(
                    card.getAttribute("data-username"), keyword
                )
                || containsIgnoreCase(
                    card.getAttribute("data-phone"), keyword
                )
                || containsIgnoreCase(
                    card.getAttribute("data-spec"), keyword
                );
    }

    private boolean containsIgnoreCase(
            String value,
            String keyword
    ) {
        return value != null
                && value.toLowerCase().contains(keyword);
    }
}
