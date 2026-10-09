package com.example.dormitory.uat;

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
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

class LoginUATTest {

    private WebDriver driver;
    private WebDriverWait wait;

    private static final String BASE_URL =
            "http://localhost:8080";

    private static final String VALID_EMAIL =
            "lucaenen01@gmail.com";

    private static final String VALID_PASSWORD =
            "lucazaza";

    @BeforeEach
    void setUp() {

        driver = new ChromeDriver();

        driver.manage()
                .window()
                .maximize();

        wait = new WebDriverWait(
                driver,
                Duration.ofSeconds(15)
        );

        driver.get(BASE_URL + "/login");
    }

    @AfterEach
    void tearDown() {

        if (driver != null) {
            driver.quit();
        }
    }

    /**
     * TC-UAT-02-01
     *
     * ผู้ใช้สามารถเข้าสู่ระบบด้วยข้อมูลที่ถูกต้อง
     * และสามารถเข้าสู่หน้าหลักตามสิทธิ์ของตนเองได้
     */
    @Test
    void TC_UAT_02_01_userCanLoginWithValidData() {

        WebElement email =
                wait.until(
                        ExpectedConditions.visibilityOfElementLocated(
                                By.id("email")
                        )
                );

        WebElement password =
                wait.until(
                        ExpectedConditions.visibilityOfElementLocated(
                                By.id("password")
                        )
                );

        email.sendKeys(VALID_EMAIL);
        password.sendKeys(VALID_PASSWORD);

        driver.findElement(
                By.cssSelector(".submit-button")
        ).click();

        wait.until(
                ExpectedConditions.urlContains(
                        "/reporter/"
                )
        );

        String currentUrl =
                driver.getCurrentUrl();

        assertTrue(
                currentUrl.contains("/reporter/"),
                "ผู้ใช้ควรเข้าสู่หน้าหลักของ Reporter หลัง Login สำเร็จ"
        );
    }

    /**
     * TC-UAT-02-02
     *
     * ผู้ใช้ไม่สามารถเข้าสู่ระบบด้วย Password ที่ไม่ถูกต้อง
     * และระบบต้องแจ้งเตือนผู้ใช้
     */
    @Test
    void TC_UAT_02_02_userCannotLoginWithWrongPassword() {

        WebElement email =
                wait.until(
                        ExpectedConditions.visibilityOfElementLocated(
                                By.id("email")
                        )
                );

        WebElement password =
                wait.until(
                        ExpectedConditions.visibilityOfElementLocated(
                                By.id("password")
                        )
                );

        email.sendKeys(VALID_EMAIL);
        password.sendKeys("wrong123");

        driver.findElement(
                By.cssSelector(".submit-button")
        ).click();

        WebElement error =
                wait.until(
                        ExpectedConditions.visibilityOfElementLocated(
                                By.cssSelector(".error-message")
                        )
                );

        assertTrue(
                error.isDisplayed(),
                "ระบบควรแสดงข้อความแจ้งเตือนเมื่อ Login ไม่สำเร็จ"
        );

        assertTrue(
                error.getText()
                        .contains("Invalid email or password"),
                "ระบบควรแจ้งว่าข้อมูล Login ไม่ถูกต้อง"
        );

        assertTrue(
                driver.getCurrentUrl().contains("/login"),
                "ผู้ใช้ควรยังอยู่ที่หน้า Login"
        );
    }

    /**
     * TC-UAT-02-03
     *
     * ผู้ใช้ไม่สามารถ Login ได้หากไม่กรอก Password
     */
    @Test
    void TC_UAT_02_03_userCannotLoginWithoutPassword() {

        WebElement email =
                wait.until(
                        ExpectedConditions.visibilityOfElementLocated(
                                By.id("email")
                        )
                );

        WebElement password =
                driver.findElement(
                        By.id("password")
                );

        email.sendKeys(VALID_EMAIL);

        driver.findElement(
                By.cssSelector(".submit-button")
        ).click();

        assertTrue(
                password.getAttribute("value").isEmpty(),
                "Password ต้องยังว่างอยู่"
        );

        assertTrue(
                driver.getCurrentUrl().contains("/login"),
                "ผู้ใช้ไม่ควรถูกเข้าสู่ระบบเมื่อไม่กรอก Password"
        );
    }

