package com.example.dormitory.functional;

import java.time.Duration;

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

class ReporterRequestStatusFunctionalTest {

    private WebDriver driver;
    private WebDriverWait wait;

    private static final String BASE_URL =
            "http://localhost:8080";

    private static final String EMAIL =
            "lucaenen01@gmail.com";

    private static final String PASSWORD =
            "lucazaza";

    @BeforeEach
    void setUp() {

        ChromeOptions options = new ChromeOptions();

        options.addArguments("--start-maximized");

        driver = new ChromeDriver(options);

        wait = new WebDriverWait(
                driver,
                Duration.ofSeconds(15));
    }

    @AfterEach
    void tearDown() {

        if (driver != null) {
            driver.quit();
        }
    }

    /**
     * Login เข้าสู่ระบบด้วยบัญชี Reporter
     */
    private void login() {

        driver.get(BASE_URL + "/login");

        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.name("email")));

        driver.findElement(
                By.name("email"))
                .sendKeys(EMAIL);

        driver.findElement(
                By.name("password"))
                .sendKeys(PASSWORD);

        driver.findElement(
                By.cssSelector("button[type='submit']"))
                .click();

        wait.until(
                ExpectedConditions.or(
                        ExpectedConditions.urlContains("/reporter"),
                        ExpectedConditions.urlContains(
                                "/reporter/requests"),
                        ExpectedConditions.urlContains(
                                "/reporter/latest"),
                        ExpectedConditions.urlContains(
                                "/reporter/add"),
                        ExpectedConditions.not(
                                ExpectedConditions.urlContains(
                                        "/login"))
                ));
    }

    /**
     * เปิดหน้าประวัติคำร้อง
     */
    private void openRequestHistory() {

        driver.get(
                BASE_URL + "/reporter/requests");

        wait.until(
                ExpectedConditions.not(
                        ExpectedConditions.urlContains(
                                "/login")));

        wait.until(
                ExpectedConditions.presenceOfElementLocated(
                        By.tagName("body")));
    }

    /**
     * เปิดรายละเอียดคำร้องรายการแรก
     */
    private void openFirstRequestDetail() {

        wait.until(
                ExpectedConditions.presenceOfElementLocated(
                        By.cssSelector(
                                "a[href*='/reporter/requests/']")));

        WebElement requestLink =
                driver.findElements(
                        By.cssSelector(
                                "a[href*='/reporter/requests/']"))
                        .stream()
                        .filter(element ->
                                element.isDisplayed())
                        .findFirst()
                        .orElseThrow(() ->
                                new AssertionError(
                                        "ไม่พบลิงก์รายละเอียดคำร้อง"));

        String requestUrl =
                requestLink.getAttribute("href");

        driver.get(requestUrl);

        wait.until(
                ExpectedConditions.urlContains(
                        "/reporter/requests/"));

        wait.until(
                ExpectedConditions.presenceOfElementLocated(
                        By.cssSelector(".stepper")));
    }

    @Test
    void TC_FT_07_01_userCanOpenRequestStatusPage() {

        // Arrange
        login();

        // Act
        openRequestHistory();
        openFirstRequestDetail();

        // Assert
        assertTrue(
                driver.getCurrentUrl()
                        .contains("/reporter/requests/"),
                "URL ต้องเป็นหน้ารายละเอียดคำร้อง");

        assertTrue(
                driver.findElement(
                        By.cssSelector(".stepper"))
                        .isDisplayed(),
                "ต้องแสดงส่วนติดตามขั้นตอนการดำเนินการ");
    }

    @Test
    void TC_FT_07_02_userCanSeeCurrentRequestStatus() {

        // Arrange
        login();

        // Act
        openRequestHistory();
        openFirstRequestDetail();

        // Assert
        WebElement statusBadge =
                wait.until(
                        ExpectedConditions.visibilityOfElementLocated(
                                By.cssSelector(".status-badge")));

        String status =
                statusBadge.getText().trim();

        assertFalse(
                status.isEmpty(),
                "ต้องแสดงสถานะปัจจุบันของคำร้อง");

        assertTrue(
                statusBadge.isDisplayed(),
                "Status Badge ต้องแสดงบนหน้ารายละเอียด");
    }

    @Test
    void TC_FT_07_03_userCanSeeRepairProcessStepper() {

        // Arrange
        login();

        // Act
        openRequestHistory();
        openFirstRequestDetail();

        // Assert
        WebElement stepper =
                wait.until(
                        ExpectedConditions.visibilityOfElementLocated(
                                By.cssSelector(".stepper")));

        assertTrue(
                stepper.getText()
                        .contains("1. ส่งคำร้อง"),
                "ต้องแสดงขั้นตอนที่ 1 ส่งคำร้อง");

        assertTrue(
                stepper.getText()
                        .contains("ช่างดำเนินการซ่อม"),
                "ต้องแสดงขั้นตอนการดำเนินการซ่อม");

        assertTrue(
                stepper.getText()
                        .contains("ตรวจสอบและเสร็จสิ้น"),
                "ต้องแสดงขั้นตอนตรวจสอบและเสร็จสิ้น");
    }

    @Test
    void TC_FT_07_04_userCanSeeRequestDetailInformation() {

        // Arrange
        login();

        // Act
        openRequestHistory();
        openFirstRequestDetail();

        // Assert
        WebElement detailCard =
                wait.until(
                        ExpectedConditions.visibilityOfElementLocated(
                                By.cssSelector(".detail-card")));

        String detailText =
                detailCard.getText();

        assertTrue(
                detailText.contains("ผู้แจ้ง"),
                "ต้องแสดงข้อมูลผู้แจ้ง");

        assertTrue(
                detailText.contains("เบอร์ติดต่อ"),
                "ต้องแสดงข้อมูลเบอร์ติดต่อ");

        assertTrue(
                detailText.contains("ห้องพัก"),
                "ต้องแสดงข้อมูลห้องพัก");

        assertTrue(
                detailText.contains("ประเภทงาน"),
                "ต้องแสดงข้อมูลประเภทงาน");

        assertTrue(
                detailText.contains("สถานะ"),
                "ต้องแสดงข้อมูลสถานะ");
    }

    @Test
    void TC_FT_07_05_userCanReturnToRequestHistory() {

        // Arrange
        login();

        // Act
        openRequestHistory();
        openFirstRequestDetail();

        WebElement backButton =
                wait.until(
                        ExpectedConditions.elementToBeClickable(
                                By.cssSelector(
                                        "a.back-btn[href*='/reporter/requests']")));

        backButton.click();

        // Assert
        wait.until(
                ExpectedConditions.urlContains(
                        "/reporter/requests"));

        assertTrue(
                driver.getCurrentUrl()
                        .contains("/reporter/requests"),
                "ต้องสามารถกลับไปหน้าประวัติคำร้องได้");
    }
}