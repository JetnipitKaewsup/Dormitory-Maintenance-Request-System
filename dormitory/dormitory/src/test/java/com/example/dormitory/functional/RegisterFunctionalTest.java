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
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.example.dormitory.domain.entity.Resident;
import com.example.dormitory.domain.entity.Room;
import com.example.dormitory.repository.AdminResidentRepository;
import com.example.dormitory.repository.RoomRepository;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class RegisterFunctionalTest {

    private static final String BASE_URL =
            "http://localhost:8080";

    private static final String TEST_PASSWORD =
            "Password123!";

    @Autowired
    private AdminResidentRepository residentRepository;

    @Autowired
    private RoomRepository roomRepository;

    private WebDriver driver;

    private WebDriverWait wait;

    /*
     * Resident ที่สร้างขึ้นสำหรับ TC_FT_01_02
     */
    private Resident testResident;

    // =========================================================
    // SETUP
    // =========================================================

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

        driver.get(
                BASE_URL + "/register"
        );

        wait.until(
                ExpectedConditions.presenceOfElementLocated(
                        By.cssSelector(
                                "form.register-form"
                        )
                )
        );
    }

    // =========================================================
    // TEARDOWN
    // =========================================================

    @AfterEach
    void tearDown() {

        if (driver != null) {
            driver.quit();
        }
    }

    // =========================================================
    // FT-01-01
    // ตรวจสอบหน้า Register
    // =========================================================

    @Test
    void TC_FT_01_01_registerPage_shouldDisplayCorrectly() {

        WebElement form = wait.until(
                ExpectedConditions.presenceOfElementLocated(
                        By.cssSelector(
                                "form.register-form"
                        )
                )
        );

        assertTrue(
                form.isEnabled(),
                "Register form should be enabled"
        );

        assertNotNull(
                driver.findElement(
                        By.id("firstName")
                )
        );

        assertNotNull(
                driver.findElement(
                        By.id("lastName")
                )
        );

        assertNotNull(
                driver.findElement(
                        By.id("username")
                )
        );

        assertNotNull(
                driver.findElement(
                        By.id("phoneNo")
                )
        );

        assertNotNull(
                driver.findElement(
                        By.id("email")
                )
        );

        assertNotNull(
                driver.findElement(
                        By.id("roomNumber")
                )
        );

        assertNotNull(
                driver.findElement(
                        By.id("password")
                )
        );

        assertNotNull(
                driver.findElement(
                        By.id("confirmPassword")
                )
        );

        assertNotNull(
                driver.findElement(
                        By.cssSelector(
                                ".terms-checkbox input[type='checkbox']"
                        )
                )
        );

        assertNotNull(
                driver.findElement(
                        By.cssSelector(
                                "button.submit-button"
                        )
                )
        );
    }

    // =========================================================
    // FT-01-02
    // สมัครสมาชิกด้วยข้อมูลถูกต้อง
    //
    // สำคัญ:
    // สร้าง Resident ใหม่ก่อนสมัครทุกครั้ง
    // =========================================================

    @Test
    void TC_FT_01_02_registerWithValidData_shouldSuccess() {

        /*
         * =====================================================
         * STEP 1: สร้าง Resident ใหม่
         * =====================================================
         */
        testResident = createNewResident();

        String firstName =
                testResident.getFirstName();

        String lastName =
                testResident.getLastName();

        String phone =
                testResident.getPhoneNo();

        String roomNumber =
                String.valueOf(
                        testResident.getRoom().getRoomNo()
                );

        /*
         * Username และ Email ต้อง unique เช่นกัน
         */
        String username =
                uniqueUsername();

        String email =
                uniqueEmail();

        System.out.println();
        System.out.println(
                "=========================================="
        );

        System.out.println(
                "NEW RESIDENT CREATED FOR REGISTRATION TEST"
        );

        System.out.println(
                "=========================================="
        );

        System.out.println(
                "Resident ID = "
                        + testResident.getResidentId()
        );

        System.out.println(
                "First Name = "
                        + firstName
        );

        System.out.println(
                "Last Name = "
                        + lastName
        );

        System.out.println(
                "Phone = "
                        + phone
        );

        System.out.println(
                "Room = "
                        + roomNumber
        );

        System.out.println(
                "Username = "
                        + username
        );

        System.out.println(
                "Email = "
                        + email
        );

        System.out.println(
                "=========================================="
        );

        /*
         * =====================================================
         * STEP 2: กรอกข้อมูลของ Resident ที่เพิ่งสร้าง
         * =====================================================
         */
        fillRegisterForm(
                firstName,
                lastName,
                username,
                email,
                roomNumber,
                TEST_PASSWORD,
                TEST_PASSWORD,
                phone
        );

        acceptTerms();

        /*
         * =====================================================
         * STEP 3: ตรวจสอบ HTML5 validation
         * =====================================================
         */
        assertTrue(
                isRegisterFormValid(),
                "Valid registration data should pass HTML validation"
        );

        /*
         * =====================================================
         * STEP 4: Submit
         * =====================================================
         */
        submitRegisterForm();

        /*
         * =====================================================
         * STEP 5: ตรวจสอบ Registration Success
         * =====================================================
         */
        boolean success =
                waitForRegisterSuccess();

        if (!success) {

            printRegisterDebug(
                    username,
                    email
            );
        }

        assertTrue(
                success,
                "Registration should succeed"
        );
    }

    // =========================================================
    // FT-01-03
    // Required Fields
    // =========================================================

    @Test
    void TC_FT_01_03_requiredFields_shouldBeValidated() {

        assertFalse(
                isRegisterFormValid(),
                "Empty registration form should be invalid"
        );
    }

    // =========================================================
    // FT-01-04
    // Username ว่าง
    // =========================================================

    @Test
    void TC_FT_01_04_emptyUsername_shouldBeInvalid() {

        fillRegisterForm(
                "ทดสอบ",
                "สมัครสมาชิก",
                "",
                uniqueEmail(),
                "1201",
                TEST_PASSWORD,
                TEST_PASSWORD,
                "0812345678"
        );

        acceptTerms();

        assertFalse(
                isRegisterFormValid(),
                "Empty username should make the form invalid"
        );
    }

    // =========================================================
    // FT-01-05
    // Email ว่าง
    // =========================================================

    @Test
    void TC_FT_01_05_emptyEmail_shouldBeInvalid() {

        fillRegisterForm(
                "ทดสอบ",
                "สมัครสมาชิก",
                uniqueUsername(),
                "",
                "1201",
                TEST_PASSWORD,
                TEST_PASSWORD,
                "0812345678"
        );

        acceptTerms();

        assertFalse(
                isRegisterFormValid(),
                "Empty email should make the form invalid"
        );
    }

    // =========================================================
    // FT-01-06
    // Password ไม่ตรงกัน
    // =========================================================

    @Test
    void TC_FT_01_06_passwordMismatch_shouldBeRejected() {

        fillRegisterForm(
                "ทดสอบ",
                "สมัครสมาชิก",
                uniqueUsername(),
                uniqueEmail(),
                "1201",
                TEST_PASSWORD,
                "DifferentPassword123!",
                "0812345678"
        );

        acceptTerms();

        submitRegisterForm();

        sleep(1000);

        assertFalse(
                isRegisterSuccessPage(),
                "Password mismatch should not register successfully"
        );
    }

    // =========================================================
    // FT-01-07
    // Resident ไม่มีอยู่ในระบบ
    // =========================================================

    @Test
    void TC_FT_01_07_nonExistingResident_shouldBeRejected() {

        fillRegisterForm(
                "ไม่มีจริง",
                "Resident",
                uniqueUsername(),
                uniqueEmail(),
                "9999",
                TEST_PASSWORD,
                TEST_PASSWORD,
                "0899999999"
        );

        acceptTerms();

        submitRegisterForm();

        sleep(1500);

        assertFalse(
                isRegisterSuccessPage(),
                "Non-existing resident should not register successfully"
        );
    }

    // =========================================================
    // FT-01-08
    // ไม่ยอมรับ Terms
    // =========================================================

    @Test
    void TC_FT_01_08_termsNotAccepted_shouldBeInvalid() {

        fillRegisterForm(
                "ทดสอบ",
                "สมัครสมาชิก",
                uniqueUsername(),
                uniqueEmail(),
                "1201",
                TEST_PASSWORD,
                TEST_PASSWORD,
                "0812345678"
        );

        assertFalse(
                isRegisterFormValid(),
                "Form should be invalid when terms are not accepted"
        );
    }

    // =========================================================
    // FT-01-09
    // Password Toggle
    // =========================================================

    @Test
    void TC_FT_01_09_passwordToggle_shouldChangeInputType() {

        WebElement password =
                wait.until(
                        ExpectedConditions.presenceOfElementLocated(
                                By.id("password")
                        )
                );

        assertEquals(
                "password",
                password.getAttribute("type")
        );

        WebElement toggle =
                driver.findElement(
                        By.cssSelector(
                                "button.password-toggle"
                        )
                );

        javascriptClick(toggle);

        assertEquals(
                "text",
                password.getAttribute("type")
        );
    }

    // =========================================================
    // FT-01-10
    // Confirm Password Toggle
    // =========================================================

    @Test
    void TC_FT_01_10_confirmPasswordToggle_shouldChangeInputType() {

        WebElement confirmPassword =
                wait.until(
                        ExpectedConditions.presenceOfElementLocated(
                                By.id("confirmPassword")
                        )
                );

        assertEquals(
                "password",
                confirmPassword.getAttribute("type")
        );

        List<WebElement> toggles =
                driver.findElements(
                        By.cssSelector(
                                "button.password-toggle"
                        )
                );

        assertTrue(
                toggles.size() >= 2,
                "There should be two password toggle buttons"
        );

        javascriptClick(
                toggles.get(1)
        );

        assertEquals(
                "text",
                confirmPassword.getAttribute("type")
        );
    }

    // =========================================================
    // FT-01-11
    // Login Link
    // =========================================================

    @Test
    void TC_FT_01_11_loginLink_shouldNavigateToLogin() {

        WebElement loginLink =
                wait.until(
                        ExpectedConditions.presenceOfElementLocated(
                                By.cssSelector(
                                        "a.login-trigger"
                                )
                        )
                );

        javascriptClick(
                loginLink
        );

        wait.until(
                ExpectedConditions.urlContains(
                        "/login"
                )
        );

        assertTrue(
                driver.getCurrentUrl()
                        .contains("/login"),
                "Login link should navigate to /login"
        );
    }

    // =========================================================
    // HELPER: Create New Resident
    // =========================================================

    private Resident createNewResident() {

        /*
         * หา Room ที่มีอยู่จริง
         *
         * เรียง room_no จากน้อยไปมาก
         */
        List<Room> rooms =
                roomRepository.findAll(
                        Sort.by(
                                Sort.Direction.ASC,
                                "roomNo"
                        )
                );

        assertFalse(
                rooms.isEmpty(),
                "At least one room must exist in database"
        );

        /*
         * ใช้ Room ที่มีอยู่จริง
         */
        Room room =
                rooms.get(0);

        /*
         * ใช้ timestamp เพื่อให้ข้อมูล Resident
         * แตกต่างกันทุกครั้งที่รัน test
         */
        long timestamp =
                System.currentTimeMillis();

        String firstName =
                "TestResident";

        String lastName =
                "FT" + timestamp;

        /*
         * เบอร์โทร 10 หลัก
         *
         * ใช้เลขท้ายจาก timestamp
         */
        String phone =
                generateUniquePhone(timestamp);

        Resident resident =
                new Resident(
                        room,
                        firstName,
                        lastName,
                        phone
                );

        Resident saved =
                residentRepository.save(
                        resident
                );

        assertNotNull(
                saved,
                "Resident should be saved"
        );

        assertNotNull(
                saved.getResidentId(),
                "Saved Resident should have residentId"
        );

        return saved;
    }

    // =========================================================
    // HELPER: Generate Unique Phone
    // =========================================================

    private String generateUniquePhone(
            long timestamp) {

        String digits =
                String.valueOf(
                        timestamp
                );

        /*
         * เอา 9 หลักท้ายของ timestamp
         */
        if (digits.length() > 9) {

            digits =
                    digits.substring(
                            digits.length() - 9
                    );
        }

        /*
         * ให้ขึ้นต้นด้วย 08
         *
         * รวมเป็น 11 ตัวถ้าใช้ 08 + 9 digits
         * จึงตัดให้เหลือ 8 digits หลัง 08
         */
        if (digits.length() > 8) {

            digits =
                    digits.substring(
                            digits.length() - 8
                    );
        }

        return "08" + digits;
    }

    // =========================================================
    // HELPER: Fill Register Form
    // =========================================================

    private void fillRegisterForm(
            String firstName,
            String lastName,
            String username,
            String email,
            String roomNumber,
            String password,
            String confirmPassword,
            String phoneNo) {

        setField(
                "firstName",
                firstName
        );

        setField(
                "lastName",
                lastName
        );

        setField(
                "username",
                username
        );

        setField(
                "phoneNo",
                phoneNo
        );

        setField(
                "email",
                email
        );

        setField(
                "roomNumber",
                roomNumber
        );

        setField(
                "password",
                password
        );

        setField(
                "confirmPassword",
                confirmPassword
        );
    }

    // =========================================================
    // HELPER: Set Field
    // =========================================================

    private void setField(
            String id,
            String value) {

        WebElement element =
                wait.until(
                        ExpectedConditions.presenceOfElementLocated(
                                By.id(id)
                        )
                );

        element.clear();

        element.sendKeys(
                value
        );
    }

    // =========================================================
    // HELPER: Accept Terms
    // =========================================================

    private void acceptTerms() {

        WebElement terms =
                wait.until(
                        ExpectedConditions.presenceOfElementLocated(
                                By.cssSelector(
                                        ".terms-checkbox input[type='checkbox']"
                                )
                        )
                );

        if (!terms.isSelected()) {

            javascriptClick(
                    terms
            );
        }

        assertTrue(
                terms.isSelected(),
                "Terms checkbox should be selected"
        );
    }

    // =========================================================
    // HELPER: Submit
    // =========================================================

    private void submitRegisterForm() {

        WebDriverWait submitWait =
                new WebDriverWait(
                        driver,
                        Duration.ofSeconds(10)
                );

        WebElement form =
                submitWait.until(
                        ExpectedConditions.presenceOfElementLocated(
                                By.cssSelector(
                                        "form.register-form"
                                )
                        )
                );

        WebElement submitButton =
                submitWait.until(
                        ExpectedConditions.elementToBeClickable(
                                By.cssSelector(
                                        "form.register-form button[type='submit']"
                                )
                        )
                );

        System.out.println();
        System.out.println(
                "========== FORM SUBMIT DEBUG =========="
        );

        System.out.println(
                "Current URL BEFORE submit = "
                        + driver.getCurrentUrl()
        );

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
                        + isRegisterFormValid()
        );

        System.out.println(
                "Submit displayed = "
                        + submitButton.isDisplayed()
        );

        System.out.println(
                "Submit enabled = "
                        + submitButton.isEnabled()
        );

        System.out.println(
                "firstName = "
                        + driver.findElement(
                                By.id("firstName")
                        ).getAttribute("value")
        );

        System.out.println(
                "lastName = "
                        + driver.findElement(
                                By.id("lastName")
                        ).getAttribute("value")
        );

        System.out.println(
                "username = "
                        + driver.findElement(
                                By.id("username")
                        ).getAttribute("value")
        );

        System.out.println(
                "phoneNo = "
                        + driver.findElement(
                                By.id("phoneNo")
                        ).getAttribute("value")
        );

        System.out.println(
                "email = "
                        + driver.findElement(
                                By.id("email")
                        ).getAttribute("value")
        );

        System.out.println(
                "roomNumber = "
                        + driver.findElement(
                                By.id("roomNumber")
                        ).getAttribute("value")
        );

        System.out.println(
                "terms selected = "
                        + driver.findElement(
                                By.id("terms1")
                        ).isSelected()
        );

        System.out.println(
                "======================================="
        );

        submitButton.click();

        System.out.println();
        System.out.println(
                "========== AFTER CLICK ================"
        );

        System.out.println(
                "Current URL AFTER click = "
                        + driver.getCurrentUrl()
        );

        System.out.println(
                "======================================="
        );
    }

    // =========================================================
    // HELPER: HTML5 Validation
    // =========================================================

    private boolean isRegisterFormValid() {

        WebElement form =
                wait.until(
                        ExpectedConditions.presenceOfElementLocated(
                                By.cssSelector(
                                        "form.register-form"
                                )
                        )
                );

        return (Boolean)
                ((JavascriptExecutor) driver)
                        .executeScript(
                                "return arguments[0].checkValidity();",
                                form
                        );
    }

    // =========================================================
    // HELPER: Wait Success
    // =========================================================

    private boolean waitForRegisterSuccess() {

        try {

            new WebDriverWait(
                    driver,
                    Duration.ofSeconds(15)
            ).until(
                    currentDriver ->
                            isRegisterSuccessPage()
            );

            return true;

        } catch (Exception e) {

            System.out.println();
            System.out.println(
                    "========== REGISTER WAIT TIMEOUT =========="
            );

            System.out.println(
                    "Current URL = "
                            + driver.getCurrentUrl()
            );

            System.out.println(
                    "Current Title = "
                            + driver.getTitle()
            );

            try {

                System.out.println(
                        "Page Body:"
                );

                System.out.println(
                        driver.findElement(
                                By.tagName("body")
                        ).getText()
                );

            } catch (Exception bodyException) {

                System.out.println(
                        "Cannot read page body: "
                                + bodyException.getMessage()
                );
            }

            System.out.println(
                    "=========================================="
            );

            System.out.println();

            return false;
        }
    }

    // =========================================================
    // HELPER: Check Success
    // =========================================================

    private boolean isRegisterSuccessPage() {

        String currentUrl =
                driver.getCurrentUrl();

        if (currentUrl.contains("/login")) {

            return true;
        }

        String pageText;

        try {

            pageText =
                    driver.findElement(
                            By.tagName("body")
                    ).getText();

        } catch (Exception e) {

            return false;
        }

        return pageText.contains(
                    "สมัครสมาชิกสำเร็จ"
                )
                || pageText.contains(
                    "สมัครสำเร็จ"
                )
                || pageText.contains(
                    "ลงทะเบียนสำเร็จ"
                )
                || pageText.contains(
                    "สมัครสมาชิกเรียบร้อย"
                );
    }

    // =========================================================
    // HELPER: Print Debug
    // =========================================================

    private void printRegisterDebug(
            String username,
            String email) {

        System.out.println();
        System.out.println(
                "=========================================="
        );

        System.out.println(
                "REGISTER TEST DEBUG"
        );

        System.out.println(
                "=========================================="
        );

        System.out.println(
                "Username = "
                        + username
        );

        System.out.println(
                "Email = "
                        + email
        );

        System.out.println(
                "Current URL = "
                        + driver.getCurrentUrl()
        );

        System.out.println(
                "Page Title = "
                        + driver.getTitle()
        );

        try {

            String bodyText =
                    driver.findElement(
                            By.tagName("body")
                    ).getText();

            System.out.println(
                    "Page Body = "
            );

            System.out.println(
                    bodyText
            );

        } catch (Exception e) {

            System.out.println(
                    "Cannot read page body: "
                            + e.getMessage()
            );
        }

        System.out.println(
                "=========================================="
        );

        System.out.println();
    }

    // =========================================================
    // HELPER: JavaScript Click
    // =========================================================

    private void javascriptClick(
            WebElement element) {

        JavascriptExecutor js =
                (JavascriptExecutor) driver;

        js.executeScript(
                "arguments[0].scrollIntoView({block:'center'});",
                element
        );

        js.executeScript(
                "arguments[0].click();",
                element
        );
    }

    // =========================================================
    // HELPER: Sleep
    // =========================================================

    private void sleep(
            long milliseconds) {

        try {

            Thread.sleep(
                    milliseconds
            );

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();
        }
    }

    // =========================================================
    // HELPER: Unique Username
    // =========================================================

    private String uniqueUsername() {

        return "testuser_"
                + System.currentTimeMillis();
    }

    // =========================================================
    // HELPER: Unique Email
    // =========================================================

    private String uniqueEmail() {

        return "test_"
                + System.currentTimeMillis()
                + "@example.com";
    }
}
