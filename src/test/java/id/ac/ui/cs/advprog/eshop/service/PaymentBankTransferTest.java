package id.ac.ui.cs.advprog.eshop.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import id.ac.ui.cs.advprog.eshop.enums.PaymentMethod;
import id.ac.ui.cs.advprog.eshop.enums.PaymentStatus;
import id.ac.ui.cs.advprog.eshop.model.Order;
import id.ac.ui.cs.advprog.eshop.model.Payment;
import id.ac.ui.cs.advprog.eshop.model.Product;
import id.ac.ui.cs.advprog.eshop.repository.OrderRepository;
import id.ac.ui.cs.advprog.eshop.repository.PaymentRepositoryInterface;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PaymentBankTransferTest {

  @InjectMocks
  PaymentServiceImpl paymentService;

  @Mock
  PaymentRepositoryInterface paymentRepository;

  @Mock
  OrderRepository orderRepository;

  private Order order;

  @BeforeEach
  void setUp() {
    List<Product> products = new ArrayList<>();
    Product product = new Product();
    product.setProductId("eb558e9f-1c39-460e-8860-71af6af63bd6");
    product.setProductName("Sampo Cap Bambang");
    product.setProductQuantity(2);
    products.add(product);

    this.order = new Order("13652556-012a-4c07-b546-54eb1396d79b",
        products, 1708560000L, "Safira Sudrajat");

    doAnswer(invocation -> invocation.getArgument(0))
        .when(paymentRepository).add(any(Payment.class));
  }

  @Test
  void testAddPaymentWithValidBankTransferDataShouldSetStatusSuccess() {
    Map<String, String> paymentData = new HashMap<>();
    paymentData.put("bankName", "BCA");
    paymentData.put("referenceCode", "INV-123456");

    Payment payment = paymentService.addPayment(order, PaymentMethod.BANK_TRANSFER.getValue(), paymentData);

    assertEquals(PaymentStatus.SUCCESS.getValue(), payment.getStatus());
    assertEquals("BCA", payment.getPaymentData().get("bankName"));
    assertEquals("INV-123456", payment.getPaymentData().get("referenceCode"));
    verify(paymentRepository, times(1)).add(any(Payment.class));
  }

  @Test
  void testAddPaymentWithNullBankNameShouldSetStatusRejected() {
    Map<String, String> paymentData = new HashMap<>();
    paymentData.put("bankName", null);
    paymentData.put("referenceCode", "INV-123456");

    Payment payment = paymentService.addPayment(order, PaymentMethod.BANK_TRANSFER.getValue(), paymentData);

    assertEquals(PaymentStatus.REJECTED.getValue(), payment.getStatus());
    verify(paymentRepository, times(1)).add(any(Payment.class));
  }

  @Test
  void testAddPaymentWithEmptyBankNameShouldSetStatusRejected() {
    Map<String, String> paymentData = new HashMap<>();
    paymentData.put("bankName", "");
    paymentData.put("referenceCode", "INV-123456");

    Payment payment = paymentService.addPayment(order, PaymentMethod.BANK_TRANSFER.getValue(), paymentData);

    assertEquals(PaymentStatus.REJECTED.getValue(), payment.getStatus());
    verify(paymentRepository, times(1)).add(any(Payment.class));
  }

  @Test
  void testAddPaymentWithNullReferenceCodeShouldSetStatusRejected() {
    Map<String, String> paymentData = new HashMap<>();
    paymentData.put("bankName", "BCA");
    paymentData.put("referenceCode", null);

    Payment payment = paymentService.addPayment(order, PaymentMethod.BANK_TRANSFER.getValue(), paymentData);

    assertEquals(PaymentStatus.REJECTED.getValue(), payment.getStatus());
    verify(paymentRepository, times(1)).add(any(Payment.class));
  }

  @Test
  void testAddPaymentWithEmptyReferenceCodeShouldSetStatusRejected() {
    Map<String, String> paymentData = new HashMap<>();
    paymentData.put("bankName", "BCA");
    paymentData.put("referenceCode", "");

    Payment payment = paymentService.addPayment(order, PaymentMethod.BANK_TRANSFER.getValue(), paymentData);

    assertEquals(PaymentStatus.REJECTED.getValue(), payment.getStatus());
    verify(paymentRepository, times(1)).add(any(Payment.class));
  }
}
