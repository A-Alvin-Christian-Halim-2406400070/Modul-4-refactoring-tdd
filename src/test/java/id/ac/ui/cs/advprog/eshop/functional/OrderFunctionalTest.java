package id.ac.ui.cs.advprog.eshop.functional;

import id.ac.ui.cs.advprog.eshop.model.Order;
import id.ac.ui.cs.advprog.eshop.model.Product;
import id.ac.ui.cs.advprog.eshop.repository.OrderRepository;
import io.github.bonigarcia.seljup.SeleniumJupiter;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ExtendWith(SeleniumJupiter.class)
class OrderFunctionalTest {
    private static final String SEEDED_ORDER_AUTHOR = "Safira Sudrajat";

    @LocalServerPort
    private int serverPort;

    @Value("${app.baseUrl:http://localhost}")
    private String testBaseUrl;

    @Autowired
    private OrderRepository orderRepository;

    private String baseUrl;
    private String seededOrderId;

    @BeforeEach
    void setUpTest() {
        baseUrl = String.format("%s:%d", testBaseUrl, serverPort);
        seededOrderId = UUID.randomUUID().toString();

        Product product = new Product();
        product.setProductName("Seeded Product");
        product.setProductQuantity(1);

        Order seededOrder = new Order(
                seededOrderId,
                List.of(product),
                System.currentTimeMillis(),
                SEEDED_ORDER_AUTHOR
        );
        orderRepository.save(seededOrder);
    }

    private void goToCreateOrderFromHome(ChromeDriver driver) {
        driver.get(baseUrl + "/");
        driver.findElement(By.cssSelector("a[href='/order/create']")).click();
    }

    private void goToOrderHistoryFromHome(ChromeDriver driver) {
        driver.get(baseUrl + "/");
        driver.findElement(By.cssSelector("a[href='/order/history']")).click();
    }

    private void goToPayPageFromHome(ChromeDriver driver, String orderId) {
        goToOrderHistoryFromHome(driver);
        driver.findElement(By.id("authorInput")).sendKeys(SEEDED_ORDER_AUTHOR);
        driver.findElement(By.id("searchHistoryButton")).click();
        driver.findElement(By.cssSelector("a[href='/order/pay/" + orderId + "']")).click();
    }

    @Test
    void testGetCreateOrderPage(ChromeDriver driver) {
        goToCreateOrderFromHome(driver);

        assertEquals("Create Order", driver.getTitle());
        assertFalse(driver.findElements(By.id("authorInput")).isEmpty());
        assertFalse(driver.findElements(By.id("createOrderButton")).isEmpty());
    }

    @Test
    void testGetHistoryOrderPage(ChromeDriver driver) {
        goToOrderHistoryFromHome(driver);

        assertEquals("Order History", driver.getTitle());
        assertFalse(driver.findElements(By.id("authorInput")).isEmpty());
        assertFalse(driver.findElements(By.id("searchHistoryButton")).isEmpty());
    }

    @Test
    void testPostHistoryOrderPage(ChromeDriver driver) {
        goToOrderHistoryFromHome(driver);

        driver.findElement(By.id("authorInput")).sendKeys(SEEDED_ORDER_AUTHOR);
        driver.findElement(By.id("searchHistoryButton")).click();

        assertEquals(baseUrl + "/order/history", driver.getCurrentUrl());
        assertEquals("Orders", driver.findElement(By.tagName("h3")).getText());
    }

    @Test
    void testPostHistoryOrderPageUnknownAuthorShowsEmptyResult(ChromeDriver driver) {
        goToOrderHistoryFromHome(driver);

        driver.findElement(By.id("authorInput")).sendKeys("unknown-author-" + UUID.randomUUID());
        driver.findElement(By.id("searchHistoryButton")).click();

        assertEquals(baseUrl + "/order/history", driver.getCurrentUrl());
        assertFalse(driver.findElements(By.xpath("//*[contains(text(),'No orders found.')]")).isEmpty());
        assertTrue(driver.findElements(By.cssSelector("table tbody tr")).isEmpty());
    }

    @Test
    void testGetPayOrderPage(ChromeDriver driver) {
        goToPayPageFromHome(driver, seededOrderId);

        assertEquals("Pay Order", driver.getTitle());
        assertFalse(driver.findElements(By.id("methodInput")).isEmpty());
        assertFalse(driver.findElements(By.id("payOrderButton")).isEmpty());
    }

    @Test
    void testPostPayOrderPage(ChromeDriver driver) {
        goToPayPageFromHome(driver, seededOrderId);

        WebElement methodSelect = driver.findElement(By.id("methodInput"));
        new Select(methodSelect).selectByValue("BANK_TRANSFER");
        driver.findElement(By.id("bankNameInput")).sendKeys("BCA");
        driver.findElement(By.id("referenceCodeInput")).sendKeys("INV-123456");
        driver.findElement(By.id("payOrderButton")).click();

        assertEquals("Payment Created", driver.findElement(By.tagName("h3")).getText());
        assertFalse(driver.findElements(By.id("paymentIdText")).isEmpty());
    }

    @Test
    void testPostPayOrderPageInvalidVoucherCreatesRejectedPayment(ChromeDriver driver) {
        goToPayPageFromHome(driver, seededOrderId);

        WebElement methodSelect = driver.findElement(By.id("methodInput"));
        new Select(methodSelect).selectByValue("VOUCHER_CODE");
        driver.findElement(By.id("voucherCodeInput")).sendKeys("INVALID");
        driver.findElement(By.id("payOrderButton")).click();

        assertEquals("Payment Created", driver.findElement(By.tagName("h3")).getText());
        assertEquals("REJECTED", driver.findElement(By.xpath("//strong[text()='Status:']/following-sibling::span")).getText());
    }
}
