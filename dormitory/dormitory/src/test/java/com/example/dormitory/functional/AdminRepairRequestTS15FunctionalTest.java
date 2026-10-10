
package com.example.dormitory.functional;

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
 * TS-15
 * ประสานงานกับผู้แจ้ง
 *
 * Functional Test สำหรับการตรวจสอบงานของ Admin
 * และการติดต่อผู้แจ้งก่อนยืนยันการดำเนินงาน
 *
 * TC-FT-15-01 เปิดหน้าตรวจสอบงาน
 * TC-FT-15-02 แสดงข้อมูลผู้แจ้งและช่องทางติดต่อ
 * TC-FT-15-03 ใช้ปุ่มคัดลอกเบอร์โทร
 * TC-FT-15-04 ตรวจสอบช่องหมายเหตุและสถานะปุ่มยืนยัน
 * TC-FT-15-05 กลับไปยังรายการตรวจงาน
 */
class AdminRepairRequestTS15FunctionalTest {

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

        driver.manage()
                .window()
                .maximize();

        loginAsAdmin();
    }

    @AfterEach
    void tearDown() {

        if (driver != null) {
            driver.quit();
        }
    }


    /**
     * เข้าสู่ระบบด้วยบัญชี Admin
     * ใช้รูปแบบเดียวกับ AdminRepairRequestReviewFunctionalTest
     */
    private void loginAsAdmin() {

        driver.get(BASE_URL + "/login");

        WebElement email =
                wait.until(
                        ExpectedConditions.visibilityOfElementLocated(
                                By.name("email")
                        )
                );

        WebElement password =
                wait.until(
                        ExpectedConditions.visibilityOfElementLocated(
                                By.name("password")
                        )
                );

        email.clear();
        password.clear();

        email.sendKeys(ADMIN_EMAIL);
        password.sendKeys(ADMIN_PASSWORD);

        WebElement loginButton =
                wait.until(
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
                    "Admin login ไม่สำเร็จ"
                            + "\nURL ปัจจุบัน: " + currentUrl
                            + "\nข้อความบนหน้าเว็บ: " + pageText,
                    e
            );
        }
    }

    /**
     * เปิดหน้ารายการตรวจงาน
     * และเลือกงานรายการแรกที่มีลิงก์ตรวจสอบ
     */
    private void openFirstInspectionPage() {

        driver.get(BASE_URL + "/admin/inspections");

        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(".work-page")
                )
        );

        List<WebElement> inspectLinks =
                wait.until(currentDriver -> {

                    List<WebElement> links =
                            currentDriver.findElements(
                                    By.cssSelector(
                                            "a[href*='/admin/requests/']"
                                                    + "[href$='/inspect']"
                                    )
                            );

                    return links.isEmpty() ? null : links;
                });

        assertFalse(
                inspectLinks.isEmpty(),
                "ต้องมีรายการงานที่สามารถเปิดหน้าตรวจสอบได้"
        );

        WebElement firstInspectLink = inspectLinks.get(0);

        wait.until(
                ExpectedConditions.elementToBeClickable(
                        firstInspectLink
                )
        ).click();

        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(".ins-page")
                )
        );
    }

    /**
     * TC-FT-15-01
     * ผู้ดูแลระบบเปิดหน้าตรวจสอบงานได้
     */
    @Test
    void TC_FT_15_01_adminCanOpenInspectionPage() {

        openFirstInspectionPage();

        assertTrue(
                driver.getCurrentUrl().matches(
                        ".*/admin/requests/[0-9a-fA-F-]+/inspect$"
                ),
                "URL ต้องเป็นหน้าตรวจสอบงานของคำร้องที่เลือก"
        );

        WebElement inspectionPage =
                driver.findElement(
                        By.cssSelector(".ins-page")
                );

        assertTrue(
                inspectionPage.isDisplayed(),
                "หน้าตรวจสอบงานต้องแสดงผล"
        );

        WebElement heading =
                driver.findElement(
                        By.cssSelector(".ins-head h1")
                );

        assertTrue(
                heading.getText().contains("ตรวจสอบงาน"),
                "ต้องแสดงหัวข้อหน้าตรวจสอบงาน"
        );
    }

    /**
     * TC-FT-15-02
     * ระบบแสดงข้อมูลผู้แจ้งและช่องทางติดต่อ
     */
    @Test
    void TC_FT_15_02_reporterContactInformationIsDisplayed() {

        openFirstInspectionPage();

        WebElement reporterSection =
                driver.findElement(
                        By.cssSelector(".ins-reporter")
                );

        assertTrue(
                reporterSection.isDisplayed(),
                "ต้องแสดงส่วนข้อมูลผู้แจ้ง"
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
                "ต้องแสดงปุ่มคัดลอกเบอร์โทร "
                        + "หรือข้อความว่าไม่พบเบอร์โทร"
        );

        if (!phoneButtons.isEmpty()) {

            WebElement phoneButton =
                    phoneButtons.get(0);

            assertTrue(
                    phoneButton.isDisplayed(),
                    "ปุ่มเบอร์โทรต้องแสดงผล"
            );

            String phone =
                    phoneButton.getAttribute("data-phone");

            assertNotNull(
                    phone,
                    "ต้องมีค่าเบอร์โทรใน data-phone"
            );

            assertFalse(
                    phone.trim().isEmpty(),
                    "เบอร์โทรต้องไม่เป็นค่าว่าง"
            );

        } else {

            assertTrue(
                    noPhoneMessages.get(0).isDisplayed(),
                    "ต้องแสดงข้อความเมื่อไม่มีเบอร์โทร"
            );
        }
    }

    /**
     * TC-FT-15-03
     * ผู้ดูแลระบบสามารถใช้ปุ่มคัดลอกเบอร์โทรได้
     *
     * หมายเหตุ:
     * กรณีไม่มีเบอร์โทร ให้ตรวจสอบข้อความแจ้งแทน
     * กรณีมีเบอร์โทร ต้องตรวจสอบสถานะหลังคลิกปุ่ม
     */
    @Test
    void TC_FT_15_03_adminCanUseCopyPhoneControl() {

        openFirstInspectionPage();

        List<WebElement> phoneButtons =
                driver.findElements(
                        By.cssSelector(
                                ".ins-reporter button.ins-call"
                        )
                );

        if (phoneButtons.isEmpty()) {

            WebElement noPhoneMessage =
                    driver.findElement(
                            By.cssSelector(
                                    ".ins-reporter .ins-nophone"
                            )
                    );

            assertTrue(
                    noPhoneMessage.isDisplayed(),
                    "ต้องแจ้งเมื่อไม่มีเบอร์โทรสำหรับติดต่อ"
            );

            return;
        }

        WebElement phoneButton =
                phoneButtons.get(0);

        assertTrue(
                phoneButton.isDisplayed(),
                "ปุ่มคัดลอกเบอร์โทรต้องแสดงผล"
        );

        assertTrue(
                phoneButton.isEnabled(),
                "ปุ่มคัดลอกเบอร์โทรต้องสามารถคลิกได้"
        );

        String phone =
                phoneButton.getAttribute("data-phone");

        assertNotNull(phone);

        assertFalse(
                phone.trim().isEmpty(),
                "เบอร์โทรต้องไม่เป็นค่าว่าง"
        );

        phoneButton.click();

        wait.until(currentDriver -> {

            WebElement currentButton =
                    currentDriver.findElement(
                            By.cssSelector(
                                    ".ins-reporter button.ins-call"
                            )
                    );

            String buttonText =
                    currentButton.getText();

            String buttonClass =
                    currentButton.getAttribute("class");

            return buttonText.contains("คัดลอกแล้ว")
                    || (
                            buttonClass != null
                                    && buttonClass.contains("is-copied")
                    );
        });

        WebElement updatedPhoneButton =
                driver.findElement(
                        By.cssSelector(
                                ".ins-reporter button.ins-call"
                        )
                );

        assertTrue(
                updatedPhoneButton.getText().contains("คัดลอกแล้ว")
                        || updatedPhoneButton
                                .getAttribute("class")
                                .contains("is-copied"),
                "ปุ่มต้องแสดงสถานะหลังคัดลอกเบอร์โทรสำเร็จ"
        );
    }

    /**
     * TC-FT-15-04
     * ระบบแสดงช่องหมายเหตุและสถานะปุ่มยืนยันถูกต้อง
     *
     * ทดสอบการกรอกหมายเหตุโดยไม่ส่งฟอร์มจริง
     * เพื่อไม่เปลี่ยนสถานะคำร้องในฐานข้อมูล
     */
    @Test
    void TC_FT_15_04_noteFormAndConfirmationStateAreCorrect() {

        openFirstInspectionPage();

        WebElement form =
                wait.until(
                        ExpectedConditions.visibilityOfElementLocated(
                                By.cssSelector("form.ins-form")
                        )
                );

        WebElement noteField =
                wait.until(
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
                "ฟอร์มยืนยันต้องใช้ POST"
        );

        String formAction =
                form.getAttribute("action");

        assertNotNull(
                formAction,
                "ฟอร์มต้องมี action"
        );

        assertTrue(
                formAction.matches(
                        ".*/admin/requests/[0-9a-fA-F-]+/inspect$"
                ),
                "ฟอร์มต้องส่งไปยัง endpoint ตรวจสอบงาน"
        );

        WebElement confirmButton =
                form.findElement(
                        By.cssSelector("button.ins-confirm")
                );

        WebElement hintElement =
                form.findElement(
                        By.cssSelector(".ins-hint")
                );

        String hint =
                hintElement.getText();

        assertTrue(
                hintElement.isDisplayed(),
                "ต้องแสดงข้อความแนะนำก่อนยืนยัน"
        );

        if (confirmButton.isEnabled()) {

            assertTrue(
                    hint.contains("โทรเช็คกับผู้แจ้ง"),
                    "เมื่อปุ่มยืนยันใช้งานได้ "
                            + "ต้องแสดงข้อความให้โทรตรวจสอบกับผู้แจ้ง"
            );

        } else {

            assertTrue(
                    hint.contains(
                            "ยืนยันได้เมื่อช่างแจ้งว่างานเสร็จแล้ว"
                    ),
                    "เมื่อช่างยังทำงานไม่เสร็จ "
                            + "ต้องแสดงเงื่อนไขก่อนยืนยัน"
            );
        }

        String testNote =
                "ทดสอบหมายเหตุสำหรับ TS-15";

        noteField.clear();
        noteField.sendKeys(testNote);

        assertEquals(
                testNote,
                noteField.getAttribute("value"),
                "ช่องหมายเหตุต้องรับข้อความที่กรอกได้"
        );

        // ไม่คลิกปุ่มยืนยัน เพื่อไม่เปลี่ยนสถานะคำร้องจริง
    }

    /**
     * TC-FT-15-05
     * ผู้ดูแลระบบสามารถกลับไปยังรายการตรวจงานได้
     */
    @Test
    void TC_FT_15_05_adminCanReturnToInspectionList() {

        openFirstInspectionPage();

        WebElement backLink =
                wait.until(
                        ExpectedConditions.elementToBeClickable(
                                By.cssSelector("a.ins-back")
                        )
                );

        String backUrl =
                backLink.getAttribute("href");

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

        WebElement inspectionList =
                wait.until(
                        ExpectedConditions.visibilityOfElementLocated(
                                By.cssSelector(".work-page")
                        )
                );

        assertTrue(
                inspectionList.isDisplayed(),
                "ต้องกลับมาแสดงรายการตรวจงานได้"
        );
    }
}
