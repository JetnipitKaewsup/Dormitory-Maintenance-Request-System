package com.example.dormitory.uat;

import java.time.Duration;

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

class ReporterRequestStatusUATTest {

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

    @AfterEach
    void tearDown() {

        if (driver != null) {
            driver.quit();
        }
    }

    private void login() {

        driver.get(BASE_URL + "/login");

        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.name("email")
                )
        );

        driver.findElement(By.name("email"))
                .clear();

        driver.findElement(By.name("email"))
                .sendKeys(EMAIL);

        driver.findElement(By.name("password"))
                .clear();

        driver.findElement(By.name("password"))
                .sendKeys(PASSWORD);

        driver.findElement(
                By.cssSelector("button[type='submit']")
        ).click();

        /*
        * รอให้การ Login ดำเนินการเสร็จ
        */
        wait.until(
                ExpectedConditions.or(
                        ExpectedConditions.urlContains("/reporter"),
                        ExpectedConditions.urlContains("/reporter/requests"),
                        ExpectedConditions.urlContains("/reporter/latest"),
                        ExpectedConditions.urlContains("/reporter/add"),
                        ExpectedConditions.urlContains("/reporter/profile"),
                        ExpectedConditions.not(
                                ExpectedConditions.urlContains("/login")
                        )
                )
        );

        /*
        * ตรวจสอบว่าไม่ได้ค้างอยู่หน้า Login
        */
        assertTrue(
                !driver.getCurrentUrl().contains("/login"),
                "Login ไม่สำเร็จ ปัจจุบันอยู่ที่: "
                        + driver.getCurrentUrl()
        );
    }

    private void openRequestHistory() {

        driver.get(
                BASE_URL + "/reporter/requests"
        );

        wait.until(
                ExpectedConditions.not(
                        ExpectedConditions.urlContains("/login")
                )
        );
    }

    private void openRequestDetail() {

        openRequestHistory();

        WebElement requestLink = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector(
                                "a[href*='/reporter/requests/']"
                        )
                )
        );

        requestLink.click();

        wait.until(
                ExpectedConditions.urlContains(
                        "/reporter/requests/"
                )
        );
    }

    @Test
    void TC_UAT_07_01_userCanOpenRequestDetail() {

        login();

        openRequestDetail();

        assertTrue(
                driver.getCurrentUrl()
                        .contains("/reporter/requests/"),
                "ไม่สามารถเปิดหน้ารายละเอียดคำร้องได้"
        );

        assertTrue(
                driver.getPageSource()
                        .contains("รายละเอียด"),
                "ไม่พบข้อมูลรายละเอียดคำร้อง"
        );
    }

    @Test
    void TC_UAT_07_02_userCanSeeCurrentRepairStatus() {

        login();

        openRequestDetail();

        WebElement statusBadge = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(".status-badge")
                )
        );

        assertTrue(
                statusBadge.isDisplayed(),
                "ไม่พบสถานะปัจจุบันของคำร้อง"
        );

        assertTrue(
                !statusBadge.getText().trim().isEmpty(),
                "สถานะคำร้องไม่มีข้อมูล"
        );
    }

    @Test
    void TC_UAT_07_03_userCanSeeStatusHistory() {

        login();

        openRequestDetail();

        String pageText =
                driver.findElement(By.tagName("body"))
                        .getText();

        assertTrue(
                pageText.contains("ประวัติ")
                        || pageText.contains("สถานะ"),
                "ไม่พบข้อมูลประวัติหรือสถานะของคำร้อง"
        );
    }

    @Test
    void TC_UAT_07_04_userCanTrackRepairProgress() {

        login();

        openRequestDetail();

        String pageText =
                driver.findElement(By.tagName("body"))
                        .getText();

        boolean hasTrackingInformation =
                pageText.contains("ส่งคำร้อง")
                        || pageText.contains("Admin")
                        || pageText.contains("อนุมัติ")
                        || pageText.contains("ช่าง")
                        || pageText.contains("ซ่อม")
                        || pageText.contains("เสร็จสิ้น");

        assertTrue(
                hasTrackingInformation,
                "ไม่พบข้อมูลสำหรับติดตามสถานะการดำเนินงาน"
        );
    }

    @Test
    void TC_UAT_07_05_userCanReturnToRequestHistory() {

        login();

        openRequestDetail();

        WebElement backLink = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector(
                                "a[href='/reporter/requests']"
                        )
                )
        );

        backLink.click();

        wait.until(
                ExpectedConditions.urlContains(
                        "/reporter/requests"
                )
        );

        assertTrue(
                driver.getCurrentUrl()
                        .contains("/reporter/requests"),
                "ไม่สามารถกลับไปหน้าประวัติการแจ้งซ่อมได้"
        );
    }
}