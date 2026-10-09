package com.example.dormitory.uat;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AdminRepairRequestStatusUATTest {

    private static final String BASE_URL = "http://localhost:8080";
    private static final String ADMIN_EMAIL = "lucaenen03@gmail.com";
    private static final String ADMIN_PASSWORD = "adminnaib";

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();

        wait = new WebDriverWait(
                driver,
                Duration.ofSeconds(20)
        );
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    // =========================================================
    // Helper: Login Admin
    // =========================================================

    private void loginAsAdmin() {

        driver.get(BASE_URL + "/login");

        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.id("email")
                )
        );

        WebElement email =
                driver.findElement(By.id("email"));

        WebElement password =
                driver.findElement(By.id("password"));

        email.clear();
        email.sendKeys(ADMIN_EMAIL);

        password.clear();
        password.sendKeys(ADMIN_PASSWORD);

        /*
         * ใช้ปุ่ม Submit จริงของหน้า Login
         * แทนการเรียก form.submit() โดยตรง
         */
        WebElement submitButton =
                wait.until(
                        ExpectedConditions.elementToBeClickable(
                                By.cssSelector(
                                        "button[type='submit'], input[type='submit']"
                                )
                        )
                );

        submitButton.click();

        try {

            wait.until(
                    ExpectedConditions.not(
                            ExpectedConditions.urlContains("/login")
                    )
            );

        } catch (TimeoutException e) {

            String currentUrl =
                    driver.getCurrentUrl();

            String pageText =
                    driver.findElement(
                            By.tagName("body")
                    ).getText();

            fail(
                    "Admin Login ไม่สำเร็จ\n"
                            + "Current URL: " + currentUrl + "\n"
                            + "Page Text:\n" + pageText
            );
        }

        assertFalse(
                driver.getCurrentUrl().contains("/login"),
                "หลัง Login ระบบยังอยู่ที่หน้า /login"
        );
    }

    // =========================================================
    // Helper: Open Inspect Page
    // =========================================================

    private void openFirstInspectPage() {

        driver.get(BASE_URL + "/admin/inspections");

        wait.until(
                ExpectedConditions.not(
                        ExpectedConditions.urlContains("/login")
                )
        );

        wait.until(
                ExpectedConditions.urlContains(
                        "/admin/inspections"
                )
        );

        List<WebElement> links =
                driver.findElements(
                        By.cssSelector("a[href]")
                );

        Pattern inspectPattern =
                Pattern.compile(
                        ".*/admin/requests/[0-9a-fA-F-]{36}/inspect$"
                );

        String inspectUrl = null;

        for (WebElement link : links) {

            String href =
                    link.getAttribute("href");

            if (href != null
                    && inspectPattern
                    .matcher(href)
                    .matches()) {

                inspectUrl = href;
                break;
            }
        }

        assertNotNull(
                inspectUrl,
                "ไม่พบลิงก์ไปยังหน้าตรวจสอบงาน"
        );

        driver.get(inspectUrl);

        wait.until(
                ExpectedConditions.urlMatches(
                        ".*/admin/requests/[0-9a-fA-F-]{36}/inspect$"
                )
        );
    }

    // =========================================================
    // TC-UAT-13-01
    // =========================================================

    @Test
    @Order(1)
    @DisplayName("TC-UAT-13-01 ตรวจสอบการเปิดหน้า Login")
    void userCanOpenLoginPage() {

        driver.get(BASE_URL + "/login");

        WebElement email =
                wait.until(
                        ExpectedConditions
                                .visibilityOfElementLocated(
                                        By.id("email")
                                )
                );

        WebElement password =
                driver.findElement(
                        By.id("password")
                );

        assertTrue(email.isDisplayed());
        assertTrue(password.isDisplayed());

        assertTrue(
                driver.getCurrentUrl()
                        .contains("/login")
        );
    }

    // =========================================================
    // TC-UAT-13-02
    // =========================================================

    @Test
    @Order(2)
    @DisplayName("TC-UAT-13-02 ตรวจสอบการกรอกข้อมูลเข้าสู่ระบบของ Admin")
    void adminCanEnterLoginCredentials() {

        driver.get(BASE_URL + "/login");

        WebElement email =
                wait.until(
                        ExpectedConditions
                                .visibilityOfElementLocated(
                                        By.id("email")
                                )
                );

        WebElement password =
                driver.findElement(
                        By.id("password")
                );

        email.sendKeys(ADMIN_EMAIL);
        password.sendKeys(ADMIN_PASSWORD);

        assertEquals(
                ADMIN_EMAIL,
                email.getAttribute("value")
        );

        assertEquals(
                ADMIN_PASSWORD,
                password.getAttribute("value")
        );
    }

    // =========================================================
    // TC-UAT-13-03
    // =========================================================

    @Test
    @Order(3)
    @DisplayName("TC-UAT-13-03 ตรวจสอบการเข้าสู่ระบบ Admin สำเร็จ")
    void adminCanLoginSuccessfully() {

        loginAsAdmin();

        assertFalse(
                driver.getCurrentUrl()
                        .contains("/login")
        );

        assertTrue(
                driver.getCurrentUrl()
                        .contains("/admin"),
                "หลัง Login ไม่พบหน้า Admin"
        );
    }

    // =========================================================
    // TC-UAT-13-04
    // =========================================================

    @Test
    @Order(4)
    @DisplayName("TC-UAT-13-04 ตรวจสอบการเปิดหน้าตรวจสอบงาน")
    void adminCanOpenInspectPage() {

        loginAsAdmin();

        openFirstInspectPage();

        assertTrue(
                driver.getCurrentUrl()
                        .matches(
                                ".*/admin/requests/[0-9a-fA-F-]{36}/inspect$"
                        )
        );

        WebElement body =
                wait.until(
                        ExpectedConditions.visibilityOfElementLocated(
                                By.tagName("body")
                        )
                );

        assertTrue(
                body.getText().contains("ตรวจ"),
                "ไม่พบข้อความที่เกี่ยวข้องกับการตรวจสอบงาน"
        );
    }

    // =========================================================
    // TC-UAT-13-05
    // =========================================================

    @Test
    @Order(5)
    @DisplayName("TC-UAT-13-05 ตรวจสอบการแสดงสถานะและการยืนยันงาน")
    void adminCanViewStatusAndCompletionControls() {

        loginAsAdmin();

        openFirstInspectPage();

        String bodyText =
                wait.until(
                        ExpectedConditions.visibilityOfElementLocated(
                                By.tagName("body")
                        )
                ).getText();

        // ตรวจสอบข้อมูลสถานะของคำร้อง
        assertTrue(
                bodyText.contains("สถานะ")
                        || bodyText.contains("สถานะปัจจุบัน"),
                "ไม่พบข้อมูลสถานะคำร้อง"
        );

        // ตรวจสอบข้อมูลการมอบหมายงาน
        assertTrue(
                bodyText.contains("ช่าง")
                        || bodyText.contains("ผู้รับผิดชอบ")
                        || bodyText.contains("มอบหมาย"),
                "ไม่พบข้อมูลการมอบหมายงาน"
        );

        // ตรวจสอบส่วนยืนยันการดำเนินงาน
        List<WebElement> submitButtons =
                driver.findElements(
                        By.cssSelector(
                                "button[type='submit'], input[type='submit']"
                        )
                );

        List<WebElement> textareas =
                driver.findElements(
                        By.cssSelector("textarea")
                );

        assertTrue(
                !submitButtons.isEmpty()
                        || !textareas.isEmpty(),
                "ไม่พบส่วนควบคุมสำหรับยืนยันการดำเนินงาน"
        );
    }
}
