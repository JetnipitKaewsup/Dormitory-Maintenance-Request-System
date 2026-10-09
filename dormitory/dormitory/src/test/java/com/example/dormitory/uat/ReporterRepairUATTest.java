package com.example.dormitory.uat;

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

class ReporterRepairUATTest {

    private WebDriver driver;
    private WebDriverWait wait;
    private JavascriptExecutor js;

    private static final String BASE_URL = "http://localhost:8080";

    private static final String EMAIL = "lucaenen01@gmail.com";
    private static final String PASSWORD = "lucazaza";

    @BeforeEach
    void setUp() {

        driver = new ChromeDriver();

        wait = new WebDriverWait(
                driver,
                Duration.ofSeconds(15)
        );

        js = (JavascriptExecutor) driver;

        driver.manage().window().maximize();
    }

    @AfterEach
    void tearDown() {

        if (driver != null) {
            driver.quit();
        }
    }

    // =========================================================
    // TC-UAT-03-01
    // เปิดหน้าแจ้งซ่อม
    // =========================================================

    @Test
    void TC_UAT_03_01_openRepairRequestPage() {

        loginAsReporter();

        openRepairPage();

        assertTrue(
                driver.getCurrentUrl().contains("/reporter/add"),
                "ระบบไม่ได้เข้าสู่หน้าแจ้งซ่อม"
        );

        assertTrue(
                driver.findElement(By.id("repairForm")).isDisplayed(),
                "ไม่พบแบบฟอร์มแจ้งซ่อม"
        );

        assertTrue(
                driver.findElements(By.cssSelector(".type-btn")).size() > 0,
                "ไม่พบประเภทงานซ่อม"
        );

        assertTrue(
                driver.findElement(By.name("description")).isDisplayed(),
                "ไม่พบช่องรายละเอียดปัญหา"
        );

        assertTrue(
                driver.findElement(By.name("preferredDate")).isDisplayed(),
                "ไม่พบช่องวันที่"
        );

        assertTrue(
                driver.findElement(By.name("startTime")).isDisplayed(),
                "ไม่พบเวลาเริ่ม"
        );

        assertTrue(
                driver.findElement(By.name("endTime")).isDisplayed(),
                "ไม่พบเวลาสิ้นสุด"
        );
    }

    // =========================================================
    // TC-UAT-03-02
    // แจ้งซ่อมด้วยข้อมูลที่ถูกต้อง
    // =========================================================

