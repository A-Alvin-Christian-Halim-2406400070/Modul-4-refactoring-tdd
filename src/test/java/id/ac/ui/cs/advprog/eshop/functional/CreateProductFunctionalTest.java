package id.ac.ui.cs.advprog.eshop.functional;

import io.github.bonigarcia.seljup.SeleniumJupiter ;
import java.time.Duration;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value ;
import org.springframework.boot.test.context.SpringBootTest ;
import org.springframework.boot.test.web.server.LocalServerPort ;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT ;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ExtendWith(SeleniumJupiter.class)
public class CreateProductFunctionalTest {
    @LocalServerPort
    private int serverPort;

    @Value("${app.baseUrl:http://localhost}")
    private String testBaseUrl;
    private String baseUrl;
    @BeforeEach
    void setUpTest() {
        baseUrl = String.format("%s:%d/product/list", testBaseUrl, serverPort);
    }


    @Test
    void testCreateValidProduct(ChromeDriver driver) {
        driver.get(baseUrl);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        driver.findElement(By.id("createProductButton")).click();
        wait.until(ExpectedConditions.urlToBe(baseUrl.replace("/list", "/create")));
        assertEquals(baseUrl.replace("/list", "/create"), driver.getCurrentUrl());

        driver.findElement(By.name("productName")).sendKeys("Sample Product");

        var qty = driver.findElement(By.name("productQuantity"));
        qty.clear();
        qty.sendKeys("10");
        driver.findElement(By.id("submitButton")).click();

        wait.until(ExpectedConditions.urlToBe(baseUrl));
        assertEquals(baseUrl, driver.getCurrentUrl());
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//table/tbody/tr[last()]/td[1]")));
        String createdProductName = driver.findElement(By.xpath("//table/tbody/tr[last()]/td[1]")).getText();
        String createdProductQuantity = driver.findElement(By.xpath("//table/tbody/tr[last()]/td[2]" )).getText();
        assertEquals("Sample Product", createdProductName);
        assertEquals("10", createdProductQuantity);
    }

    void testInvalidCreateProductTemplate(ChromeDriver driver, String quantityInput, String expectedErrorMessage) {
        driver.get(baseUrl);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        driver.findElement(By.id("createProductButton")).click();
        wait.until(ExpectedConditions.urlToBe(baseUrl.replace("/list", "/create")));
        driver.findElement(By.name("productName")).sendKeys("Invalid Quantity Product");

        var qty = driver.findElement(By.name("productQuantity"));
        qty.clear();
        qty.sendKeys(quantityInput);
        driver.findElement(By.id("submitButton")).click();

        wait.until(ExpectedConditions.urlToBe(baseUrl.replace("/list", "/create")));
        assertEquals(baseUrl.replace("/list", "/create"), driver.getCurrentUrl(),"creating a invalid product shouldn't redirect to list page");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("quantityError")));
        String errorMessage = driver.findElement(By.id("quantityError")).getText();
        assertEquals(expectedErrorMessage, errorMessage);
        WebElement errorElem = driver.findElement(By.id("quantityError"));
        String color = errorElem.getCssValue("color");
        String rgb = color.replace("rgba(", "").replace("rgb(", "").replace(")", "");
        String[] parts = rgb.split(",");
        if (parts.length < 3) {
            throw new AssertionError("Unexpected CSS color format: " + color);
        }
        int r;
        int g;
        int b;
        try {
            r = Integer.parseInt(parts[0].trim());
            g = Integer.parseInt(parts[1].trim());
            b = Integer.parseInt(parts[2].trim());
        } catch (NumberFormatException e) {
            throw new AssertionError("Failed to parse RGB color from CSS value: " + color, e);
        }
        assertTrue(r > g && r > b && r > 50, "Red channel is not dominant and sufficiently strong");

    }

    @Test
    void testInvalidCreateProductQuantity_StringQuantity(ChromeDriver driver) {
        testInvalidCreateProductTemplate(driver, "ten", "Quantity must be an integer");
    }

    @Test
    void testInvalidCreateProductQuantity_FloatQuantity(ChromeDriver driver) {
        testInvalidCreateProductTemplate(driver, "5.5", "Quantity must be an integer");
    }

    @Test
    void testInvalidCreateProductQuantity_NegativeQuantity(ChromeDriver driver) {
        testInvalidCreateProductTemplate(driver, "-5", "Quantity must be positive");
    }
    @Test
    void testInvalidCreateProductQuantity_ZeroQuantity(ChromeDriver driver) {
        testInvalidCreateProductTemplate(driver, "0", "Quantity must be positive");
    }
}
