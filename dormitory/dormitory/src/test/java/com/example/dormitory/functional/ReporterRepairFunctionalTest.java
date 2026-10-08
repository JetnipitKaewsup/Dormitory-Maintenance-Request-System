package com.example.dormitory.functional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;


class ReporterRepairFunctionalTest {

    private WebDriver driver;

    private WebDriverWait wait;

    private JavascriptExecutor js;

    private static final String BASE_URL =
            "http://localhost:8080";

    private static final String EMAIL =
            "lucaenen01@gmail.com";

    private static final String PASSWORD =
            "lucazaza";


    // =========================================================
    // SET UP
    // =========================================================

    @BeforeEach
    void setUp() {

        driver = new ChromeDriver();

        wait = new WebDriverWait(
                driver,
                Duration.ofSeconds(15)
        );

        js = (JavascriptExecutor) driver;

        driver.manage()
                .window()
                .maximize();
    }


    // =========================================================
    // TEAR DOWN
    // =========================================================

    @AfterEach
    void tearDown() {

        if (driver != null) {
            driver.quit();
        }
    }


    // =========================================================
    // TC-FT-03-01
    // เปิดหน้าแจ้งซ่อม
    // =========================================================

    @Test
    void TC_FT_03_01_openRepairRequestPage() {

        loginAsReporter();

        openRepairPage();

        assertTrue(
                driver.getCurrentUrl()
                        .contains("/reporter/add")
        );

        assertTrue(
                driver.findElement(
                        By.id("repairForm")
                ).isDisplayed()
        );

        assertTrue(
                driver.findElements(
                        By.cssSelector(".type-btn")
                ).size() > 0
        );

        assertTrue(
                driver.findElement(
                        By.id("repairType")
                ).isDisplayed() == false
        );

        assertTrue(
                driver.findElement(
                        By.name("description")
                ).isDisplayed()
        );

        assertTrue(
                driver.findElement(
                        By.name("preferredDate")
                ).isDisplayed()
        );

        assertTrue(
                driver.findElement(
                        By.name("startTime")
                ).isDisplayed()
        );

        assertTrue(
                driver.findElement(
                        By.name("endTime")
                ).isDisplayed()
        );
    }


    // =========================================================
    // TC-FT-03-02
    // แจ้งซ่อมด้วยข้อมูลถูกต้อง
    // =========================================================