    @Test
    void TC_UAT_03_02_createRepairRequestWithValidData() {

        loginAsReporter();

        openRepairPage();

        // เลือกประเภทงานซ่อม
        driver.findElement(
                By.cssSelector(".type-btn")
        ).click();

        String repairTypeValue =
                driver.findElement(
                        By.id("repairType")
                ).getAttribute("value");

        assertTrue(
                repairTypeValue != null
                        && !repairTypeValue.isBlank(),
                "ไม่ได้เลือกประเภทงานซ่อม"
        );

        // กรอกรายละเอียด
        driver.findElement(
                By.name("description")
        ).sendKeys("ก๊อกน้ำรั่ว");

        // วันที่วันพรุ่งนี้
        String tomorrow =
                LocalDate.now()
                        .plusDays(1)
                        .format(DateTimeFormatter.ISO_LOCAL_DATE);

        setInputValue(
                By.name("preferredDate"),
                tomorrow
        );

        // เวลา
        setInputValue(
                By.name("startTime"),
                "09:00"
        );

        setInputValue(
                By.name("endTime"),
                "10:00"
        );

        // ตรวจสอบข้อมูลก่อนส่ง
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

        // กดส่งคำร้อง
        driver.findElement(
                By.cssSelector(".btn-submit")
        ).click();

        // รอ Success Modal
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

        // ระบบ redirect ไปหน้าประวัติคำร้อง
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
    // TC-UAT-03-03
    // แจ้งซ่อมโดยไม่กรอกข้อมูลที่จำเป็น
    // =========================================================

    @Test
    void TC_UAT_03_03_submitEmptyRepairForm() {

        loginAsReporter();

        openRepairPage();

        // ไม่กรอกข้อมูลใด ๆ
        driver.findElement(
                By.cssSelector(".btn-submit")
        ).click();

        // รอ Error Modal
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

        // ต้องยังอยู่หน้าแจ้งซ่อม
        assertTrue(
                driver.getCurrentUrl()
                        .contains("/reporter/add")
        );
    }

    // =========================================================
    // TC-UAT-03-04
    // แจ้งซ่อมโดยเลือกวันที่ในอดีต
    // =========================================================

    @Test
    void TC_UAT_03_04_submitPastDate() {

        loginAsReporter();

        openRepairPage();

        // เลือกประเภทงานซ่อม
        driver.findElement(
                By.cssSelector(".type-btn")
        ).click();

        // กรอกรายละเอียด
        driver.findElement(
                By.name("description")
        ).sendKeys("ทดสอบวันที่ในอดีต");

        // เมื่อวาน
        String yesterday =
                LocalDate.now()
                        .minusDays(1)
                        .format(DateTimeFormatter.ISO_LOCAL_DATE);

        setInputValue(
                By.name("preferredDate"),
                yesterday
        );

        setInputValue(
                By.name("startTime"),
                "09:00"
        );

        setInputValue(
                By.name("endTime"),
                "10:00"
        );

        // ตรวจสอบค่าที่ใส่
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

        // ส่งคำร้อง
        driver.findElement(
                By.cssSelector(".btn-submit")
        ).click();

        // รอ Error Modal
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

        // ต้องไม่ถูกส่งคำร้อง
        assertTrue(
                driver.getCurrentUrl()
                        .contains("/reporter/add")
        );
    }

    // =========================================================
    // TC-UAT-03-05
    // แจ้งซ่อมโดยระบุช่วงเวลาไม่ถูกต้อง
    // =========================================================

    @Test
    void TC_UAT_03_05_submitInvalidTimeRange() {

        loginAsReporter();

        openRepairPage();

        // เลือกประเภทงานซ่อม
        driver.findElement(
                By.cssSelector(".type-btn")
        ).click();

        // กรอกรายละเอียด
        driver.findElement(
                By.name("description")
        ).sendKeys("ทดสอบเวลาไม่ถูกต้อง");

        // วันที่วันพรุ่งนี้
        String tomorrow =
                LocalDate.now()
                        .plusDays(1)
                        .format(DateTimeFormatter.ISO_LOCAL_DATE);

        setInputValue(
                By.name("preferredDate"),
                tomorrow
        );

        // เวลาเริ่ม 10:00
        setInputValue(
                By.name("startTime"),
                "10:00"
        );

        // เวลาสิ้นสุด 09:00
        setInputValue(
                By.name("endTime"),
                "09:00"
        );

        // ตรวจสอบค่าที่ใส่
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

        // ส่งคำร้อง
        driver.findElement(
                By.cssSelector(".btn-submit")
        ).click();

        // รอ Error Modal
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

        // ต้องยังอยู่หน้าแจ้งซ่อม
        assertTrue(
                driver.getCurrentUrl()
                        .contains("/reporter/add")
        );
    }

    // =========================================================
    // Helper: เปิดหน้าแจ้งซ่อม
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
    // Helper: Login Reporter
    // =========================================================

    private void loginAsReporter() {

        driver.get(BASE_URL + "/login");

        wait.until(
                ExpectedConditions.presenceOfElementLocated(
                        By.id("email")
                )
        );

        WebElement email =
                driver.findElement(By.id("email"));

        WebElement password =
                driver.findElement(By.id("password"));

        email.clear();
        email.sendKeys(EMAIL);

        password.clear();
        password.sendKeys(PASSWORD);

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

        /*
        * รอจน Login สำเร็จ
        *
        * ระบบอาจใช้เวลาในการประมวลผล Login
        * จึงตรวจทั้ง URL และหน้า Reporter
        */
        try {

            wait.until(
                    ExpectedConditions.or(
                            ExpectedConditions.urlContains("/reporter"),
                            ExpectedConditions.urlContains("/reporter/add"),
                            ExpectedConditions.urlContains("/reporter/requests")
                    )
            );

        } catch (Exception e) {

            // ถ้ายังอยู่หน้า Login ให้ตรวจสอบข้อความผิดพลาด
            if (driver.getCurrentUrl().contains("/login")) {

                String pageText =
                        driver.findElement(
                                By.tagName("body")
                        ).getText();

                throw new AssertionError(
                        "Login ไม่สำเร็จ\n"
                        + "Current URL: "
                        + driver.getCurrentUrl()
                        + "\nหน้าเว็บแสดง:\n"
                        + pageText,
                        e
                );
            }

            throw e;
        }

        assertTrue(
                !driver.getCurrentUrl().contains("/login"),
                "Login ไม่สำเร็จ: ระบบยังอยู่หน้า Login"
        );
    }

    // =========================================================
    // Helper: กำหนดค่า Date / Time
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

        js.executeScript("""
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
                descriptor.set.call(
                    element,
                    value
                );
            } else {
                element.value = value;
            }

            element.dispatchEvent(
                new Event(
                    'input',
                    { bubbles: true }
                )
            );

            element.dispatchEvent(
                new Event(
                    'change',
                    { bubbles: true }
                )
            );
            """,
                element,
                value
        );
    }

    // =========================================================
    // Helper: อ่านค่า Input
    // =========================================================

    private String getInputValue(By locator) {

        WebElement element =
                driver.findElement(locator);

        return element.getAttribute("value");
    }
}