package id.ac.ui.cs.advprog.eshop.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import id.ac.ui.cs.advprog.eshop.enums.PaymentMethod;
import id.ac.ui.cs.advprog.eshop.enums.PaymentStatus;
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
          PaymentMethod.BANK_TRANSFER.getValue(), this.paymentData);
    });
  }

  @Test
  void testCreatePaymentDefaultStatus() {
    Payment payment = new Payment("a7f95c5e-9bf7-4a5f-8f20-7a7f6db5a6d1",
        PaymentMethod.BANK_TRANSFER.getValue(), this.paymentData);

    assertEquals("a7f95c5e-9bf7-4a5f-8f20-7a7f6db5a6d1", payment.getId());
    assertEquals(PaymentMethod.BANK_TRANSFER.getValue(), payment.getMethod());
    assertSame(this.paymentData, payment.getPaymentData());
    assertEquals(PaymentStatus.PENDING.getValue(), payment.getStatus());
  }

  @Test
  void testCreatePaymentSuccessStatus() {
    Payment payment = new Payment("a7f95c5e-9bf7-4a5f-8f20-7a7f6db5a6d1",
        PaymentMethod.BANK_TRANSFER.getValue(), this.paymentData, PaymentStatus.SUCCESS.getValue());

    assertEquals(PaymentStatus.SUCCESS.getValue(), payment.getStatus());
  }

  @Test
  void testCreatePaymentInvalidStatus() {
    assertThrows(IllegalArgumentException.class, () -> {
      Payment payment = new Payment("a7f95c5e-9bf7-4a5f-8f20-7a7f6db5a6d1",
          PaymentMethod.BANK_TRANSFER.getValue(), this.paymentData, "MEOW");
    });
  }

  @Test
  void testCreatePaymentInvalidMethod() {
    assertThrows(IllegalArgumentException.class, () -> {
      Payment payment = new Payment("a7f95c5e-9bf7-4a5f-8f20-7a7f6db5a6d1",
          "MEOW", this.paymentData);
    });
  }

  @Test
  void testSetStatusToCancelled() {
    Payment payment = new Payment("a7f95c5e-9bf7-4a5f-8f20-7a7f6db5a6d1",
        PaymentMethod.BANK_TRANSFER.getValue(), this.paymentData);

    payment.setStatus(PaymentStatus.CANCELLED.getValue());
    assertEquals(PaymentStatus.CANCELLED.getValue(), payment.getStatus());
  }

  @Test
  void testSetStatusToInvalidStatus() {
    Payment payment = new Payment("a7f95c5e-9bf7-4a5f-8f20-7a7f6db5a6d1",
        PaymentMethod.BANK_TRANSFER.getValue(), this.paymentData);

    assertThrows(IllegalArgumentException.class,
        () -> payment.setStatus("MEOW"));
  }
}