    /**
     * TC-UAT-02-04
     *
     * ผู้ใช้ไม่สามารถ Login ได้หากไม่กรอก Email
     */
    @Test
    void TC_UAT_02_04_userCannotLoginWithoutEmail() {

        WebElement email =
                wait.until(
                        ExpectedConditions.visibilityOfElementLocated(
                                By.id("email")
                        )
                );

        WebElement password =
                driver.findElement(
                        By.id("password")
                );

        password.sendKeys(VALID_PASSWORD);

        driver.findElement(
                By.cssSelector(".submit-button")
        ).click();

        assertTrue(
                email.getAttribute("value").isEmpty(),
                "Email ต้องยังว่างอยู่"
        );

        assertTrue(
                driver.getCurrentUrl().contains("/login"),
                "ผู้ใช้ไม่ควรถูกเข้าสู่ระบบเมื่อไม่กรอก Email"
        );
    }

    /**
     * TC-UAT-02-05
     *
     * ผู้ใช้ไม่สามารถ Login ด้วย Email ที่มีรูปแบบไม่ถูกต้อง
     */
    @Test
    void TC_UAT_02_05_userCannotLoginWithInvalidEmailFormat() {

        WebElement email =
                wait.until(
                        ExpectedConditions.visibilityOfElementLocated(
                                By.id("email")
                        )
                );

        WebElement password =
                driver.findElement(
                        By.id("password")
                );

        email.sendKeys("lucaenen01@");
        password.sendKeys(VALID_PASSWORD);

        driver.findElement(
                By.cssSelector(".submit-button")
        ).click();

        assertTrue(
                driver.getCurrentUrl().contains("/login"),
                "ผู้ใช้ไม่ควรถูกเข้าสู่ระบบด้วย Email ที่ไม่ถูกต้อง"
        );

        assertFalse(
                driver.getCurrentUrl().contains("/reporter/"),
                "ระบบต้องไม่พาผู้ใช้เข้าสู่หน้าหลักเมื่อ Email ไม่ถูกต้อง"
        );
    }
    /**
     * TC-UAT-02-06
     *
     * ผู้ใช้สามารถ Logout หลังจากเข้าสู่ระบบสำเร็จ
     * และระบบนำกลับไปยังหน้า Login
     */
    @Test
    void TC_UAT_02_06_userCanLogoutSuccessfully() {

        // Login ก่อน
        WebElement email =
                wait.until(
                        ExpectedConditions.visibilityOfElementLocated(
                                By.id("email")
                        )
                );

        WebElement password =
                driver.findElement(
                        By.id("password")
                );

        email.sendKeys(VALID_EMAIL);
        password.sendKeys(VALID_PASSWORD);

        driver.findElement(
                By.cssSelector(".submit-button")
        ).click();

        // รอให้เข้าสู่หน้าของ Reporter
        wait.until(
                ExpectedConditions.urlContains(
                        "/reporter/"
                )
        );

        // ตรวจสอบว่าปุ่ม Logout แสดงอยู่
        WebElement logoutButton =
                wait.until(
                        ExpectedConditions.elementToBeClickable(
                                By.cssSelector(".logout-btn")
                        )
                );

        assertTrue(
                logoutButton.isDisplayed(),
                "ผู้ใช้ที่ Login แล้วควรเห็นปุ่ม Logout"
        );

        // กด Logout
        logoutButton.click();

        // รอให้ระบบกลับไปหน้า Login
        wait.until(
                ExpectedConditions.urlContains(
                        "/login"
                )
        );

        // ตรวจสอบว่ากลับมาหน้า Login จริง
        assertTrue(
                driver.getCurrentUrl().contains("/login"),
                "หลัง Logout ระบบควรนำผู้ใช้กลับไปยังหน้า Login"
        );

        // ตรวจสอบว่าหน้า Login แสดงช่อง Email อีกครั้ง
        assertTrue(
                wait.until(
                        ExpectedConditions.visibilityOfElementLocated(
                                By.id("email")
                        )
                ).isDisplayed(),
                "หลัง Logout ผู้ใช้ควรเห็นหน้า Login"
        );
    }
}