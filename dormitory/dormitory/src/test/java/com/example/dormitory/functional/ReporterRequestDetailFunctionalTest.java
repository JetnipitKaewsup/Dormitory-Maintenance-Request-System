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
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

class ReporterRequestDetailFunctionalTest {

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
                Duration.ofSeconds(10)
        );
    }


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

        wait.until(
                ExpectedConditions.not(
                        ExpectedConditions.urlContains("/login")
                )
        );
    }


    @Test
    void TC_FT_05_01_openRequestDetail() {

        login();

        driver.get(
                BASE_URL + "/reporter/requests"
        );

        wait.until(
                ExpectedConditions.or(
                        ExpectedConditions.presenceOfElementLocated(
                                By.cssSelector("a.btn-outline")
                        ),
                        ExpectedConditions.presenceOfElementLocated(
                                By.xpath("//*[contains(text(),'ยังไม่มีคำร้อง')]")
                        ),
                        ExpectedConditions.presenceOfElementLocated(
                                By.xpath("//*[contains(text(),'ไม่มีคำร้อง')]")
                        )
                )
        );

        var detailLinks =
                driver.findElements(
                        By.cssSelector("a.btn-outline")
                );

        if (!detailLinks.isEmpty()) {

            detailLinks.get(0).click();

            wait.until(
                    ExpectedConditions.urlContains(
                            "/reporter/requests/"
                    )
            );

            assertTrue(
                    driver.getCurrentUrl()
                            .contains("/reporter/requests/")
            );
        } else {

            assertTrue(
                    driver.getPageSource().contains("ไม่มีคำร้อง")
                    || driver.getPageSource().contains("ยังไม่มีคำร้อง")
            );
        }
    }


    @Test
    void TC_FT_05_02_checkRequestDetailInformation() {

        login();

        driver.get(
                BASE_URL + "/reporter/requests"
        );

        wait.until(
                ExpectedConditions.presenceOfElementLocated(
                        By.tagName("body")
                )
        );

        var detailLinks =
                driver.findElements(
                        By.cssSelector("a.btn-outline")
                );

        if (!detailLinks.isEmpty()) {

            detailLinks.get(0).click();

            wait.until(
                    ExpectedConditions.urlContains(
                            "/reporter/requests/"
                    )
            );

            String pageSource =
                    driver.getPageSource();

            assertTrue(
                    pageSource.contains("ผู้แจ้ง")
            );

            assertTrue(
                    pageSource.contains("ห้องพัก")
            );

            assertTrue(
                    pageSource.contains("ประเภทงาน")
            );

            assertTrue(
                    pageSource.contains("สถานะ")
            );
        }
    }


    @Test
    void TC_FT_05_03_checkRequestStatus() {

        login();

        driver.get(
                BASE_URL + "/reporter/requests"
        );

        wait.until(
                ExpectedConditions.presenceOfElementLocated(
                        By.tagName("body")
                )
        );

        var detailLinks =
                driver.findElements(
                        By.cssSelector("a.btn-outline")
                );

        if (!detailLinks.isEmpty()) {

            detailLinks.get(0).click();

            wait.until(
                    ExpectedConditions.urlContains(
                            "/reporter/requests/"
                    )
            );

            WebElement statusBadge =
                    wait.until(
                            ExpectedConditions.presenceOfElementLocated(
                                    By.cssSelector(".status-badge")
                            )
                    );

            assertTrue(
                    statusBadge.isDisplayed()
            );

            assertTrue(
                    !statusBadge.getText()
                            .trim()
                            .isEmpty()
            );
        }
    }


    @Test
    void TC_FT_05_04_checkRequestHistory() {

        login();

        driver.get(
                BASE_URL + "/reporter/requests"
        );

        wait.until(
                ExpectedConditions.presenceOfElementLocated(
                        By.tagName("body")
                )
        );

        var detailLinks =
                driver.findElements(
                        By.cssSelector("a.btn-outline")
                );

        if (!detailLinks.isEmpty()) {

            detailLinks.get(0).click();

            wait.until(
                    ExpectedConditions.urlContains(
                            "/reporter/requests/"
                    )
            );

            String pageSource =
                    driver.getPageSource();

            assertTrue(
                    pageSource.contains("ส่งคำร้อง")
                    || pageSource.contains("Admin อนุมัติ")
                    || pageSource.contains("ช่างดำเนินการซ่อม")
                    || pageSource.contains("ตรวจสอบและเสร็จสิ้น")
                    || pageSource.contains("คำร้องถูกปฏิเสธ")
            );
        }
    }


    @Test
    void TC_FT_05_05_backToRequestHistory() {

        login();

        driver.get(
                BASE_URL + "/reporter/requests"
        );

        wait.until(
                ExpectedConditions.presenceOfElementLocated(
                        By.tagName("body")
                )
        );

        var detailLinks =
                driver.findElements(
                        By.cssSelector("a.btn-outline")
                );

        if (!detailLinks.isEmpty()) {

            detailLinks.get(0).click();

            wait.until(
                    ExpectedConditions.urlContains(
                            "/reporter/requests/"
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
                            .contains("/reporter/requests")
            );
        }
    }


    @AfterEach
    void tearDown() {

        if (driver != null) {
            driver.quit();
        }
    }
}