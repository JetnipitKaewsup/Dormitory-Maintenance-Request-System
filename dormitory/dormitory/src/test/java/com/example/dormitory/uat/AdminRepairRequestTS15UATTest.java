
package com.example.dormitory.uat;

import java.time.Duration;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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

/**
 * TS-15: ประสานงานกับผู้แจ้ง
 *
 * User Acceptance Test (UAT)
 * ตรวจสอบว่าผู้ดูแลระบบสามารถใช้งานหน้าประสานงาน
 * กับผู้แจ้งได้ตามความต้องการของระบบ
 *
 * TC-UAT-15-01 เปิดหน้าตรวจสอบงาน
 * TC-UAT-15-02 ตรวจสอบข้อมูลติดต่อของผู้แจ้ง
 * TC-UAT-15-03 ใช้ปุ่มคัดลอกเบอร์โทร
 * TC-UAT-15-04 ตรวจสอบหมายเหตุและเงื่อนไขยืนยันงาน
 * TC-UAT-15-05 กลับไปยังรายการตรวจงาน
 */
class AdminRepairRequestTS15UATTest {

    private static final String BASE_URL =
            "http://localhost:8080";

    private static final String ADMIN_EMAIL =
            "lucaenen03@gmail.com";

    private static final String ADMIN_PASSWORD =
            "adminnaib";

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    void setUp() {

        ChromeOptions options = new ChromeOptions();

        driver = new ChromeDriver(options);

        wait = new WebDriverWait(
                driver,
                Duration.ofSeconds(60)
        );

        driver.manage().window().maximize();

        loginAsAdmin();
    }

    @AfterEach
    void tearDown() {

        if (driver != null) {
            driver.quit();
        }
    }

    /**
     * เข้าสู่ระบบในฐานะผู้ดูแลระบบ
     */
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
        password.clear();

        email.sendKeys(ADMIN_EMAIL);
        password.sendKeys(ADMIN_PASSWORD);

