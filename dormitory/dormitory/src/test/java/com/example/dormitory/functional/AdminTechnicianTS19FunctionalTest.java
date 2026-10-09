package com.example.dormitory.functional;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

class AdminTechnicianTS19FunctionalTest {

    private static final String BASE_URL =
            System.getProperty("baseUrl", "http://localhost:8080");

    private static final String LIST_URL =
            BASE_URL + "/admin/technicians";

    private static final String ADMIN_EMAIL =
            System.getProperty(
                    "adminEmail",
                    System.getenv().getOrDefault(
                            "ADMIN_EMAIL", "lucaenen03@gmail.com"));

    private static final String ADMIN_PASSWORD =
            System.getProperty(
                    "adminPassword",
                    System.getenv().getOrDefault(
                            "ADMIN_PASSWORD", "adminnaib"));

    private static final By TECHNICIAN_CARDS =
            By.cssSelector("#techList .tech-card");

    private static final By EDIT_BUTTON =
            By.cssSelector("button.edit-btn");

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    void setUp() {
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        driver.manage().window().maximize();

        loginAsAdmin();
        openTechnicianList();
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    // ---------------------------------------------------------
    // Login
    // ---------------------------------------------------------

    private void loginAsAdmin() {
        driver.get(BASE_URL + "/login");

        WebElement email = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.name("email")));

