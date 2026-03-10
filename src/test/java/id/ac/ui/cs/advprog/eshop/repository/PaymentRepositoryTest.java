package id.ac.ui.cs.advprog.eshop.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import id.ac.ui.cs.advprog.eshop.enums.PaymentMethod;
import id.ac.ui.cs.advprog.eshop.enums.PaymentStatus;
import id.ac.ui.cs.advprog.eshop.model.Payment;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PaymentRepositoryTest {
  PaymentRepository paymentRepository;
  List<Payment> payments;

  @BeforeEach
  void setUp() {
    paymentRepository = new PaymentRepository();

    Map<String, String> paymentData1 = new HashMap<>();
    paymentData1.put("bank", "BCA");
    paymentData1.put("accountNumber", "1234567890");

    Map<String, String> paymentData2 = new HashMap<>();
    paymentData2.put("bank", "BRI");
    paymentData2.put("accountNumber", "9999999999");

    Map<String, String> paymentData3 = new HashMap<>();
    paymentData3.put("bank", "MANDIRI");
    paymentData3.put("accountNumber", "8888888888");

    payments = new ArrayList<>();
    Payment payment1 = new Payment("13652556-012a-4c07-b546-54eb1396d79b",
        PaymentMethod.BANK_TRANSFER.getValue(), paymentData1);
    payments.add(payment1);

    Payment payment2 = new Payment("7f9e15bb-4b15-42f4-aebc-c3af385fb078",
        PaymentMethod.BANK_TRANSFER.getValue(), paymentData2);
    payments.add(payment2);

    Payment payment3 = new Payment("e334ef40-9eff-4da8-9487-8ee697ecbf1e",
        PaymentMethod.BANK_TRANSFER.getValue(), paymentData3);
    payments.add(payment3);
  }

  @Test
  void testAddCreate() {
    Payment payment = payments.get(1);
    Payment result = paymentRepository.add(payment);

    Payment findResult = paymentRepository.findById(payments.get(1).getId());
    assertEquals(payment.getId(), result.getId());
    assertEquals(payment.getId(), findResult.getId());
    assertEquals(payment.getMethod(), findResult.getMethod());
    assertEquals(payment.getStatus(), findResult.getStatus());
    assertEquals(payment.getPaymentData(), findResult.getPaymentData());
  }

  @Test
  void testAddUpdate() {
    Payment payment = payments.get(1);
    paymentRepository.add(payment);

    Payment newPayment = new Payment(payment.getId(), payment.getMethod(), payment.getPaymentData(),
        PaymentStatus.SUCCESS.getValue());
    Payment result = paymentRepository.add(newPayment);

    Payment findResult = paymentRepository.findById(payments.get(1).getId());
    assertEquals(payment.getId(), result.getId());
    assertEquals(payment.getId(), findResult.getId());
    assertEquals(payment.getMethod(), findResult.getMethod());
    assertEquals(PaymentStatus.SUCCESS.getValue(), findResult.getStatus());
    assertEquals(payment.getPaymentData(), findResult.getPaymentData());
  }

  @Test
  void testFindByIdIfIdFound() {
    for (Payment payment : payments) {
      paymentRepository.add(payment);
    }

    Payment findResult = paymentRepository.findById(payments.get(1).getId());
    assertEquals(payments.get(1).getId(), findResult.getId());
    assertEquals(payments.get(1).getMethod(), findResult.getMethod());
    assertEquals(payments.get(1).getStatus(), findResult.getStatus());
    assertEquals(payments.get(1).getPaymentData(), findResult.getPaymentData());
  }

  @Test
  void testFindByIdIfIdNotFound() {
    for (Payment payment : payments) {
      paymentRepository.add(payment);
    }

    Payment findResult = paymentRepository.findById("zczc");
    assertNull(findResult);
  }

  @Test
  void testFindAllIfDataExists() {
    for (Payment payment : payments) {
      paymentRepository.add(payment);
    }

    List<Payment> paymentList = paymentRepository.findAll();
    assertEquals(3, paymentList.size());
  }

  @Test
  void testFindAllIfEmpty() {
    List<Payment> paymentList = paymentRepository.findAll();
    assertTrue(paymentList.isEmpty());
  }
}
