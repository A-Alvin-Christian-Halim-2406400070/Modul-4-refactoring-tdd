package id.ac.ui.cs.advprog.eshop.functional;

import io.github.bonigarcia.seljup.SeleniumJupiter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import id.ac.ui.cs.advprog.eshop.enums.PaymentMethod;
import id.ac.ui.cs.advprog.eshop.model.Payment;
import id.ac.ui.cs.advprog.eshop.repository.PaymentRepositoryInterface;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ExtendWith(SeleniumJupiter.class)
class PaymentFunctionalTest {

    @LocalServerPort
    private int serverPort;

    @Value("${app.baseUrl:http://localhost}")
    private String testBaseUrl;

    @Autowired
    private PaymentRepositoryInterface paymentRepository;

    private String baseUrl;
    private String seededPaymentId;

    @BeforeEach
    void setUpTest() {
        baseUrl = String.format("%s:%d", testBaseUrl, serverPort);

        seededPaymentId = UUID.randomUUID().toString();
        Map<String, String> paymentData = new HashMap<>();
        paymentData.put("bankName", "BCA");
        paymentData.put("referenceCode", "INV-123456");
        Payment seededPayment = new Payment(
                seededPaymentId,
                PaymentMethod.BANK_TRANSFER.getValue(),
                paymentData
        );
        paymentRepository.add(seededPayment);
    }

    private void goToPaymentDetailFromHome(ChromeDriver driver) {
        driver.get(baseUrl + "/");
        driver.findElement(By.cssSelector("a[href='/payment/detail']")).click();
    }

    private void goToPaymentAdminListFromHome(ChromeDriver driver) {
        driver.get(baseUrl + "/");
        driver.findElement(By.cssSelector("a[href='/payment/admin/list']")).click();
    }

    @Test
    void testGetPaymentDetailPage(ChromeDriver driver) {
        goToPaymentDetailFromHome(driver);

        assertEquals("Payment Detail", driver.getTitle());
        assertFalse(driver.findElements(By.id("paymentIdInput")).isEmpty());
        assertFalse(driver.findElements(By.id("findPaymentButton")).isEmpty());
    }

    @Test
    void testGetPaymentDetailByIdPage(ChromeDriver driver) {
        goToPaymentDetailFromHome(driver);
        driver.findElement(By.id("paymentIdInput")).sendKeys(seededPaymentId);
        driver.findElement(By.id("findPaymentButton")).click();

        assertEquals(baseUrl + "/payment/detail/" + seededPaymentId, driver.getCurrentUrl());
        assertEquals("Payment Detail", driver.getTitle());
        assertEquals(seededPaymentId, driver.findElement(By.id("paymentIdText")).getText());
    }

    @Test
    void testGetPaymentDetailByUnknownIdPageShowsNotFoundMessage(ChromeDriver driver) {
        String unknownPaymentId = "unknown-" + UUID.randomUUID();
        goToPaymentDetailFromHome(driver);
        driver.findElement(By.id("paymentIdInput")).sendKeys(unknownPaymentId);
        driver.findElement(By.id("findPaymentButton")).click();

        assertEquals(baseUrl + "/payment/detail/" + unknownPaymentId, driver.getCurrentUrl());
        assertEquals("Payment Detail", driver.getTitle());
        assertFalse(driver.findElements(By.xpath("//*[contains(text(),'Payment not found for ID:')]")).isEmpty());
    }

    @Test
    void testGetPaymentAdminListPage(ChromeDriver driver) {
        goToPaymentAdminListFromHome(driver);

        assertEquals("Payment Admin List", driver.getTitle());
        assertFalse(driver.findElements(By.id("paymentTable")).isEmpty());
        assertFalse(driver.findElements(By.id("view-admin-detail-btn-" + seededPaymentId)).isEmpty());
    }

    @Test
    void testGetPaymentAdminDetailPage(ChromeDriver driver) {
        goToPaymentAdminListFromHome(driver);
        driver.findElement(By.id("view-admin-detail-btn-" + seededPaymentId)).click();

        assertEquals("Payment Admin Detail", driver.getTitle());
        assertFalse(driver.findElements(By.id("acceptPaymentButton")).isEmpty());
        assertFalse(driver.findElements(By.id("rejectPaymentButton")).isEmpty());
    }

    @Test
    void testPostPaymentAdminSetStatusPage(ChromeDriver driver) {
        goToPaymentAdminListFromHome(driver);
        driver.findElement(By.id("view-admin-detail-btn-" + seededPaymentId)).click();
        driver.findElement(By.id("acceptPaymentButton")).click();

        String currentUrl = driver.getCurrentUrl();
        assertTrue(currentUrl.contains("/payment/admin"));

        List<WebElement> updatedStatus = driver.findElements(By.id("updatedStatusText"));
        List<WebElement> detailStatus = driver.findElements(By.id("paymentStatusText"));
        assertTrue(!updatedStatus.isEmpty() || !detailStatus.isEmpty());

        if (!updatedStatus.isEmpty()) {
            assertEquals("SUCCESS", updatedStatus.get(0).getText());
        } else {
            assertEquals("SUCCESS", detailStatus.get(0).getText());
        }
    }

    @Test
    void testPostPaymentAdminSetRejectedStatusPage(ChromeDriver driver) {
        goToPaymentAdminListFromHome(driver);
        driver.findElement(By.id("view-admin-detail-btn-" + seededPaymentId)).click();
        driver.findElement(By.id("rejectPaymentButton")).click();

        String currentUrl = driver.getCurrentUrl();
        assertTrue(currentUrl.contains("/payment/admin"));

        List<WebElement> updatedStatus = driver.findElements(By.id("updatedStatusText"));
        List<WebElement> detailStatus = driver.findElements(By.id("paymentStatusText"));
        assertTrue(!updatedStatus.isEmpty() || !detailStatus.isEmpty());

        if (!updatedStatus.isEmpty()) {
            assertEquals("REJECTED", updatedStatus.get(0).getText());
        } else {
            assertEquals("REJECTED", detailStatus.get(0).getText());
        }
    }
}
