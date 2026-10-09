package com.example.dormitory.functional;

import java.time.Duration;

import org.junit.jupiter.api.AfterEach;
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


class ReporterRequestsFunctionalTest {

    private WebDriver driver;
    private WebDriverWait wait;

    private final String BASE_URL =
            "http://localhost:8080";

    private final String EMAIL =
            "lucaenen01@gmail.com";

    private final String PASSWORD =
            "lucazaza";

    @BeforeEach
    void setUp() {

        ChromeOptions options =
                new ChromeOptions();

        options.addArguments("--start-maximized");

        driver = new ChromeDriver(options);

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

        driver.get(
                BASE_URL + "/login"
        );

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

        /*
        * รอให้ Login ดำเนินการเสร็จ
        */
        wait.until(
                driver -> {
                    String url =
                            driver.getCurrentUrl();

                    return !url.contains("/login");
                }
        );
    }

    // =========================================================
    // TC-FT-04-01
    // เปิดหน้าประวัติการแจ้งซ่อม
    // =========================================================

    @Test
    void TC_FT_04_01_openRepairRequestHistory_shouldDisplayPage() {

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
    // TC-FT-04-02
    // ตรวจสอบข้อมูลในหน้าประวัติการแจ้งซ่อม
    // =========================================================

    @Test
    void TC_FT_04_02_repairRequestHistory_shouldDisplayRequestSection() {

        login();

        driver.get(
                BASE_URL + "/reporter/requests"
        );

        wait.until(
                ExpectedConditions.urlContains(
                        "/reporter/requests"
                )
        );

        WebElement body =
                driver.findElement(
                        By.tagName("body")
                );

        String pageText =
                body.getText();

        assertTrue(
                pageText.contains("ผู้แจ้ง")
                        || pageText.contains("ประวัติคำร้อง")
                        || pageText.contains("ยังไม่มีคำร้องแจ้งซ่อม")
        );
    }

    // =========================================================
    // TC-FT-04-03
    // เปิดรายละเอียดคำร้อง
    // =========================================================

    @Test
    void TC_FT_04_03_openRequestDetail_shouldDisplayDetailPage() {

        login();

        driver.get(
                BASE_URL + "/reporter/requests"
        );

        wait.until(
                ExpectedConditions.urlContains(
                        "/reporter/requests"
                )
        );

        var detailLinks =
                driver.findElements(
                        By.cssSelector(
                                "a.btn-outline"
                        )
                );

        /*
         * ถ้ามีคำร้อง ให้กดปุ่มรายละเอียด
         */
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
        }
        /*
         * ถ้าไม่มีคำร้อง ถือว่าไม่สามารถทดสอบรายละเอียด
         * แต่ Test ไม่ควร fail เพียงเพราะฐานข้อมูลไม่มีข้อมูล
         */
        else {

            String pageText =
                    driver.findElement(
                            By.tagName("body")
                    ).getText();

            assertTrue(
                    pageText.contains(
                            "ยังไม่มีคำร้องแจ้งซ่อม"
                    )
            );
        }
    }

    // =========================================================
    // TC-FT-04-04
    // ตรวจสอบข้อมูลรายละเอียดคำร้อง
    // =========================================================

    @Test
    void TC_FT_04_04_requestDetail_shouldDisplayRequestInformation() {

        login();

        driver.get(
                BASE_URL + "/reporter/requests"
        );

        wait.until(
                ExpectedConditions.urlContains(
                        "/reporter/requests"
                )
        );

        var detailLinks =
                driver.findElements(
                        By.cssSelector(
                                "a.btn-outline"
                        )
                );

        /*
         * กรณีมีคำร้อง
         */
        if (!detailLinks.isEmpty()) {

            detailLinks.get(0).click();

            wait.until(
                    ExpectedConditions.urlMatches(
                            ".*/reporter/requests/[a-fA-F0-9-]+"
                    )
            );

            WebElement body =
                    driver.findElement(
                            By.tagName("body")
                    );

            String pageText =
                    body.getText();

            assertTrue(
                    pageText.contains("ผู้แจ้ง")
            );

            assertTrue(
                    pageText.contains("ห้องพัก")
            );

            assertTrue(
                    pageText.contains("ประเภทงาน")
            );

            assertTrue(
                    pageText.contains("สถานะ")
            );
        }
        /*
         * กรณีไม่มีคำร้อง
         */
        else {

            String pageText =
                    driver.findElement(
                            By.tagName("body")
                    ).getText();

            assertTrue(
                    pageText.contains(
                            "ยังไม่มีคำร้องแจ้งซ่อม"
                    )
            );
        }
    }

    // =========================================================
    // TC-FT-04-05
    // กลับจากรายละเอียดไปหน้าประวัติ
    // =========================================================

    @Test
    void TC_FT_04_05_backToHistory_shouldReturnToRequestsPage() {

        login();

        driver.get(
                BASE_URL + "/reporter/requests"
        );

        wait.until(
                ExpectedConditions.urlContains(
                        "/reporter/requests"
                )
        );

        var detailLinks =
                driver.findElements(
                        By.cssSelector(
                                "a.btn-outline"
                        )
                );

        /*
         * ถ้ามีคำร้อง
         */
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
                    ExpectedConditions.urlContains(
                            "/reporter/requests"
                    )
            );

            assertTrue(
                    driver.getCurrentUrl()
                            .endsWith(
                                    "/reporter/requests"
                            )
            );
        }
        /*
         * ถ้าไม่มีคำร้อง หน้าประวัติถือว่าเปิดสำเร็จอยู่แล้ว
         */
        else {

            assertTrue(
                    driver.getCurrentUrl()
                            .contains(
                                    "/reporter/requests"
                            )
            );
        }
    }
}