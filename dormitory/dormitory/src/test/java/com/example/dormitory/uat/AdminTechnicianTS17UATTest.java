
package com.example.dormitory.uat;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

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
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * TS-17: ดูรายการช่าง
 *
 * User Acceptance Test (UAT)
 *
 * ตรวจสอบการแสดงรายการช่าง การค้นหา การกรองตามความเชี่ยวชาญ
 * และการแสดงผลเมื่อไม่พบรายการที่ตรงกับคำค้น
 *
 * TC-UAT-17-01 เปิดหน้ารายการช่าง
 * TC-UAT-17-02 ตรวจสอบการแสดงข้อมูลช่าง
 * TC-UAT-17-03 ค้นหารายการช่าง
 * TC-UAT-17-04 กรองรายการช่างตามความเชี่ยวชาญ
 * TC-UAT-17-05 ตรวจสอบเมื่อไม่พบผลลัพธ์
 */
class AdminTechnicianTS17UATTest {

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

        submitButton.click();

        try {
            wait.until(currentDriver ->
                    !currentDriver.getCurrentUrl().contains("/login")
            );
        } catch (org.openqa.selenium.TimeoutException e) {
            throw new AssertionError(
                    "Admin ไม่สามารถเข้าสู่ระบบได้"
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
     * เปิดหน้าจัดการรายการช่าง
     */
    private void openTechnicianList() {
        driver.get(BASE_URL + "/admin/technicians");

        wait.until(
                ExpectedConditions.urlContains(
                        "/admin/technicians"
                )
        );

        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("main")
                )
        );

        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(".page-title")
                )
        );
    }

    /**
     * คืนค่ารายการการ์ดช่างที่กำลังแสดงอยู่
     */
    private List<WebElement> getVisibleTechnicianCards() {
        return driver.findElements(
                By.cssSelector(".tech-card")
        ).stream()
                .filter(WebElement::isDisplayed)
                .toList();
    }

    /**
     * อ่านค่า attribute โดยรองรับกรณีที่ไม่มี attribute
     */
    private String getAttribute(
            WebElement element,
            String attributeName
    ) {
        String value = element.getAttribute(attributeName);
        return value == null ? "" : value.trim();
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    /**
     * TC-UAT-17-01
     * Admin สามารถเปิดหน้ารายการช่างได้
     */
    @Test
    void TC_UAT_17_01_adminCanOpenTechnicianList() {
        openTechnicianList();

        assertTrue(
                driver.getCurrentUrl().contains(
                        "/admin/technicians"
                ),
                "ต้องอยู่บนหน้ารายการช่าง"
        );

        assertTrue(
                driver.findElement(
                        By.cssSelector("main")
                ).isDisplayed(),
                "ต้องแสดงส่วนเนื้อหาหลักของหน้า"
        );

        assertTrue(
                driver.findElement(
                        By.cssSelector(".page-title")
                ).isDisplayed(),
                "ต้องแสดงหัวข้อหน้ารายการช่าง"
        );

        assertTrue(
                driver.findElement(
                        By.id("searchInput")
                ).isDisplayed(),
                "ต้องแสดงช่องค้นหารายการช่าง"
        );
    }

    /**
     * TC-UAT-17-02
     * ตรวจสอบการแสดงข้อมูลช่างหรือสถานะเมื่อไม่มีข้อมูล
     */
    @Test
    void TC_UAT_17_02_adminCanViewTechnicianInformation() {
        openTechnicianList();

        List<WebElement> cards = getVisibleTechnicianCards();

        if (cards.isEmpty()) {
            WebElement emptyState = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            By.id("emptyState")
                    )
            );

            assertTrue(
                    emptyState.isDisplayed(),
                    "เมื่อไม่มีช่าง ต้องแสดงสถานะรายการว่าง"
            );

            assertTrue(
                    getVisibleTechnicianCards().isEmpty(),
                    "เมื่อไม่มีข้อมูล ต้องไม่แสดงการ์ดช่าง"
            );

            return;
        }

        for (WebElement card : cards) {
            assertTrue(
                    card.findElement(
                            By.cssSelector(".tech-name")
                    ).isDisplayed(),
                    "การ์ดช่างต้องแสดงชื่อช่าง"
            );

            assertTrue(
                    card.findElement(
                            By.cssSelector(".tech-meta")
                    ).isDisplayed(),
                    "การ์ดช่างต้องแสดงข้อมูลประกอบ"
            );

            assertTrue(
                    card.findElement(
                            By.cssSelector(".tech-spec-badge")
                    ).isDisplayed(),
                    "การ์ดช่างต้องแสดงความเชี่ยวชาญ"
            );
        }
    }

    /**
     * TC-UAT-17-03
     * ตรวจสอบการค้นหาช่างด้วยข้อมูลที่มีอยู่จริง
     */
    @Test
    void TC_UAT_17_03_adminCanSearchTechnician() {
        openTechnicianList();

        List<WebElement> initialCards =
                getVisibleTechnicianCards();

        assertFalse(
                initialCards.isEmpty(),
                "กรณีทดสอบค้นหาต้องมีข้อมูลช่างอย่างน้อยหนึ่งรายการ"
        );

        WebElement targetCard = initialCards.get(0);

        String keyword = getAttribute(
                targetCard,
                "data-username"
        );

        if (keyword.isBlank()) {
            keyword = getAttribute(
                    targetCard,
                    "data-name"
            );
        }

        if (keyword.isBlank()) {
            keyword = getAttribute(
                    targetCard,
                    "data-phone"
            );
        }

        assertFalse(
                keyword.isBlank(),
                "ช่างรายการแรกต้องมีข้อมูลสำหรับใช้ค้นหา"
        );

        WebElement searchInput = driver.findElement(
                By.id("searchInput")
        );

        searchInput.clear();
        searchInput.sendKeys(keyword);

        final String searchKeyword = keyword;

        wait.until(currentDriver ->
                !getVisibleTechnicianCards().isEmpty()
                        && getVisibleTechnicianCards().stream()
                        .allMatch(card ->
                                cardMatchesKeyword(
                                        card,
                                        searchKeyword
                                )
                        )
        );

        List<WebElement> resultCards =
                getVisibleTechnicianCards();

        assertFalse(
                resultCards.isEmpty(),
                "ต้องพบรายการช่างที่ตรงกับคำค้น"
        );

        for (WebElement card : resultCards) {
            assertTrue(
                    cardMatchesKeyword(card, searchKeyword),
                    "รายการช่างที่แสดงต้องตรงกับคำค้น"
            );
        }
    }

    /**
     * ตรวจสอบว่าข้อมูลช่างตรงกับคำค้นหรือไม่
     */
    private boolean cardMatchesKeyword(
            WebElement card,
            String keyword
    ) {
        String normalizedKeyword =
                keyword.toLowerCase();

        return getAttribute(card, "data-name")
                        .toLowerCase()
                        .contains(normalizedKeyword)
                || getAttribute(card, "data-username")
                        .toLowerCase()
                        .contains(normalizedKeyword)
                || getAttribute(card, "data-phone")
                        .toLowerCase()
                        .contains(normalizedKeyword)
                || getAttribute(card, "data-spec")
                        .toLowerCase()
                        .contains(normalizedKeyword);
    }

    /**
     * TC-UAT-17-04
     * ตรวจสอบการกรองช่างตามความเชี่ยวชาญ
     */
    @Test
    void TC_UAT_17_04_adminCanFilterBySpecialization() {
        openTechnicianList();

        List<WebElement> cards =
                getVisibleTechnicianCards();

        assertFalse(
                cards.isEmpty(),
                "กรณีทดสอบกรองต้องมีข้อมูลช่างอย่างน้อยหนึ่งรายการ"
        );

        Select specializationFilter = new Select(
                driver.findElement(
                        By.id("specFilter")
                )
        );

        String selectedSpecialization = null;

        for (WebElement option :
                specializationFilter.getOptions()) {

            String value = option.getAttribute("value");

            if (value == null || value.isBlank()) {
                continue;
            }

            boolean hasMatchingTechnician = cards.stream()
                    .anyMatch(card ->
                            getAttribute(card, "data-spec")
                                    .equalsIgnoreCase(value)
                    );

            if (hasMatchingTechnician) {
                selectedSpecialization = value;
                break;
            }
        }

        assertNotNull(
                selectedSpecialization,
                "ต้องมีตัวเลือกความเชี่ยวชาญที่ตรงกับข้อมูลช่าง"
        );

        specializationFilter.selectByValue(
                selectedSpecialization
        );

        final String expectedSpecialization =
                selectedSpecialization;

        wait.until(currentDriver -> {
            List<WebElement> visibleCards =
                    getVisibleTechnicianCards();

            return !visibleCards.isEmpty()
                    && visibleCards.stream().allMatch(card ->
                            getAttribute(card, "data-spec")
                                    .equalsIgnoreCase(
                                            expectedSpecialization
                                    )
                    );
        });

        List<WebElement> filteredCards =
                getVisibleTechnicianCards();

        assertFalse(
                filteredCards.isEmpty(),
                "ต้องพบช่างตามความเชี่ยวชาญที่เลือก"
        );

        for (WebElement card : filteredCards) {
            assertTrue(
                    getAttribute(card, "data-spec")
                            .equalsIgnoreCase(
                                    expectedSpecialization
                            ),
                    "รายการช่างที่แสดงต้องตรงกับความเชี่ยวชาญที่เลือก"
            );
        }
    }

    /**
     * TC-UAT-17-05
     * ตรวจสอบการแสดงผลเมื่อไม่พบรายการช่าง
     */
    @Test
    void TC_UAT_17_05_adminSeesNoResultForUnmatchedSearch() {
        openTechnicianList();

        WebElement searchInput = driver.findElement(
                By.id("searchInput")
        );

        String unmatchedKeyword =
                "NO_MATCH_" + UUID.randomUUID();

        searchInput.clear();
        searchInput.sendKeys(unmatchedKeyword);

        wait.until(currentDriver ->
                getVisibleTechnicianCards().isEmpty()
                        && currentDriver.findElement(
                                By.id("noResultState")
                        ).isDisplayed()
        );

        assertTrue(
                getVisibleTechnicianCards().isEmpty(),
                "ต้องไม่แสดงรายการช่างที่ไม่ตรงกับคำค้น"
        );

        assertTrue(
                driver.findElement(
                        By.id("noResultState")
                ).isDisplayed(),
                "ต้องแสดงข้อความเมื่อไม่พบผลลัพธ์"
        );
    }
}
