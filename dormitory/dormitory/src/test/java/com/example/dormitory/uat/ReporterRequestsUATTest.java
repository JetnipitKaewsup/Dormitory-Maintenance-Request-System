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

class ReporterRequestsUATTest {

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

        driver = new ChromeDriver();

        driver.manage()
                .window()
                .maximize();

        wait = new WebDriverWait(
                driver,
                Duration.ofSeconds(10)
        );
    }


    @AfterEach
    void tearDown() {

        if (driver != null) {
            driver.quit();
        }
    }


    // =========================================================
    // Login
    // =========================================================

private void login() {

    driver.get(BASE_URL + "/login");

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
            By.cssSelector("button[type='submit']")
    ).click();

    // รอให้การ Login ดำเนินการเสร็จ
    wait.until(
            ExpectedConditions.or(
                    ExpectedConditions.urlContains("/reporter"),
                    ExpectedConditions.urlContains("/reporter/requests"),
                    ExpectedConditions.urlContains("/reporter/latest"),
                    ExpectedConditions.urlContains("/reporter/add")
            )
    );
}


    // =========================================================
    // TC-UAT-04-01
    // ผู้ใช้สามารถเข้าสู่หน้าประวัติการแจ้งซ่อม
    // =========================================================

    @Test
    void TC_UAT_04_01_userCanOpenRepairHistory() {

        login();

        driver.get(
                BASE_URL + "/reporter/requests"
        );

        wait.until(
                ExpectedConditions.urlContains(
                        "/reporter/requests"
                )
        );

        assertTrue(
                driver.getCurrentUrl()
                        .contains("/reporter/requests")
        );

        assertTrue(
                driver.getTitle()
                        .contains("ประวัติคำร้อง")
        );
    }


    // =========================================================
    // TC-UAT-04-02
    // ตรวจสอบรายการประวัติการแจ้งซ่อม
    // =========================================================

    @Test
    void TC_UAT_04_02_userCanViewRepairHistory() {

        login();

        driver.get(
                BASE_URL + "/reporter/requests"
        );

        wait.until(
                ExpectedConditions.presenceOfElementLocated(
                        By.tagName("body")
                )
        );

        String pageText =
                driver.findElement(
                        By.tagName("body")
                ).getText();

        boolean hasHistory =
                pageText.contains("ผู้แจ้ง")
                || pageText.contains("ประวัติคำร้อง")
                || pageText.contains("ยังไม่มีคำร้องแจ้งซ่อม");

        assertTrue(
                hasHistory,
                "ไม่พบข้อมูลหน้าประวัติการแจ้งซ่อม"
        );
    }


    // =========================================================
    // TC-UAT-04-03
    // ผู้ใช้สามารถเปิดรายละเอียดคำร้อง
    // =========================================================

    @Test
    void TC_UAT_04_03_userCanOpenRepairRequestDetail() {

        login();

        driver.get(
                BASE_URL + "/reporter/requests"
        );

        wait.until(
                ExpectedConditions.presenceOfElementLocated(
                        By.tagName("body")
                )
        );

        java.util.List<WebElement> detailLinks =
                driver.findElements(
                        By.cssSelector("a.btn-outline")
                );

        if (!detailLinks.isEmpty()) {

            detailLinks.get(0).click();

            wait.until(
                    ExpectedConditions.urlMatches(
                            ".*/reporter/requests/[a-fA-F0-9-]+"
                    )
            );

            assertTrue(
                    driver.getCurrentUrl()
                            .matches(
                                    ".*/reporter/requests/[a-fA-F0-9-]+"
                            )
            );

        } else {

            String pageText =
                    driver.findElement(
                            By.tagName("body")
                    ).getText();

            assertTrue(
                    pageText.contains(
                            "ยังไม่มีคำร้องแจ้งซ่อม"
                    ),
                    "ไม่พบรายการคำร้องและไม่พบข้อความแจ้งว่าไม่มีคำร้อง"
            );
        }
    }


    // =========================================================
    // TC-UAT-04-04
    // ตรวจสอบข้อมูลรายละเอียดคำร้อง
    // =========================================================

    @Test
    void TC_UAT_04_04_userCanViewRepairRequestDetail() {

        login();

        driver.get(
                BASE_URL + "/reporter/requests"
        );

        wait.until(
                ExpectedConditions.presenceOfElementLocated(
                        By.tagName("body")
                )
        );

        java.util.List<WebElement> detailLinks =
                driver.findElements(
                        By.cssSelector("a.btn-outline")
                );

        if (!detailLinks.isEmpty()) {

            detailLinks.get(0).click();

            wait.until(
                    ExpectedConditions.urlMatches(
                            ".*/reporter/requests/[a-fA-F0-9-]+"
                    )
            );

            String pageText =
                    driver.findElement(
                            By.tagName("body")
                    ).getText();

            assertTrue(
                    pageText.contains("ผู้แจ้ง"),
                    "ไม่พบข้อมูลผู้แจ้ง"
            );

            assertTrue(
                    pageText.contains("ห้องพัก"),
                    "ไม่พบข้อมูลห้องพัก"
            );

            assertTrue(
                    pageText.contains("ประเภทงาน"),
                    "ไม่พบข้อมูลประเภทงาน"
            );

            assertTrue(
                    pageText.contains("สถานะ"),
                    "ไม่พบข้อมูลสถานะ"
            );

        } else {

            String pageText =
                    driver.findElement(
                            By.tagName("body")
                    ).getText();

            assertTrue(
                    pageText.contains(
                            "ยังไม่มีคำร้องแจ้งซ่อม"
                    ),
                    "ไม่พบรายการคำร้อง"
            );
        }
    }


    // =========================================================
    // TC-UAT-04-05
    // ผู้ใช้สามารถกลับจากรายละเอียดมายังหน้าประวัติ
    // =========================================================

    @Test
    void TC_UAT_04_05_userCanReturnToRepairHistory() {

        login();

        driver.get(
                BASE_URL + "/reporter/requests"
        );

        wait.until(
                ExpectedConditions.presenceOfElementLocated(
                        By.tagName("body")
                )
        );

        java.util.List<WebElement> detailLinks =
                driver.findElements(
                        By.cssSelector("a.btn-outline")
                );

        if (!detailLinks.isEmpty()) {

            detailLinks.get(0).click();

            wait.until(
                    ExpectedConditions.urlMatches(
                            ".*/reporter/requests/[a-fA-F0-9-]+"
                    )
            );

            WebElement backLink =
                    wait.until(
                            ExpectedConditions.elementToBeClickable(
                                    By.cssSelector(
                                            "a[href='/reporter/requests']"
                                    )
                            )
                    );

            backLink.click();

            wait.until(
                    ExpectedConditions.urlToBe(
                            BASE_URL + "/reporter/requests"
                    )
            );

            assertTrue(
                    driver.getCurrentUrl()
                            .equals(
                                    BASE_URL + "/reporter/requests"
                            )
            );

        } else {

            assertTrue(
                    driver.getCurrentUrl()
                            .contains("/reporter/requests")
            );
        }
    }
}