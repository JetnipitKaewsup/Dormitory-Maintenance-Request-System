package com.example.dormitory.functional;

import java.time.Duration;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class RegisterUATTest {

    private WebDriver driver;
    private WebDriverWait wait;

    private static final String BASE_URL = "http://localhost:8080";

    @BeforeEach
    void setUp() {

        driver = new ChromeDriver();

        driver.manage().window().maximize();

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(3));

        wait = new WebDriverWait(
                driver,
                Duration.ofSeconds(15)
        );

        driver.get(BASE_URL + "/register");

        wait.until(
                ExpectedConditions.presenceOfElementLocated(
                        By.cssSelector("form.register-form")
                )
        );
    }

    @AfterEach
    void tearDown() {

        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    void TC_UAT_01_01_reporterRegistration_shouldSuccess() {

        /*
         * ============================================================
         * TC_UAT_01_01
         * ============================================================
         * Test Scenario:
         * สมัครสมาชิก Reporter
         *
         * Expected Result:
         * ผู้ใช้สามารถสมัครสมาชิกได้สำเร็จ
         * และระบบเปลี่ยนออกจากหน้า /register
         * ============================================================
         */

        String timestamp = String.valueOf(
                System.currentTimeMillis()
        );

        String firstName = "UAT";
        String lastName = "Reporter";
        String username = "uatuser_" + timestamp;
        String phoneNo = "0812345678";
        String email = "uat_" + timestamp + "@example.com";
        String roomNumber = "3218";
        String password = "Test@12345";

        /*
         * ============================================================
         * 1. กรอก First Name
         * ============================================================
         */

        WebElement firstNameInput = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.id("firstName")
                )
        );

        firstNameInput.clear();
        firstNameInput.sendKeys(firstName);

        /*
         * ============================================================
         * 2. กรอก Last Name
         * ============================================================
         */

        WebElement lastNameInput = driver.findElement(
                By.id("lastName")
        );

        lastNameInput.clear();
        lastNameInput.sendKeys(lastName);

        /*
         * ============================================================
         * 3. กรอก Username
         * ============================================================
         */

        WebElement usernameInput = driver.findElement(
                By.id("username")
        );

        usernameInput.clear();
        usernameInput.sendKeys(username);

        /*
         * ============================================================
         * 4. กรอก Phone Number
         * ============================================================
         */

        WebElement phoneInput = driver.findElement(
                By.id("phoneNo")
        );

        phoneInput.clear();
        phoneInput.sendKeys(phoneNo);

        /*
         * ============================================================
         * 5. กรอก Email
         * ============================================================
         */

        WebElement emailInput = driver.findElement(
                By.id("email")
        );

        emailInput.clear();
        emailInput.sendKeys(email);

        /*
         * ============================================================
         * 6. กรอก Room Number
         * ============================================================
         */

        WebElement roomInput = driver.findElement(
                By.id("roomNumber")
        );

        roomInput.clear();
        roomInput.sendKeys(roomNumber);

        /*
         * ============================================================
         * 7. กรอก Password
         * ============================================================
         */

        WebElement passwordInput = driver.findElement(
                By.id("password")
        );

        passwordInput.clear();
        passwordInput.sendKeys(password);

        /*
         * ============================================================
         * 8. กรอก Confirm Password
         * ============================================================
         */

        WebElement confirmPasswordInput = driver.findElement(
                By.id("confirmPassword")
        );

        confirmPasswordInput.clear();
        confirmPasswordInput.sendKeys(password);

        /*
         * ============================================================
         * 9. ยอมรับ Terms & Privacy Policy
         * ============================================================
         */

        WebElement termsCheckbox = driver.findElement(
                By.cssSelector(
                        ".terms-checkbox input[type='checkbox']"
                )
        );

        if (!termsCheckbox.isSelected()) {
            termsCheckbox.click();
        }

        /*
         * ============================================================
         * 10. Debug ข้อมูลที่กรอก
         * ============================================================
         */

        System.out.println();
        System.out.println("==========================================");
        System.out.println("REGISTER INPUT DEBUG");
        System.out.println("==========================================");

        System.out.println(
                "First Name = "
                        + firstNameInput.getAttribute("value")
        );

        System.out.println(
                "Last Name = "
                        + lastNameInput.getAttribute("value")
        );

        System.out.println(
                "Username = "
                        + usernameInput.getAttribute("value")
        );

        System.out.println(
                "Phone No = "
                        + phoneInput.getAttribute("value")
        );

        System.out.println(
                "Email = "
                        + emailInput.getAttribute("value")
        );

        System.out.println(
                "Room No = "
                        + roomInput.getAttribute("value")
        );

        System.out.println(
                "Password Filled = "
                        + !passwordInput
                                .getAttribute("value")
                                .isEmpty()
        );

        System.out.println(
                "Confirm Password Filled = "
                        + !confirmPasswordInput
                                .getAttribute("value")
                                .isEmpty()
        );

        System.out.println(
                "Terms Checked = "
                        + termsCheckbox.isSelected()
        );

        System.out.println("==========================================");

        /*
         * ============================================================
         * 11. ตรวจ HTML5 validation ก่อน Submit
         * ============================================================
         */

        JavascriptExecutor js =
                (JavascriptExecutor) driver;

        String validationResult =
                (String) js.executeScript("""
                    const form =
                        document.querySelector('form.register-form');

                    if (!form) {
                        return 'FORM NOT FOUND';
                    }

                    const fields = [
                        'firstName',
                        'lastName',
                        'username',
                        'phoneNo',
                        'email',
                        'roomNumber',
                        'password',
                        'confirmPassword'
                    ];

                    let output = [];

                    fields.forEach(id => {

                        const element =
                            document.getElementById(id);

                        if (!element) {
                            output.push(
                                id + ' => NOT FOUND'
                            );
                            return;
                        }

                        output.push(
                            id
                            + ' | value=['
                            + element.value
                            + ']'
                            + ' | valid='
                            + element.validity.valid
                            + ' | message=['
                            + element.validationMessage
                            + ']'
                        );
                    });

                    const terms =
                        form.querySelector(
                            '.terms-checkbox input[type="checkbox"]'
                        );

                    output.push(
                        'terms'
                        + ' | checked='
                        + (terms
                            ? terms.checked
                            : false)
                        + ' | valid='
                        + (terms
                            ? terms.validity.valid
                            : false)
                    );

                    output.push(
                        'FORM VALID='
                        + form.checkValidity()
                    );

                    return output.join('\\n');
                """);

        System.out.println();
        System.out.println("==========================================");
        System.out.println("REGISTER HTML5 VALIDATION");
        System.out.println("==========================================");
        System.out.println(validationResult);
        System.out.println("==========================================");

        /*
         * ============================================================
         * 12. ตรวจว่า Form valid ก่อน Submit
         * ============================================================
         */

        Boolean formValid = (Boolean) js.executeScript("""
            const form =
                document.querySelector('form.register-form');

            return form ? form.checkValidity() : false;
        """);

        assertTrue(
                formValid,
                "Registration form is invalid before submit."
        );

        /*
         * ============================================================
         * 13. ตรวจ Form action
         * ============================================================
         */

        WebElement form = driver.findElement(
                By.cssSelector("form.register-form")
        );

        System.out.println();
        System.out.println("==========================================");
        System.out.println("FORM SUBMIT DEBUG");
        System.out.println("==========================================");

        System.out.println(
                "Form action = "
                        + form.getAttribute("action")
        );

        System.out.println(
                "Form method = "
                        + form.getAttribute("method")
        );

        System.out.println(
                "Form valid = "
                        + formValid
        );

        System.out.println("==========================================");

        /*
         * ============================================================
         * 14. Submit Registration
         * ============================================================
         */

        WebElement submitButton = driver.findElement(
                By.cssSelector(
                        "button[type='submit'].submit-button"
                )
        );

        wait.until(
                ExpectedConditions.elementToBeClickable(
                        submitButton
                )
        );

        submitButton.click();

        /*
         * ============================================================
         * 15. รอให้ Server ประมวลผล
         * ============================================================
         */

        wait.until(driver ->
                !driver.getCurrentUrl()
                        .equals(BASE_URL + "/register")
                || !driver.findElements(
                        By.cssSelector("form.register-form")
                ).isEmpty()
        );

        /*
         * ============================================================
         * 16. Debug หลัง Submit
         * ============================================================
         */

        System.out.println();
        System.out.println("==========================================");
        System.out.println("UAT REGISTER DEBUG AFTER SUBMIT");
        System.out.println("==========================================");

        System.out.println(
                "Current URL = "
                        + driver.getCurrentUrl()
        );

        System.out.println(
                "Page Title = "
                        + driver.getTitle()
        );

        System.out.println(
                "Page Source Length = "
                        + driver.getPageSource().length()
        );

        System.out.println("==========================================");

        /*
         * ============================================================
         * 17. ตรวจ Error Message จากหน้า Register
         * ============================================================
         */

        String bodyText = driver
                .findElement(By.tagName("body"))
                .getText();

        System.out.println();
        System.out.println("==========================================");
        System.out.println("PAGE BODY AFTER REGISTER");
        System.out.println("==========================================");
        System.out.println(bodyText);
        System.out.println("==========================================");

        /*
         * ============================================================
         * 18. ตรวจผลการสมัคร
         * ============================================================
         *
         * หากสมัครสำเร็จ ระบบควรออกจาก /register
         *
         * เช่น:
         * /login
         * /auth/verified
         * /
         * หรือหน้าอื่นตาม Controller จริง
         *
         * ถ้ายังอยู่ /register ให้ถือว่า registration ไม่สำเร็จ
         * ============================================================
         */

        String currentUrl = driver.getCurrentUrl();

        boolean registrationSuccess =
                !currentUrl.equals(BASE_URL + "/register");

        /*
         * ============================================================
         * 19. หากยังอยู่หน้า Register ให้แสดงข้อมูลเพิ่มเติม
         * ============================================================
         */

        if (!registrationSuccess) {

            System.out.println();
            System.out.println(
                    "=========================================="
            );

            System.out.println(
                    "REGISTRATION DID NOT REDIRECT"
            );

            System.out.println(
                    "The server returned the registration page."
            );

            System.out.println(
                    "This usually means backend validation "
                    + "or registration processing failed."
            );

            System.out.println(
                    "=========================================="
            );

            /*
             * ตรวจ input หลัง server ส่งหน้าใหม่กลับมา
             */

            try {

                WebElement newForm = driver.findElement(
                        By.cssSelector("form.register-form")
                );

                Boolean newFormValid =
                        (Boolean) js.executeScript("""
                            const form =
                                document.querySelector(
                                    'form.register-form'
                                );

                            return form
                                ? form.checkValidity()
                                : false;
                        """);

                System.out.println(
                        "New Form Valid = "
                                + newFormValid
                );

                System.out.println(
                        "New Form Action = "
                                + newForm.getAttribute("action")
                );

            } catch (Exception e) {

                System.out.println(
                        "Unable to inspect returned register form: "
                                + e.getMessage()
                );
            }
        }

        /*
         * ============================================================
         * 20. Final Assertion
         * ============================================================
         */

        assertTrue(
                registrationSuccess,
                "Reporter registration should be successful. "
                        + "The application remained on /register. "
                        + "Check the server log and page body above "
                        + "for the actual registration error."
        );
    }
}