    @Test
    void TC_FT_03_02_createRepairRequestWithValidData() {

        loginAsReporter();

        openRepairPage();

        // -----------------------------------------------------
        // เลือกประเภทงานซ่อม
        // -----------------------------------------------------

        driver.findElement(
                By.cssSelector(".type-btn")
        ).click();

        String repairTypeValue =
                driver.findElement(
                        By.id("repairType")
                ).getAttribute("value");

        assertTrue(
                repairTypeValue != null
                        && !repairTypeValue.isBlank()
        );

        // -----------------------------------------------------
        // รายละเอียด
        // -----------------------------------------------------

        driver.findElement(
                By.name("description")
        ).sendKeys(
                "ก๊อกน้ำรั่ว"
        );

        // -----------------------------------------------------
        // วันที่พรุ่งนี้
        // -----------------------------------------------------

        String tomorrow =
                LocalDate.now()
                        .plusDays(1)
                        .format(
                                DateTimeFormatter.ISO_LOCAL_DATE
                        );

        setInputValue(
                By.name("preferredDate"),
                tomorrow
        );

        // -----------------------------------------------------
        // เวลา
        // -----------------------------------------------------

        setInputValue(
                By.name("startTime"),
                "09:00"
        );

        setInputValue(
                By.name("endTime"),
                "10:00"
        );

        // -----------------------------------------------------
        // ตรวจสอบค่าที่กรอกจริง
        // -----------------------------------------------------

        assertEquals(
                tomorrow,
                getInputValue(
                        By.name("preferredDate")
                )
        );

        assertEquals(
                "09:00",
                getInputValue(
                        By.name("startTime")
                )
        );

        assertEquals(
                "10:00",
                getInputValue(
                        By.name("endTime")
                )
        );

        // -----------------------------------------------------
        // Submit
        // -----------------------------------------------------

        driver.findElement(
                By.cssSelector(".btn-submit")
        ).click();

        // -----------------------------------------------------
        // Success Modal
        // -----------------------------------------------------

        wait.until(
                ExpectedConditions.attributeContains(
                        By.id("successModal"),
                        "class",
                        "show"
                )
        );

        WebElement successModal =
                driver.findElement(
                        By.id("successModal")
                );

        assertTrue(
                successModal.getAttribute("class")
                        .contains("show")
        );

        assertTrue(
                successModal.getText()
                        .contains("ส่งคำร้องสำเร็จ")
        );

        // -----------------------------------------------------
        // Redirect
        // -----------------------------------------------------

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


    // =========================================================
    // TC-FT-03-03
    // Submit ฟอร์มว่าง
    // =========================================================

    @Test
    void TC_FT_03_03_submitEmptyRepairForm() {

        loginAsReporter();

        openRepairPage();

        driver.findElement(
                By.cssSelector(".btn-submit")
        ).click();

        // -----------------------------------------------------
        // Error Modal
        // -----------------------------------------------------

        wait.until(
                ExpectedConditions.attributeContains(
                        By.id("errorModal"),
                        "class",
                        "show"
                )
        );

        WebElement errorModal =
                driver.findElement(
                        By.id("errorModal")
                );

        assertTrue(
                errorModal.getAttribute("class")
                        .contains("show")
        );

        assertEquals(
                "กรุณาเลือกประเภทงานซ่อม",
                driver.findElement(
                        By.id("errorMessage")
                ).getText()
        );

        assertTrue(
                driver.getCurrentUrl()
                        .contains("/reporter/add")
        );
    }


    // =========================================================
    // TC-FT-03-04
    // วันที่ในอดีต
    // =========================================================

    @Test
    void TC_FT_03_04_submitPastDate() {

        loginAsReporter();

        openRepairPage();

        // -----------------------------------------------------
        // เลือกประเภท
        // -----------------------------------------------------

        driver.findElement(
                By.cssSelector(".type-btn")
        ).click();

        // -----------------------------------------------------
        // รายละเอียด
        // -----------------------------------------------------

        driver.findElement(
                By.name("description")
        ).sendKeys(
                "ทดสอบวันที่ในอดีต"
        );

        // -----------------------------------------------------
        // เมื่อวาน
        // -----------------------------------------------------

        String yesterday =
                LocalDate.now()
                        .minusDays(1)
                        .format(
                                DateTimeFormatter.ISO_LOCAL_DATE
                        );

        setInputValue(
                By.name("preferredDate"),
                yesterday
        );

        // -----------------------------------------------------
        // เวลา
        // -----------------------------------------------------

        setInputValue(
                By.name("startTime"),
                "09:00"
        );

        setInputValue(
                By.name("endTime"),
                "10:00"
        );

        // -----------------------------------------------------
        // ตรวจสอบค่าก่อน Submit
        // -----------------------------------------------------

        assertEquals(
                yesterday,
                getInputValue(
                        By.name("preferredDate")
                )
        );

        assertEquals(
                "09:00",
                getInputValue(
                        By.name("startTime")
                )
        );

        assertEquals(
                "10:00",
                getInputValue(
                        By.name("endTime")
                )
        );

        // -----------------------------------------------------
        // Submit
        // -----------------------------------------------------

        driver.findElement(
                By.cssSelector(".btn-submit")
        ).click();

        // -----------------------------------------------------
        // Error Modal
        // -----------------------------------------------------

        wait.until(
                ExpectedConditions.attributeContains(
                        By.id("errorModal"),
                        "class",
                        "show"
                )
        );

        WebElement errorModal =
                driver.findElement(
                        By.id("errorModal")
                );

        assertTrue(
                errorModal.getAttribute("class")
                        .contains("show")
        );

        assertEquals(
                "ไม่สามารถเลือกวันที่ในอดีตได้",
                driver.findElement(
                        By.id("errorMessage")
                ).getText()
        );

        assertTrue(
                driver.getCurrentUrl()
                        .contains("/reporter/add")
        );
    }


    // =========================================================
    // TC-FT-03-05
    // เวลาสิ้นสุดน้อยกว่าหรือเท่ากับเวลาเริ่ม
    // =========================================================

    @Test
    void TC_FT_03_05_submitInvalidTimeRange() {

        loginAsReporter();

        openRepairPage();

        // -----------------------------------------------------
        // เลือกประเภท
        // -----------------------------------------------------

        driver.findElement(
                By.cssSelector(".type-btn")
        ).click();

        // -----------------------------------------------------
        // รายละเอียด
        // -----------------------------------------------------

        driver.findElement(
                By.name("description")
        ).sendKeys(
                "ทดสอบเวลาไม่ถูกต้อง"
        );

        // -----------------------------------------------------
        // วันที่พรุ่งนี้
        // -----------------------------------------------------

        String tomorrow =
                LocalDate.now()
                        .plusDays(1)
                        .format(
                                DateTimeFormatter.ISO_LOCAL_DATE
                        );

        setInputValue(
                By.name("preferredDate"),
                tomorrow
        );

        // -----------------------------------------------------
        // Start = 10:00
        // End = 09:00
        // -----------------------------------------------------

        setInputValue(
                By.name("startTime"),
                "10:00"
        );

        setInputValue(
                By.name("endTime"),
                "09:00"
        );

        // -----------------------------------------------------
        // ตรวจสอบค่าจริง
        // -----------------------------------------------------

        assertEquals(
                "10:00",
                getInputValue(
                        By.name("startTime")
                )
        );

        assertEquals(
                "09:00",
                getInputValue(
                        By.name("endTime")
                )
        );

        // -----------------------------------------------------
        // Submit
        // -----------------------------------------------------

        driver.findElement(
                By.cssSelector(".btn-submit")
        ).click();

        // -----------------------------------------------------
        // Error Modal
        // -----------------------------------------------------

        wait.until(
                ExpectedConditions.attributeContains(
                        By.id("errorModal"),
                        "class",
                        "show"
                )
        );

        WebElement errorModal =
                driver.findElement(
                        By.id("errorModal")
                );

        assertTrue(
                errorModal.getAttribute("class")
                        .contains("show")
        );

        assertEquals(
                "เวลาสิ้นสุดต้องมากกว่าเวลาเริ่ม",
                driver.findElement(
                        By.id("errorMessage")
                ).getText()
        );

        assertTrue(
                driver.getCurrentUrl()
                        .contains("/reporter/add")
        );
    }


    // =========================================================
    // OPEN REPAIR PAGE
    // =========================================================

    private void openRepairPage() {

        driver.get(
                BASE_URL + "/reporter/add"
        );

        wait.until(
                ExpectedConditions.presenceOfElementLocated(
                        By.id("repairForm")
                )
        );

        assertTrue(
                driver.getCurrentUrl()
                        .contains("/reporter/add")
        );
    }


    // =========================================================
    // LOGIN
    // =========================================================

    private void loginAsReporter() {

        driver.get(
                BASE_URL + "/login"
        );

        wait.until(
                ExpectedConditions.presenceOfElementLocated(
                        By.id("email")
                )
        );

        WebElement email =
                driver.findElement(
                        By.id("email")
                );

        WebElement password =
                driver.findElement(
                        By.id("password")
                );

        email.clear();

        email.sendKeys(
                EMAIL
        );

        password.clear();

        password.sendKeys(
                PASSWORD
        );

        assertEquals(
                EMAIL,
                email.getAttribute("value")
        );

        assertEquals(
                PASSWORD,
                password.getAttribute("value")
        );

        driver.findElement(
                By.cssSelector(
                        "button.submit-button[type='submit']"
                )
        ).click();

        wait.until(
                ExpectedConditions.not(
                        ExpectedConditions.urlContains(
                                "/login"
                        )
                )
        );

        assertTrue(
                !driver.getCurrentUrl()
                        .contains("/login"),
                "Login ไม่สำเร็จ: ระบบยังอยู่หน้า Login"
        );
    }


    // =========================================================
    // SET INPUT VALUE
    // =========================================================

    private void setInputValue(
            By locator,
            String value) {

        WebElement element =
                wait.until(
                        ExpectedConditions.presenceOfElementLocated(
                                locator
                        )
                );

        js.executeScript(
                """
                const element = arguments[0];
                const value = arguments[1];

                const prototype =
                    Object.getPrototypeOf(element);

                const descriptor =
                    Object.getOwnPropertyDescriptor(
                        prototype,
                        'value'
                    );

                if (descriptor && descriptor.set) {
                    descriptor.set.call(element, value);
                } else {
                    element.value = value;
                }

                element.dispatchEvent(
                    new Event('input', {
                        bubbles: true
                    })
                );

                element.dispatchEvent(
                    new Event('change', {
                        bubbles: true
                    })
                );
                """,
                element,
                value
        );
    }


    // =========================================================
    // GET INPUT VALUE
    // =========================================================

    private String getInputValue(By locator) {

        WebElement element =
                driver.findElement(locator);

        return element.getAttribute("value");
    }
}