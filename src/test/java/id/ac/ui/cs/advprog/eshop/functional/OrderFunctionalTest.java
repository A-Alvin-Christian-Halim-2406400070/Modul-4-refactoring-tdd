package id.ac.ui.cs.advprog.eshop.functional;

import io.github.bonigarcia.seljup.SeleniumJupiter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ExtendWith(SeleniumJupiter.class)
class OrderFunctionalTest {

    @LocalServerPort
    private int serverPort;

    @Value("${app.baseUrl:http://localhost}")
    private String testBaseUrl;

    private String baseUrl;

    @BeforeEach
    void setUpTest() {
        baseUrl = String.format("%s:%d", testBaseUrl, serverPort);
    }

    @Test
    void testGetCreateOrderPage(ChromeDriver driver) {
        driver.get(baseUrl + "/order/create");

        assertEquals("Create Order", driver.getTitle());
        assertFalse(driver.findElements(By.id("authorInput")).isEmpty());
        assertFalse(driver.findElements(By.id("createOrderButton")).isEmpty());
    }

    @Test
    void testGetHistoryOrderPage(ChromeDriver driver) {
        driver.get(baseUrl + "/order/history");

        assertEquals("Order History", driver.getTitle());
        assertFalse(driver.findElements(By.id("authorInput")).isEmpty());
        assertFalse(driver.findElements(By.id("searchHistoryButton")).isEmpty());
    }

    @Test
    void testPostHistoryOrderPage(ChromeDriver driver) {
        driver.get(baseUrl + "/order/history");

        driver.findElement(By.id("authorInput")).sendKeys("Safira Sudrajat");
        driver.findElement(By.id("searchHistoryButton")).click();

        assertEquals(baseUrl + "/order/history", driver.getCurrentUrl());
        assertEquals("Orders", driver.findElement(By.tagName("h3")).getText());
    }

    @Test
    void testGetPayOrderPage(ChromeDriver driver) {
        String orderId = "13652556-012a-4c07-b546-54eb1396d79b";
        driver.get(baseUrl + "/order/pay/" + orderId);

        assertEquals("Pay Order", driver.getTitle());
        assertFalse(driver.findElements(By.id("methodInput")).isEmpty());
        assertFalse(driver.findElements(By.id("payOrderButton")).isEmpty());
    }

    @Test
    void testPostPayOrderPage(ChromeDriver driver) {
        String orderId = "13652556-012a-4c07-b546-54eb1396d79b";
        driver.get(baseUrl + "/order/pay/" + orderId);

        WebElement methodSelect = driver.findElement(By.id("methodInput"));
        new Select(methodSelect).selectByValue("BANK_TRANSFER");
        driver.findElement(By.id("bankNameInput")).sendKeys("BCA");
        driver.findElement(By.id("referenceCodeInput")).sendKeys("INV-123456");
        driver.findElement(By.id("payOrderButton")).click();

        assertEquals("Payment Created", driver.findElement(By.tagName("h3")).getText());
        assertFalse(driver.findElements(By.id("paymentIdText")).isEmpty());
    }
}
