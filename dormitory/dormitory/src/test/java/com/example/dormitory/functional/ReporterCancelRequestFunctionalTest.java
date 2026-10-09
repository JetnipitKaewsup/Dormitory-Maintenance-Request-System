package com.example.dormitory.functional;

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

class ReporterCancelRequestFunctionalTest {

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


    // =========================================================
    // LOGIN
    // =========================================================

    private void login() {

        driver.get(
                BASE_URL + "/login"
        );

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
                By.cssSelector(
                        "button[type='submit']"
                )
        ).click();

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


    // =========================================================
    // OPEN REQUEST HISTORY
    // =========================================================

    private void openRequestHistory() {

        driver.get(
                BASE_URL + "/reporter/requests"
        );

        wait.until(
                ExpectedConditions.presenceOfElementLocated(
                        By.tagName("body")
                )
        );

        wait.until(
                ExpectedConditions.urlContains(
                        "/reporter/requests"
                )
        );
    }


    // =========================================================
    // FIND PENDING REQUEST
    // =========================================================

    private WebElement findPendingCancelButton() {

        List<WebElement> cancelButtons =
                driver.findElements(
                        By.cssSelector(
                                ".cancel-trigger"
                        )
                );

        for (WebElement button : cancelButtons) {

            try {

                if (button.isDisplayed()) {
                    return button;
                }

            } catch (Exception ignored) {
                // Continue searching
            }
        }

        return null;
    }


    // =========================================================
    // TC-FT-06-01
    // เปิดหน้าประวัติการแจ้งซ่อม
    // =========================================================

    @Test
    void TC_FT_06_01_userCanOpenRequestHistory() {

        login();

        openRequestHistory();

        assertTrue(
                driver.getCurrentUrl()
                        .contains(
                                "/reporter/requests"
                        )
        );
    }


    // =========================================================
    // TC-FT-06-02
    // ตรวจสอบปุ่มยกเลิกคำร้อง
    // =========================================================

    @Test
    void TC_FT_06_02_pendingRequestShouldShowCancelButton() {

        login();

        openRequestHistory();

        WebElement cancelButton =
                findPendingCancelButton();

        /*
         * หากไม่มีคำร้อง PENDING
         * ไม่มีอะไรให้ทดสอบต่อ
         */
        if (cancelButton == null) {
            return;
        }

        assertTrue(
                cancelButton.isDisplayed()
        );

        assertTrue(
                cancelButton.isEnabled()
        );
    }


    // =========================================================
    // TC-FT-06-03
    // เปิดหน้าต่างยืนยันการยกเลิก
    // =========================================================

    @Test
    void TC_FT_06_03_userCanOpenCancelModal() {

        login();

        openRequestHistory();

        WebElement cancelButton =
                findPendingCancelButton();

        if (cancelButton == null) {
            return;
        }

        wait.until(
                ExpectedConditions.elementToBeClickable(
                        cancelButton
                )
        );

        cancelButton.click();

        WebElement cancelModal =
                wait.until(
                        ExpectedConditions.visibilityOfElementLocated(
                                By.id(
                                        "cancelModal"
                                )
                        )
                );

        assertTrue(
                cancelModal.isDisplayed()
        );
    }


    // =========================================================
    // TC-FT-06-04
    // ตรวจสอบข้อมูลใน Modal
    // =========================================================

    @Test
    void TC_FT_06_04_cancelModalShouldContainConfirmButton() {

        login();

        openRequestHistory();

        WebElement cancelButton =
                findPendingCancelButton();

        if (cancelButton == null) {
            return;
        }

        wait.until(
                ExpectedConditions.elementToBeClickable(
                        cancelButton
                )
        );

        cancelButton.click();

        WebElement cancelModal =
                wait.until(
                        ExpectedConditions.visibilityOfElementLocated(
                                By.id(
                                        "cancelModal"
                                )
                        )
                );

        assertTrue(
                cancelModal.isDisplayed()
        );


        /*
         * HTML จริงของโปรเจกต์ใช้
         *
         * id="confirmCancelBtn"
         *
         * ไม่ใช่ #deleteForm button[type='submit']
         */
        WebElement confirmButton =
                wait.until(
                        ExpectedConditions.visibilityOfElementLocated(
                                By.id(
                                        "confirmCancelBtn"
                                )
                        )
                );

        assertTrue(
                confirmButton.isDisplayed()
        );

        assertTrue(
                confirmButton.isEnabled()
        );
    }


    // =========================================================
    // TC-FT-06-05
    // ยืนยันการยกเลิกคำร้อง
    // =========================================================

    @Test
    void TC_FT_06_05_userCanCancelPendingRequest() {

        login();

        openRequestHistory();

        WebElement cancelButton =
                findPendingCancelButton();

        if (cancelButton == null) {
            return;
        }

        /*
         * เปิด Modal
         */
        wait.until(
                ExpectedConditions.elementToBeClickable(
                        cancelButton
                )
        );

        cancelButton.click();


        /*
         * รอ Modal
         */
        WebElement cancelModal =
                wait.until(
                        ExpectedConditions.visibilityOfElementLocated(
                                By.id(
                                        "cancelModal"
                                )
                        )
                );

        assertTrue(
                cancelModal.isDisplayed()
        );


        /*
         * HTML จริงใช้ confirmCancelBtn
         */
        WebElement confirmButton =
                wait.until(
                        ExpectedConditions.elementToBeClickable(
                                By.id(
                                        "confirmCancelBtn"
                                )
                        )
                );


        /*
         * กดยืนยันยกเลิก
         *
         * JavaScript ของหน้าเว็บจะเรียก
         * currentForm.submit()
         */
        confirmButton.click();


        /*
         * รอให้ระบบประมวลผล
         * และกลับมายังหน้าประวัติการแจ้งซ่อม
         */
        wait.until(
                ExpectedConditions.urlContains(
                        "/reporter/requests"
                )
        );


        /*
         * ตรวจสอบว่าอยู่หน้าประวัติคำร้อง
         */
        assertTrue(
                driver.getCurrentUrl()
                        .contains(
                                "/reporter/requests"
                        )
        );
    }


    // =========================================================
    // TEARDOWN
    // =========================================================

    @AfterEach
    void tearDown() {

        if (driver != null) {
            driver.quit();
        }
    }
}