        WebElement loginButton = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector("button[type='submit']")
                )
        );

        loginButton.click();

        try {
            wait.until(
                    ExpectedConditions.urlToBe(
                            BASE_URL + "/admin/requests"
                    )
            );
        } catch (org.openqa.selenium.TimeoutException e) {

            String currentUrl = driver.getCurrentUrl();

            String pageText = driver.findElement(
                    By.tagName("body")
            ).getText();

            throw new AssertionError(
                    "ไม่สามารถเข้าสู่ระบบ Admin ได้"
                            + "\nURL ปัจจุบัน: " + currentUrl
                            + "\nข้อความบนหน้าเว็บ: " + pageText,
                    e
            );
        }
    }

    /**
     * เปิดรายการตรวจงานและเลือกรายการแรก
     * ที่มีลิงก์ไปยังหน้าประสานงานกับผู้แจ้ง
     */
    private void openFirstInspectionPage() {

        driver.get(BASE_URL + "/admin/inspections");

        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(".work-page")
                )
        );

        List<WebElement> inspectLinks = wait.until(
                currentDriver -> {
                    List<WebElement> links =
                            currentDriver.findElements(
                                    By.cssSelector(
                                            "a[href*='/admin/requests/']"
                                                    + "[href$='/inspect']"
                                    )
                            );

                    return links.isEmpty() ? null : links;
                }
        );

        assertFalse(
                inspectLinks.isEmpty(),
                "ต้องมีรายการงานสำหรับตรวจสอบ"
        );

        inspectLinks.get(0).click();

        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(".ins-page")
                )
        );
    }

    /**
     * TC-UAT-15-01
     * ผู้ดูแลระบบเปิดหน้าประสานงานกับผู้แจ้งได้
     */
    @Test
    void TC_UAT_15_01_adminCanOpenCoordinationPage() {

        openFirstInspectionPage();

        assertTrue(
                driver.getCurrentUrl().matches(
                        ".*/admin/requests/[0-9a-fA-F-]+/inspect$"
                ),
                "ต้องเปิดหน้าตรวจสอบงานของคำร้องที่เลือก"
        );

        WebElement heading = driver.findElement(
                By.cssSelector(".ins-head h1")
        );

        assertTrue(
                heading.isDisplayed(),
                "ต้องแสดงหัวข้อหน้าตรวจสอบงาน"
        );

        assertTrue(
                heading.getText().contains("ตรวจสอบงาน"),
                "หัวข้อต้องสื่อถึงการตรวจสอบงาน"
        );
    }

    /**
     * TC-UAT-15-02
     * ผู้ดูแลระบบตรวจสอบข้อมูลติดต่อผู้แจ้งได้
     */
    @Test
    void TC_UAT_15_02_adminCanViewReporterContact() {

        openFirstInspectionPage();

        WebElement reporterSection = driver.findElement(
                By.cssSelector(".ins-reporter")
        );

        assertTrue(
                reporterSection.isDisplayed(),
                "ต้องแสดงข้อมูลผู้แจ้ง"
        );

        List<WebElement> phoneButtons =
                reporterSection.findElements(
                        By.cssSelector("button.ins-call")
                );

        List<WebElement> noPhoneMessages =
                reporterSection.findElements(
                        By.cssSelector(".ins-nophone")
                );

        assertTrue(
                !phoneButtons.isEmpty()
                        || !noPhoneMessages.isEmpty(),
                "ต้องแสดงช่องทางติดต่อหรือแจ้งว่าไม่มีเบอร์โทร"
        );

        if (!phoneButtons.isEmpty()) {

            WebElement phoneButton = phoneButtons.get(0);

            assertTrue(
                    phoneButton.isDisplayed(),
                    "ต้องแสดงปุ่มเบอร์โทร"
            );

            String phone = phoneButton.getAttribute("data-phone");

            assertNotNull(
                    phone,
                    "ต้องมีข้อมูลเบอร์โทร"
            );

            assertFalse(
                    phone.trim().isEmpty(),
                    "เบอร์โทรต้องไม่เป็นค่าว่าง"
            );

        } else {

            assertTrue(
                    noPhoneMessages.get(0).isDisplayed(),
                    "ต้องแจ้งให้ทราบเมื่อไม่มีเบอร์โทร"
            );
        }
    }

    /**
     * TC-UAT-15-03
     * ผู้ดูแลระบบใช้ปุ่มคัดลอกเบอร์โทรได้
     */
    @Test
    void TC_UAT_15_03_adminCanCopyReporterPhone() {

        openFirstInspectionPage();

        List<WebElement> phoneButtons =
                driver.findElements(
                        By.cssSelector(
                                ".ins-reporter button.ins-call"
                        )
                );

        if (phoneButtons.isEmpty()) {

            WebElement noPhoneMessage = driver.findElement(
                    By.cssSelector(
                            ".ins-reporter .ins-nophone"
                    )
            );

            assertTrue(
                    noPhoneMessage.isDisplayed(),
                    "ต้องแสดงข้อความเมื่อไม่มีเบอร์โทร"
            );

            return;
        }

        WebElement phoneButton = phoneButtons.get(0);

        assertTrue(
                phoneButton.isDisplayed(),
                "ต้องแสดงปุ่มคัดลอกเบอร์โทร"
        );

        assertTrue(
                phoneButton.isEnabled(),
                "ปุ่มคัดลอกเบอร์โทรต้องพร้อมใช้งาน"
        );

        String phone = phoneButton.getAttribute("data-phone");

        assertNotNull(phone);

        assertFalse(
                phone.trim().isEmpty(),
                "ต้องมีเบอร์โทรสำหรับคัดลอก"
        );

        phoneButton.click();

        wait.until(currentDriver -> {
            WebElement currentButton =
                    currentDriver.findElement(
                            By.cssSelector(
                                    ".ins-reporter button.ins-call"
                            )
                    );

            String text = currentButton.getText();
            String cssClass = currentButton.getAttribute("class");

            return text.contains("คัดลอกแล้ว")
                    || (cssClass != null
                    && cssClass.contains("is-copied"));
        });

        WebElement updatedButton = driver.findElement(
                By.cssSelector(
                        ".ins-reporter button.ins-call"
                )
        );

        String updatedClass =
                updatedButton.getAttribute("class");

        assertTrue(
                updatedButton.getText().contains("คัดลอกแล้ว")
                        || (updatedClass != null
                        && updatedClass.contains("is-copied")),
                "ระบบต้องแสดงสถานะหลังคัดลอกเบอร์โทร"
        );
    }

    /**
     * TC-UAT-15-04
     * ผู้ดูแลระบบกรอกหมายเหตุและตรวจสอบเงื่อนไขยืนยันได้
     *
     * ไม่ส่งฟอร์มจริงเพื่อไม่เปลี่ยนสถานะงาน
     */
    @Test
    void TC_UAT_15_04_adminCanEnterNoteAndCheckConfirmation() {

        openFirstInspectionPage();

        WebElement form = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("form.ins-form")
                )
        );

        WebElement noteField = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(
                                "form.ins-form textarea[name='note']"
                        )
                )
        );

        assertTrue(
                noteField.isDisplayed(),
                "ต้องแสดงช่องหมายเหตุ"
        );

        assertEquals(
                "post",
                form.getAttribute("method").toLowerCase(),
                "ฟอร์มต้องใช้ POST"
        );

        String formAction = form.getAttribute("action");

        assertNotNull(
                formAction,
                "ฟอร์มต้องมีปลายทาง"
        );

        assertTrue(
                formAction.matches(
                        ".*/admin/requests/[0-9a-fA-F-]+/inspect$"
                ),
                "ฟอร์มต้องส่งไปยัง endpoint ตรวจสอบงาน"
        );

        WebElement confirmButton = form.findElement(
                By.cssSelector("button.ins-confirm")
        );

        WebElement hintElement = form.findElement(
                By.cssSelector(".ins-hint")
        );

        assertTrue(
                hintElement.isDisplayed(),
                "ต้องแสดงคำแนะนำก่อนยืนยันงาน"
        );

        String hint = hintElement.getText();

        if (confirmButton.isEnabled()) {

            assertTrue(
                    hint.contains("โทรเช็คกับผู้แจ้ง"),
                    "เมื่อยืนยันได้ ต้องแจ้งให้โทรตรวจสอบกับผู้แจ้ง"
            );

        } else {

            assertTrue(
                    hint.contains(
                            "ยืนยันได้เมื่อช่างแจ้งว่างานเสร็จแล้ว"
                    ),
                    "เมื่อยังยืนยันไม่ได้ ต้องแสดงเงื่อนไข"
            );
        }

        String testNote = "ทดสอบ UAT TS-15";

        noteField.clear();
        noteField.sendKeys(testNote);

        assertEquals(
                testNote,
                noteField.getAttribute("value"),
                "ผู้ดูแลระบบต้องกรอกหมายเหตุได้"
        );

        // ไม่ส่งฟอร์มจริง เพื่อรักษาสถานะคำร้องในฐานข้อมูล
    }

    /**
     * TC-UAT-15-05
     * ผู้ดูแลระบบกลับไปยังรายการตรวจงานได้
     */
    @Test
    void TC_UAT_15_05_adminCanReturnToInspectionList() {

        openFirstInspectionPage();

        WebElement backLink = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector("a.ins-back")
                )
        );

        String backUrl = backLink.getAttribute("href");

        assertNotNull(backUrl);

        assertTrue(
                backUrl.endsWith("/admin/inspections"),
                "ลิงก์กลับต้องชี้ไปยังรายการตรวจงาน"
        );

        backLink.click();

        wait.until(
                ExpectedConditions.urlToBe(
                        BASE_URL + "/admin/inspections"
                )
        );

        WebElement inspectionList = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(".work-page")
                )
        );

        assertTrue(
                inspectionList.isDisplayed(),
                "ต้องกลับไปยังรายการตรวจงานได้"
        );
    }
}
