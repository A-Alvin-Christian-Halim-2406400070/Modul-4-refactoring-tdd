package id.ac.ui.cs.advprog.eshop.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import id.ac.ui.cs.advprog.eshop.enums.OrderStatus;
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
class PaymentServiceTest {

  @InjectMocks
  PaymentServiceImpl paymentService;

  @Mock
  PaymentRepositoryInterface paymentRepository;

  @Mock
  OrderRepository orderRepository;

  private Order order;
  private Payment payment;
  private Map<String, String> paymentData;

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

    this.paymentData = new HashMap<>();
    this.paymentData.put("bankName", "BCA");
    this.paymentData.put("referenceCode", "INV-123456");

    this.payment = new Payment(order.getId(), PaymentMethod.BANK_TRANSFER.getValue(), this.paymentData);
  }

  @Test
  void testAddPayment() {
    doAnswer(invocation -> invocation.getArgument(0)).when(paymentRepository).add(any(Payment.class));

    Payment result = paymentService.addPayment(order, PaymentMethod.BANK_TRANSFER.getValue(), paymentData);

    verify(paymentRepository, times(1)).add(any(Payment.class));
    assertEquals(order.getId(), result.getId());
    assertEquals(PaymentMethod.BANK_TRANSFER.getValue(), result.getMethod());
    assertSame(paymentData, result.getPaymentData());
    assertEquals(PaymentStatus.SUCCESS.getValue(), result.getStatus());
  }

  @Test
  void testAddPaymentEmptyPaymentData() {
    Map<String, String> emptyData = new HashMap<>();

    assertThrows(IllegalArgumentException.class,
        () -> paymentService.addPayment(order, PaymentMethod.BANK_TRANSFER.getValue(), emptyData));

    verify(paymentRepository, times(0)).add(any(Payment.class));
  }

  @Test
  void testAddPaymentNullOrder() {
    assertThrows(IllegalArgumentException.class,
        () -> paymentService.addPayment(null, PaymentMethod.BANK_TRANSFER.getValue(), paymentData));

    verify(paymentRepository, times(0)).add(any(Payment.class));
  }

  @Test
  void testAddPaymentInvalidMethod() {
    assertThrows(IllegalArgumentException.class,
        () -> paymentService.addPayment(order, "MEOW", paymentData));

    verify(paymentRepository, times(0)).add(any(Payment.class));
  }

  @Test
  void testSetStatusToSuccessShouldUpdateOrderToSuccess() {
    doReturn(order).when(orderRepository).findById(payment.getId());
    doAnswer(invocation -> invocation.getArgument(0)).when(paymentRepository).add(any(Payment.class));

    Payment result = paymentService.setStatus(payment, PaymentStatus.SUCCESS.getValue());

    assertEquals(PaymentStatus.SUCCESS.getValue(), result.getStatus());
    assertEquals(OrderStatus.SUCCESS.getValue(), order.getStatus());
    verify(paymentRepository, times(1)).add(any(Payment.class));
  }

  @Test
  void testSetStatusToRejectedShouldUpdateOrderToFailed() {
    doReturn(order).when(orderRepository).findById(payment.getId());
    doAnswer(invocation -> invocation.getArgument(0)).when(paymentRepository).add(any(Payment.class));

    Payment result = paymentService.setStatus(payment, PaymentStatus.REJECTED.getValue());

    assertNotEquals(PaymentStatus.PENDING.getValue(), result.getStatus());
    assertEquals(OrderStatus.FAILED.getValue(), order.getStatus());
    verify(paymentRepository, times(1)).add(any(Payment.class));
  }

  @Test
  void testSetStatusInvalidStatus() {
    assertThrows(IllegalArgumentException.class,
        () -> paymentService.setStatus(payment, "MEOW"));
    assertEquals(PaymentStatus.PENDING.getValue(), payment.getStatus());
  }

  @Test
  void testGetPaymentIfFound() {
    doReturn(payment).when(paymentRepository).findById(payment.getId());

    Payment result = paymentService.getPayment(payment.getId());

    assertEquals(payment.getId(), result.getId());
  }

  @Test
  void testGetPaymentIfNotFound() {
    doReturn(null).when(paymentRepository).findById("zczc");

    assertNull(paymentService.getPayment("zczc"));
  }

  @Test
  void testGetAllPaymentsIfDataExists() {
    List<Payment> payments = new ArrayList<>();
    payments.add(payment);
    payments.add(new Payment("7f9e15bb-4b15-42f4-aebc-c3af385fb078",
        PaymentMethod.BANK_TRANSFER.getValue(), paymentData));
    doReturn(payments).when(paymentRepository).findAll();

    List<Payment> result = paymentService.getAllPayments();

    assertEquals(2, result.size());
  }

  @Test
  void testGetAllPaymentsIfEmpty() {
    doReturn(new ArrayList<Payment>()).when(paymentRepository).findAll();

    List<Payment> result = paymentService.getAllPayments();

    assertTrue(result.isEmpty());
  }
}
