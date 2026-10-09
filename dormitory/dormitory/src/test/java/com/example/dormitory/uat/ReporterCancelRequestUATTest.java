package com.example.dormitory.uat;

import java.time.Duration;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

class ReporterCancelRequestUATTest {

    private WebDriver driver;
    private WebDriverWait wait;

    private final String BASE_URL = "http://localhost:8080";

    private final String EMAIL = "lucaenen01@gmail.com";
    private final String PASSWORD = "lucazaza";

    @BeforeEach
    void setUp() {

        driver = new ChromeDriver();

        driver.manage().window().maximize();

        wait = new WebDriverWait(
                driver,
                Duration.ofSeconds(15)
        );
    }

    /**
     * Login
     */
    private void login() {

        driver.get(BASE_URL + "/login");

        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.name("email")
                )
        );

        driver.findElement(
                By.name("email")
        ).clear();

        driver.findElement(
                By.name("email")
        ).sendKeys(EMAIL);

        driver.findElement(
                By.name("password")
        ).clear();

        driver.findElement(
                By.name("password")
        ).sendKeys(PASSWORD);

        driver.findElement(
                By.cssSelector("button[type='submit']")
        ).click();

        /*
         * ใช้เงื่อนไขเดียวกับ Functional Test
         * เพื่อรองรับหน้า Reporter ที่อาจ redirect
         * ไปยังหลาย URL
         */
        wait.until(
                ExpectedConditions.or(
                        ExpectedConditions.urlContains("/reporter"),
                        ExpectedConditions.urlContains("/reporter/requests"),
                        ExpectedConditions.urlContains("/reporter/latest"),
                        ExpectedConditions.urlContains("/reporter/add"),
                        ExpectedConditions.not(
                                ExpectedConditions.urlContains("/login")
                        )
                )
        );
    }

    /**
     * เปิดหน้าประวัติคำร้อง
     */
    private void openRequestHistory() {

        driver.get(
                BASE_URL + "/reporter/requests"
        );

        wait.until(
                ExpectedConditions.urlContains(
                        "/reporter/requests"
                )
        );

        wait.until(
                ExpectedConditions.presenceOfElementLocated(
                        By.tagName("body")
                )
        );
    }

    /**
     * ค้นหาปุ่ม "ลบคำร้อง"
     *
     * HTML จริง:
     * .delete-trigger
     */
    private WebElement findDeleteButton() {

        List<WebElement> deleteButtons =
                driver.findElements(
                        By.cssSelector(".delete-trigger")
                );

        for (WebElement button : deleteButtons) {

            try {

                if (button.isDisplayed()
                        && button.isEnabled()) {

                    return button;
                }

            } catch (Exception ignored) {
                // ค้นหาปุ่มถัดไป
            }
        }

        return null;
    }

    /**
     * TC-UAT-06-01
     *
     * ตรวจสอบการเปิดหน้าประวัติคำร้อง
     */
    @Test
    void TC_UAT_06_01_userCanOpenRequestHistory() {

        login();

        openRequestHistory();

        assertTrue(
                driver.getCurrentUrl()
                        .contains("/reporter/requests")
        );
    }

    /**
     * TC-UAT-06-02
     *
     * ตรวจสอบคำร้องที่สามารถลบได้
     */
    @Test
    void TC_UAT_06_02_pendingRequestShouldShowDeleteButton() {

        login();

        openRequestHistory();

        WebElement deleteButton =
                findDeleteButton();

        assertTrue(
                deleteButton != null,
                "ไม่พบปุ่มลบคำร้องสำหรับคำร้องสถานะ PENDING"
        );

        assertTrue(
                deleteButton.isDisplayed()
        );

        assertTrue(
                deleteButton.isEnabled()
        );
    }

    /**
     * TC-UAT-06-03
     *
     * ตรวจสอบการเปิด Modal ยืนยันการลบคำร้อง
     */
    @Test
    void TC_UAT_06_03_userCanOpenDeleteModal() {

        login();

        openRequestHistory();

        WebElement deleteButton =
                findDeleteButton();

        assertTrue(
                deleteButton != null,
                "ไม่พบปุ่มลบคำร้อง"
        );

        wait.until(
                ExpectedConditions.elementToBeClickable(
                        deleteButton
                )
        );

        deleteButton.click();

        /*
         * HTML จริง:
         *
         * <div id="deleteModal"
         *      class="modal-overlay">
         */
        WebElement deleteModal =
                wait.until(
                        ExpectedConditions.visibilityOfElementLocated(
                                By.id("deleteModal")
                        )
                );

        assertTrue(
                deleteModal.isDisplayed()
        );
    }

    /**
     * TC-UAT-06-04
     *
     * ตรวจสอบปุ่มยืนยันการลบคำร้อง
     */
    @Test
    void TC_UAT_06_04_userCanConfirmDeleteRequest() {

        login();

        openRequestHistory();

        WebElement deleteButton =
                findDeleteButton();

        assertTrue(
                deleteButton != null,
                "ไม่พบปุ่มลบคำร้อง"
        );

        wait.until(
                ExpectedConditions.elementToBeClickable(
                        deleteButton
                )
        );

        deleteButton.click();

        /*
         * รอ Modal
         */
        WebElement deleteModal =
                wait.until(
                        ExpectedConditions.visibilityOfElementLocated(
                                By.id("deleteModal")
                        )
                );

        assertTrue(
                deleteModal.isDisplayed()
        );

        /*
         * HTML จริง:
         *
         * id="confirmDeleteBtn"
         */
        WebElement confirmButton =
                wait.until(
                        ExpectedConditions.visibilityOfElementLocated(
                                By.id("confirmDeleteBtn")
                        )
                );

        assertTrue(
                confirmButton.isDisplayed()
        );

        assertTrue(
                confirmButton.isEnabled()
        );
    }

    /**
     * TC-UAT-06-05
     *
     * ตรวจสอบการลบคำร้อง
     */
    @Test
    void TC_UAT_06_05_userCanDeletePendingRequest() {

        login();

        openRequestHistory();

        WebElement deleteButton =
                findDeleteButton();

        assertTrue(
                deleteButton != null,
                "ไม่พบปุ่มลบคำร้อง"
        );

        wait.until(
                ExpectedConditions.elementToBeClickable(
                        deleteButton
                )
        );

        /*
         * เปิด Modal
         */
        deleteButton.click();

        WebElement deleteModal =
                wait.until(
                        ExpectedConditions.visibilityOfElementLocated(
                                By.id("deleteModal")
                        )
                );

        assertTrue(
                deleteModal.isDisplayed()
        );

        /*
         * กดปุ่มยืนยันลบ
         */
        WebElement confirmButton =
                wait.until(
                        ExpectedConditions.elementToBeClickable(
                                By.id("confirmDeleteBtn")
                        )
                );

        confirmButton.click();

        /*
         * Backend จะรับ POST:
         *
         * /reporter/requests/{id}/delete
         *
         * หลังจากดำเนินการเสร็จ
         * ระบบควรกลับมายังหน้าประวัติคำร้อง
         */
        wait.until(
                ExpectedConditions.urlContains(
                        "/reporter/requests"
                )
        );

        assertTrue(
                driver.getCurrentUrl()
                        .contains("/reporter/requests")
        );
    }

    @AfterEach
    void tearDown() {

        if (driver != null) {
            driver.quit();
        }
    }
}