package com.example.dormitory.functional;

import java.time.Duration;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

class AdminTechnicianTS18FunctionalTest {

    private static final String BASE_URL =
            System.getProperty("baseUrl", "http://localhost:8080");

    // อ่านจาก System property / Environment variable ก่อน ถ้าไม่มีใช้ค่าเดิม
    // (ไม่ควร hardcode รหัสผ่านจริงในซอร์สโค้ดที่ขึ้น Git)
    private static final String ADMIN_EMAIL =
            System.getProperty("adminEmail",
                    System.getenv().getOrDefault("ADMIN_EMAIL", "lucaenen03@gmail.com"));
    private static final String ADMIN_PASSWORD =
            System.getProperty("adminPassword",
                    System.getenv().getOrDefault("ADMIN_PASSWORD", "adminnaib"));

    private static final String CREATE_URL = BASE_URL + "/admin/technicians/create";
    private static final String LIST_URL = BASE_URL + "/admin/technicians";

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

    // คลิกปุ่ม submit ของฟอร์มหลักในหน้า (ไม่ใช่ปุ่ม submit อื่น เช่น ปุ่ม Logout ใน navbar)
    private void clickSubmit(String formSelector) {
        WebElement button = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector(formSelector + " button[type='submit']")
                )
        );

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'});", button);

        button.click();
    }

    // เช็กว่า URL คือหน้ารายชื่อช่างจริง ๆ (ไม่รวม /create และตัด query string ออก)
    private boolean isTechnicianListUrl(String url) {
        return url.split("\\?")[0].equals(LIST_URL);
    }

    // Login ด้วยบัญชี Admin
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

        clickSubmit("form");

        try {
            wait.until(d ->
                    d.getCurrentUrl().startsWith(BASE_URL + "/admin")
            );
        } catch (TimeoutException e) {
            throw new AssertionError(
                    "Admin login ไม่สำเร็จ"
                            + "\nCurrent URL: " + driver.getCurrentUrl()
                            + "\nPage text: "
                            + driver.findElement(By.tagName("body")).getText(),
                    e
            );
        }
    }

    // เปิดหน้าสร้างบัญชีช่าง
    private void openCreateTechnicianPage() {
        driver.get(CREATE_URL);

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

        assertTrue(
                driver.findElement(By.cssSelector(".page-title"))
                        .getText().contains("สร้าง Account ช่าง"),
                "ต้องแสดงหัวข้อหน้าสร้างบัญชีช่าง"
        );
    }

    // กรอกข้อมูลที่ถูกต้องสำหรับใช้ทดสอบ
    private void fillValidTechnicianData(
            String username,
            String emailAddress
    ) {
        driver.findElement(By.name("firstName"))
                .sendKeys("Test");

        driver.findElement(By.name("lastName"))
                .sendKeys("Technician");

        driver.findElement(By.name("username"))
                .sendKeys(username);

        driver.findElement(By.name("email"))
                .sendKeys(emailAddress);

        driver.findElement(By.name("phoneNo"))
                .sendKeys("0812345678");

        new Select(
                driver.findElement(By.name("specialization"))
        ).selectByValue("electric");

        driver.findElement(By.name("password"))
                .sendKeys("password123");
    }

    // ส่งแบบฟอร์มสร้างบัญชี
    private void submitCreateForm() {
        clickSubmit("main form");
    }

    // รอจนกว่าจะมีข้อความ error ปรากฏ
    // (จากฝั่ง server เช่น .form-hint หรือจาก HTML5 validation ของ browser)
    // ใช้รอแทนการรอ .page-title เพราะ .page-title มีอยู่แล้วก่อนส่งฟอร์ม
    // ทำให้เกิด race condition (assert ก่อนหน้าโหลดใหม่เสร็จ)
    private boolean waitForValidationError() {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(10)).until(d -> {
                boolean serverError = d.findElements(
                                By.cssSelector(".form-hint[style*='color']"))
                        .stream()
                        .anyMatch(WebElement::isDisplayed);

                Object nativeInvalid = ((JavascriptExecutor) d).executeScript(
                        "var f = document.querySelector('main form');"
                                + "return f !== null && !f.checkValidity();"
                );

                return serverError || Boolean.TRUE.equals(nativeInvalid);
            });
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    // TC-FT-18-01: เปิดหน้าสร้างบัญชีช่าง
    @Test
    void TC_FT_18_01_adminCanOpenCreateTechnicianPage() {
        openCreateTechnicianPage();

        assertTrue(
                driver.getCurrentUrl().endsWith(
                        "/admin/technicians/create"
                ),
                "ต้องอยู่ที่หน้าสร้างบัญชีช่าง"
        );

        assertTrue(
                driver.findElement(By.name("firstName")).isDisplayed(),
                "ต้องแสดงช่องชื่อ"
        );

        assertTrue(
                driver.findElement(By.name("lastName")).isDisplayed(),
                "ต้องแสดงช่องนามสกุล"
        );

        assertTrue(
                driver.findElement(By.name("username")).isDisplayed(),
                "ต้องแสดงช่อง Username"
        );

        assertTrue(
                driver.findElement(By.name("email")).isDisplayed(),
                "ต้องแสดงช่องอีเมล"
        );

        assertTrue(
                driver.findElement(By.name("phoneNo")).isDisplayed(),
                "ต้องแสดงช่องเบอร์โทรศัพท์"
        );

        assertTrue(
                driver.findElement(By.name("specialization")).isDisplayed(),
                "ต้องแสดงช่องความเชี่ยวชาญ"
        );

        assertTrue(
                driver.findElement(By.name("password")).isDisplayed(),
                "ต้องแสดงช่องรหัสผ่านเริ่มต้น"
        );
    }

    // TC-FT-18-02: ตรวจสอบตัวเลือกความเชี่ยวชาญ
    @Test
    void TC_FT_18_02_specializationOptionsAreAvailable() {
        openCreateTechnicianPage();

        Select specialization = new Select(
                driver.findElement(By.name("specialization"))
        );

        assertTrue(
                specialization.getOptions().stream()
                        .anyMatch(option ->
                                "electric".equals(
                                        option.getAttribute("value")
                                )
                        ),
                "ต้องมีตัวเลือกความเชี่ยวชาญด้านไฟฟ้า"
        );

        assertTrue(
                specialization.getOptions().stream()
                        .anyMatch(option ->
                                "plumbing".equals(
                                        option.getAttribute("value")
                                )
                        ),
                "ต้องมีตัวเลือกความเชี่ยวชาญด้านประปา"
        );

        assertTrue(
                specialization.getOptions().stream()
                        .anyMatch(option ->
                                "aircon".equals(
                                        option.getAttribute("value")
                                )
                        ),
                "ต้องมีตัวเลือกความเชี่ยวชาญด้านแอร์"
        );

        assertTrue(
                specialization.getOptions().stream()
                        .anyMatch(option ->
                                "general".equals(
                                        option.getAttribute("value")
                                )
                        ),
                "ต้องมีตัวเลือกความเชี่ยวชาญงานทั่วไป"
        );
    }

    // TC-FT-18-03: ตรวจสอบการส่งแบบฟอร์มที่ไม่มีข้อมูล
    @Test
    void TC_FT_18_03_emptyFormShowsValidationErrors() {
        openCreateTechnicianPage();

        submitCreateForm();

        assertTrue(
                waitForValidationError(),
                "ต้องแสดงข้อความตรวจสอบข้อมูลในแบบฟอร์ม"
        );

        assertTrue(
                driver.getCurrentUrl().endsWith(
                        "/admin/technicians/create"
                ),
                "เมื่อไม่กรอกข้อมูล ต้องไม่เปลี่ยนไปหน้ารายชื่อช่าง"
        );
    }

    // TC-FT-18-04: ตรวจสอบเบอร์โทรศัพท์ที่ไม่ถูกต้อง
    @Test
    void TC_FT_18_04_invalidPhoneNumberShowsValidationError() {
        openCreateTechnicianPage();

        String unique = UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 12);

        fillValidTechnicianData(
                "tech" + unique,
                "tech" + unique + "@gmail.com"
        );

        WebElement phone = driver.findElement(By.name("phoneNo"));
        phone.clear();
        phone.sendKeys("12345");

        submitCreateForm();

        assertTrue(
                waitForValidationError(),
                "ต้องแสดงข้อความแจ้งข้อผิดพลาดของข้อมูล"
        );

        assertTrue(
                driver.getCurrentUrl().endsWith(
                        "/admin/technicians/create"
                ),
                "เบอร์โทรศัพท์ที่ไม่ถูกต้องต้องไม่ทำให้สร้างบัญชีสำเร็จ"
        );
    }

    // TC-FT-18-05: สร้างบัญชีช่างด้วยข้อมูลที่ถูกต้อง
    @Test
    void TC_FT_18_05_adminCanCreateTechnicianSuccessfully() {
        openCreateTechnicianPage();

        String unique = UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 12);

        String username = "tech" + unique;
        String emailAddress = "tech" + unique + "@gmail.com";

        fillValidTechnicianData(username, emailAddress);

        submitCreateForm();

        // เมื่อสร้างสำเร็จ Controller จะ redirect ไปหน้ารายชื่อช่าง
        try {
            wait.until(d -> isTechnicianListUrl(d.getCurrentUrl()));
        } catch (TimeoutException e) {
            throw new AssertionError(
                    "ไม่สามารถยืนยันการสร้างบัญชีช่างสำเร็จ"
                            + "\nCurrent URL: " + driver.getCurrentUrl()
                            + "\nPage text: "
                            + driver.findElement(By.tagName("body")).getText(),
                    e
            );
        }

        assertTrue(
                isTechnicianListUrl(driver.getCurrentUrl()),
                "เมื่อสร้างบัญชีสำเร็จ ต้องกลับไปหน้ารายชื่อช่าง"
        );
    }
}