        WebElement password = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.name("password")));

        email.clear();
        email.sendKeys(ADMIN_EMAIL);

        password.clear();
        password.sendKeys(ADMIN_PASSWORD);

        wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector("form button[type='submit']")))
                .click();

        try {
            wait.until(d ->
                    d.getCurrentUrl().startsWith(BASE_URL + "/admin")
                    && !d.getCurrentUrl().contains("/login"));
        } catch (TimeoutException e) {
            String currentUrl = driver.getCurrentUrl();
            String pageText = driver.findElement(
                    By.tagName("body")).getText();

            throw new AssertionError(
                    "Admin login failed. Verify the test account and password."
                    + "\nCurrent URL: " + currentUrl
                    + "\nPage text: " + pageText,
                    e);
        }
    }

    // ---------------------------------------------------------
    // Technician list helpers
    // ---------------------------------------------------------

    private void openTechnicianList() {
        driver.get(LIST_URL);

        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(".page-title")));

        wait.until(d ->
                !d.findElements(By.id("techList")).isEmpty()
                || !d.findElements(By.id("emptyState")).isEmpty());
    }

    private List<WebElement> getVisibleCards() {
        return driver.findElements(TECHNICIAN_CARDS);
    }

    private WebElement requireFirstTechnicianCard() {
        List<WebElement> cards = getVisibleCards();

        assertFalse(
                cards.isEmpty(),
                "ต้องมีข้อมูลช่างอย่างน้อยหนึ่งรายการเพื่อทดสอบ TS-19");

        return cards.get(0);
    }

    private WebElement findCardByTechnicianId(String technicianId) {
        return wait.until(d -> {
            for (WebElement card : d.findElements(TECHNICIAN_CARDS)) {
                WebElement editButton = card.findElement(EDIT_BUTTON);

                if (technicianId.equals(
                        editButton.getAttribute("data-id"))) {
                    return card;
                }
            }
            return null;
        });
    }

    private String getTechnicianId(WebElement card) {
        String id = card.findElement(EDIT_BUTTON)
                .getAttribute("data-id");

        assertNotNull(id, "ไม่พบ technicianId ในปุ่มแก้ไข");
        assertFalse(id.isBlank(), "technicianId ต้องไม่เป็นค่าว่าง");

        return id;
    }

    private WebElement getEditButton(WebElement card) {
        return card.findElement(EDIT_BUTTON);
    }

    // ---------------------------------------------------------
    // Edit modal helpers
    // ---------------------------------------------------------

    private void openEditModal(WebElement card) {
        WebElement editButton = getEditButton(card);

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'});",
                editButton);

        wait.until(
                ExpectedConditions.elementToBeClickable(editButton))
                .click();

        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.id("editModalBackdrop")));

        wait.until(d -> {
            WebElement modal = d.findElement(
                    By.id("editModalBackdrop"));

            return modal.getAttribute("class") != null
                    && modal.getAttribute("class").contains("open");
        });
    }

    private void fillEditForm(
            String firstName,
            String lastName,
            String phoneNo) {

        setInputValue("edit-firstName", firstName);
        setInputValue("edit-lastName", lastName);
        setInputValue("edit-phoneNo", phoneNo);
    }

    private void setInputValue(String elementId, String value) {
        WebElement input = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.id(elementId)));

        input.clear();

        if (value != null) {
            input.sendKeys(value);
        }
    }

    private String getInputValue(String elementId) {
        return driver.findElement(By.id(elementId))
                .getAttribute("value");
    }

    private String getSelectValue(String elementId) {
        WebElement element = driver.findElement(By.id(elementId));
        return new Select(element).getFirstSelectedOption()
                .getAttribute("value");
    }

    private void selectSpecializationIfAvailable(String value) {
        if (value == null || value.isBlank()) {
            return;
        }

        Select select = new Select(
                driver.findElement(By.id("edit-specialization")));

        boolean optionExists = select.getOptions().stream()
                .anyMatch(option ->
                        value.equals(option.getAttribute("value")));

        if (optionExists) {
            select.selectByValue(value);
        }
    }

    private void submitEditForm() {
        WebElement modal = driver.findElement(
                By.id("editModalBackdrop"));

        WebElement form = modal.findElement(
                By.cssSelector("form"));

        WebElement submit = form.findElement(
                By.cssSelector("button[type='submit'], input[type='submit']"));

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'});",
                submit);

        wait.until(
                ExpectedConditions.elementToBeClickable(submit))
                .click();
    }

    private void cancelEditModal() {
        WebElement cancel = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.id("cancelEditBtn")));

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].click();", cancel);

        wait.until(d -> {
            WebElement backdrop = d.findElement(
                    By.id("editModalBackdrop"));

            String classes = backdrop.getAttribute("class");
            return classes == null || !classes.contains("open");
        });
    }

    private void waitForListAfterSubmit() {
        wait.until(d ->
                d.getCurrentUrl().split("\\?")[0].equals(LIST_URL));

        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(".page-title")));
    }

    private String getCardName(WebElement card) {
        return card.getAttribute("data-name");
    }

    private String getCardPhone(WebElement card) {
        return card.getAttribute("data-phone");
    }

    private String getCardSpecialization(WebElement card) {
        return getEditButton(card).getAttribute("data-spec");
    }

    private String normalized(String value) {
        return value == null ? "" : value.trim();
    }

    private void assertCardData(
            String technicianId,
            String expectedName,
            String expectedPhone) {

        WebElement card = findCardByTechnicianId(technicianId);

        assertEquals(
                normalized(expectedName),
                normalized(getCardName(card)),
                "ชื่อช่างหลังบันทึกไม่ตรงกับค่าที่คาดหวัง");

        assertEquals(
                normalized(expectedPhone),
                normalized(getCardPhone(card)),
                "เบอร์โทรศัพท์หลังบันทึกไม่ตรงกับค่าที่คาดหวัง");
    }

    private void restoreOriginalData(
            String technicianId,
            String originalFirstName,
            String originalLastName,
            String originalPhone,
            String originalSpecialization) {

        openTechnicianList();

        WebElement card = findCardByTechnicianId(technicianId);
        openEditModal(card);

        fillEditForm(
                originalFirstName,
                originalLastName,
                originalPhone);

        selectSpecializationIfAvailable(originalSpecialization);

        submitEditForm();
        waitForListAfterSubmit();

        assertCardData(
                technicianId,
                normalized(originalFirstName) + " "
                        + normalized(originalLastName),
                originalPhone);
    }

    // ---------------------------------------------------------
    // TC-FT-19-01
    // ตรวจสอบการเปิดหน้ารายการช่าง
    // ---------------------------------------------------------

    @Test
    void TC_FT_19_01_adminCanOpenTechnicianList() {
        assertTrue(
                driver.getCurrentUrl().split("\\?")[0].equals(LIST_URL),
                "ควรเปิดหน้ารายการช่างได้");

        assertTrue(
                driver.findElements(By.cssSelector(".page-title"))
                        .size() > 0,
                "ควรแสดงหัวข้อหน้ารายการช่าง");
    }

    // ---------------------------------------------------------
    // TC-FT-19-02
    // ตรวจสอบว่าค่าเดิมแสดงใน Modal
    // ---------------------------------------------------------

    @Test
    void TC_FT_19_02_editModalShowsExistingTechnicianData() {
        WebElement card = requireFirstTechnicianCard();
        WebElement editButton = getEditButton(card);

        String originalFirstName =
                editButton.getAttribute("data-firstname");
        String originalLastName =
                editButton.getAttribute("data-lastname");
        String originalPhone =
                editButton.getAttribute("data-phone");
        String originalSpec =
                editButton.getAttribute("data-spec");

        openEditModal(card);

        assertEquals(
                normalized(originalFirstName),
                normalized(getInputValue("edit-firstName")),
                "ช่องชื่อควรแสดงข้อมูลเดิม");

        assertEquals(
                normalized(originalLastName),
                normalized(getInputValue("edit-lastName")),
                "ช่องนามสกุลควรแสดงข้อมูลเดิม");

        assertEquals(
                normalized(originalPhone),
                normalized(getInputValue("edit-phoneNo")),
                "ช่องเบอร์โทรศัพท์ควรแสดงข้อมูลเดิม");

        /*
         * ตรวจสอบ specialization เฉพาะเมื่อค่าจากการ์ดตรงกับ
         * value ของ option ใน select เพื่อป้องกันการเปรียบเทียบ
         * display text กับ option value คนละรูปแบบ
         */
        if (originalSpec != null && !originalSpec.isBlank()) {
            Select select = new Select(
                    driver.findElement(
                            By.id("edit-specialization")));

            boolean optionExists = select.getOptions().stream()
                    .anyMatch(option ->
                            originalSpec.equals(
                                    option.getAttribute("value")));

            if (optionExists) {
                assertEquals(
                        originalSpec,
                        getSelectValue("edit-specialization"),
                        "ความเชี่ยวชาญควรแสดงค่าที่เลือกไว้เดิม");
            } else {
                System.out.println(
                        "INFO: data-spec ไม่ตรงกับ option value: "
                                + originalSpec);
            }
        }

        assertTrue(
                originalFirstName != null
                        && !originalFirstName.isBlank(),
                "การ์ดควรมีชื่อช่าง");
    }

    // ---------------------------------------------------------
    // TC-FT-19-03
    // ยกเลิกการแก้ไขแล้วข้อมูลต้องไม่เปลี่ยน
    // ---------------------------------------------------------

    @Test
    void TC_FT_19_03_cancelEditDoesNotChangeTechnicianData() {
        WebElement card = requireFirstTechnicianCard();
        String technicianId = getTechnicianId(card);

        String originalName = getCardName(card);
        String originalPhone = getCardPhone(card);

        WebElement editButton = getEditButton(card);
        String originalFirstName =
                editButton.getAttribute("data-firstname");
        String originalLastName =
                editButton.getAttribute("data-lastname");

        openEditModal(card);

        fillEditForm(
                "CancelTest",
                "NotSaved",
                "0898765432");

        cancelEditModal();

        WebElement cardAfterCancel =
                findCardByTechnicianId(technicianId);

        assertEquals(
                normalized(originalName),
                normalized(getCardName(cardAfterCancel)),
                "การยกเลิกต้องไม่เปลี่ยนชื่อบนการ์ด");

        assertEquals(
                normalized(originalPhone),
                normalized(getCardPhone(cardAfterCancel)),
                "การยกเลิกต้องไม่เปลี่ยนเบอร์โทรศัพท์บนการ์ด");

        // ป้องกันตัวแปรเดิมไม่ถูกใช้โดยไม่ตั้งใจ
        assertNotNull(originalFirstName);
        assertNotNull(originalLastName);
    }

    // ---------------------------------------------------------
    // TC-FT-19-04
    // เบอร์โทรศัพท์ไม่ถูกต้องต้องไม่บันทึก
    // ---------------------------------------------------------

    @Test
    void TC_FT_19_04_invalidPhoneNumberDoesNotUpdateTechnician() {
        WebElement card = requireFirstTechnicianCard();
        String technicianId = getTechnicianId(card);

        String originalName = getCardName(card);
        String originalPhone = getCardPhone(card);

        WebElement editButton = getEditButton(card);
        String firstName =
                editButton.getAttribute("data-firstname");
        String lastName =
                editButton.getAttribute("data-lastname");

        openEditModal(card);

        fillEditForm(firstName, lastName, "12345");
        submitEditForm();

        waitForListAfterSubmit();

        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(".alert-error")));

        WebElement cardAfterSubmit =
                findCardByTechnicianId(technicianId);

        assertEquals(
                normalized(originalName),
                normalized(getCardName(cardAfterSubmit)),
                "ชื่อช่างต้องไม่เปลี่ยนเมื่อส่งเบอร์โทรศัพท์ไม่ถูกต้อง");

        assertEquals(
                normalized(originalPhone),
                normalized(getCardPhone(cardAfterSubmit)),
                "เบอร์โทรศัพท์ต้องไม่เปลี่ยนเมื่อข้อมูลไม่ผ่าน validation");
    }

    // ---------------------------------------------------------
    // TC-FT-19-05
    // แก้ไขข้อมูลช่างสำเร็จ
    // ---------------------------------------------------------

    @Test
    void TC_FT_19_05_adminCanUpdateTechnicianSuccessfully() {
        WebElement card = requireFirstTechnicianCard();
        String technicianId = getTechnicianId(card);

        WebElement editButton = getEditButton(card);

        String originalFirstName =
                editButton.getAttribute("data-firstname");
        String originalLastName =
                editButton.getAttribute("data-lastname");
        String originalPhone =
                editButton.getAttribute("data-phone");
        String originalSpec =
                editButton.getAttribute("data-spec");

        String updatedFirstName =
                "Test" + UUID.randomUUID()
                        .toString().substring(0, 8);

        String updatedLastName = "Updated";
        String updatedPhone = "0898765432";

        String updatedName =
                updatedFirstName + " " + updatedLastName;

        try {
            openEditModal(card);

            fillEditForm(
                    updatedFirstName,
                    updatedLastName,
                    updatedPhone);

            // คงความเชี่ยวชาญเดิมไว้ ไม่เปลี่ยนฟิลด์ที่ไม่เกี่ยวข้อง
            selectSpecializationIfAvailable(originalSpec);

            submitEditForm();
            waitForListAfterSubmit();

            wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            By.cssSelector(".alert-success")));

            assertCardData(
                    technicianId,
                    updatedName,
                    updatedPhone);

        } finally {
            /*
             * คืนค่าข้อมูลเดิมหลังทดสอบ
             * หากล็อกอินหมดอายุหรือการคืนค่าล้มเหลว จะรายงาน WARNING
             * ใน Console เพื่อให้ผู้ทดสอบตรวจสอบข้อมูลด้วยตนเอง
             */
            try {
                restoreOriginalData(
                        technicianId,
                        originalFirstName,
                        originalLastName,
                        originalPhone,
                        originalSpec);

                System.out.println(
                        "INFO: Restored technician data. ID="
                                + technicianId);

            } catch (Exception cleanupError) {
                System.err.println(
                        "WARNING: Could not restore technician data."
                                + " technicianId=" + technicianId
                                + ", reason="
                                + cleanupError.getMessage());

                // อย่ากลบข้อมูลผิดพลาดหรือแสร้งว่าคืนค่าสำเร็จ
                // ให้ตรวจสอบรายการช่างนี้ด้วยตนเองหลังการทดสอบ
            }
        }
    }
}

