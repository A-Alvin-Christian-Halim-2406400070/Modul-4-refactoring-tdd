package id.ac.ui.cs.advprog.eshop.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PaymentTest {
  private Map<String, String> paymentData;

  @BeforeEach
  void setUp() {
    this.paymentData = new HashMap<>();
    this.paymentData.put("bank", "BCA");
    this.paymentData.put("accountNumber", "1234567890");
  }

  @Test
  void testCreatePaymentEmptyPaymentData() {
    this.paymentData.clear();

    assertThrows(IllegalArgumentException.class, () -> {
      Payment payment = new Payment("a7f95c5e-9bf7-4a5f-8f20-7a7f6db5a6d1",
          "BANK_TRANSFER", this.paymentData);
    });
  }

  @Test
  void testCreatePaymentDefaultStatus() {
    Payment payment = new Payment("a7f95c5e-9bf7-4a5f-8f20-7a7f6db5a6d1",
        "BANK_TRANSFER", this.paymentData);

    assertEquals("a7f95c5e-9bf7-4a5f-8f20-7a7f6db5a6d1", payment.getId());
    assertEquals("BANK_TRANSFER", payment.getMethod());
    assertSame(this.paymentData, payment.getPaymentData());
    assertEquals("WAITING_PAYMENT", payment.getStatus());
  }

  @Test
  void testCreatePaymentSuccessStatus() {
    Payment payment = new Payment("a7f95c5e-9bf7-4a5f-8f20-7a7f6db5a6d1",
        "BANK_TRANSFER", this.paymentData, "SUCCESS");

    assertEquals("SUCCESS", payment.getStatus());
  }

  @Test
  void testCreatePaymentInvalidStatus() {
    assertThrows(IllegalArgumentException.class, () -> {
      Payment payment = new Payment("a7f95c5e-9bf7-4a5f-8f20-7a7f6db5a6d1",
          "BANK_TRANSFER", this.paymentData, "MEOW");
    });
  }

  @Test
  void testSetStatusToCancelled() {
    Payment payment = new Payment("a7f95c5e-9bf7-4a5f-8f20-7a7f6db5a6d1",
        "BANK_TRANSFER", this.paymentData);

    payment.setStatus("CANCELLED");
    assertEquals("CANCELLED", payment.getStatus());
  }

  @Test
  void testSetStatusToInvalidStatus() {
    Payment payment = new Payment("a7f95c5e-9bf7-4a5f-8f20-7a7f6db5a6d1",
        "BANK_TRANSFER", this.paymentData);

    assertThrows(IllegalArgumentException.class,
        () -> payment.setStatus("MEOW"));
  }
}
