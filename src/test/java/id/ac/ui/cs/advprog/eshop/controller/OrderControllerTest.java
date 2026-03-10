package id.ac.ui.cs.advprog.eshop.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import id.ac.ui.cs.advprog.eshop.model.Order;
import id.ac.ui.cs.advprog.eshop.model.Payment;
import id.ac.ui.cs.advprog.eshop.service.OrderService;
import id.ac.ui.cs.advprog.eshop.service.PaymentService;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

  @Mock
  private OrderService orderService;

  @Mock
  private PaymentService paymentService;

  @Mock
  private Model model;

  private OrderController controller;

  @BeforeEach
  void setUp() throws Exception {
    controller = new OrderController();

    Field orderServiceField = OrderController.class.getDeclaredField("orderService");
    orderServiceField.setAccessible(true);
    orderServiceField.set(controller, orderService);

    Field paymentServiceField = OrderController.class.getDeclaredField("paymentService");
    paymentServiceField.setAccessible(true);
    paymentServiceField.set(controller, paymentService);
  }

  @Test
  void createOrderPostWhenServiceReturnsNullShowsCreateOrderWithError() {
    when(orderService.createOrder(any(Order.class))).thenReturn(null);

    String view = controller.createOrderPost("Author", model);

    assertEquals("CreateOrder", view);
    verify(model).addAttribute("errorMessage", "Failed to create order");
  }

  @Test
  void createOrderPostWhenAuthorIsNullShowsRequiredError() {
    String view = controller.createOrderPost(null, model);

    assertEquals("CreateOrder", view);
    verify(model).addAttribute("errorMessage", "Author name is required");
    verify(orderService, never()).createOrder(any(Order.class));
  }

  @Test
  void orderHistoryPostWhenAuthorIsNullUsesEmptyAuthor() {
    when(orderService.findAllByAuthor("")).thenReturn(List.of());

    String view = controller.orderHistoryPost(null, model);

    assertEquals("OrderList", view);
    verify(orderService).findAllByAuthor("");
    verify(model).addAttribute(eq("orders"), any(List.class));
  }

  @Test
  void payOrderPostWithUnknownMethodSendsEmptyPaymentData() {
    String orderId = "known-order-id";
    Order order = org.mockito.Mockito.mock(Order.class);
    when(orderService.findById(orderId)).thenReturn(order);

    Payment payment = new Payment(orderId, "BANK_TRANSFER", new HashMap<>(java.util.Map.of("bankName", "BCA")));
    when(paymentService.addPayment(eq(order), eq("UNKNOWN_METHOD"), anyMap())).thenReturn(payment);

    String view = controller.payOrderPost(
        orderId,
        "UNKNOWN_METHOD",
        null,
        null,
        null,
        model
    );

    assertEquals("OrderPayResult", view);
    @SuppressWarnings("unchecked")
    ArgumentCaptor<java.util.Map<String, String>> paymentDataCaptor = ArgumentCaptor.forClass(java.util.Map.class);
    verify(paymentService).addPayment(eq(order), eq("UNKNOWN_METHOD"), paymentDataCaptor.capture());
    assertEquals(0, paymentDataCaptor.getValue().size());
  }

  @Test
  void payOrderPostWhenOrderIsNullReturnsPayResultWithPaymentId() {
    String orderId = "missing-order-id";
    when(orderService.findById(orderId)).thenReturn(null);

    String view = controller.payOrderPost(
        orderId,
        "BANK_TRANSFER",
        "BCA",
        "INV-123",
        null,
        model
    );

    assertEquals("OrderPayResult", view);
    verify(model).addAttribute("paymentId", orderId);
    verify(paymentService, never()).addPayment(any(Order.class), anyString(), anyMap());
  }
}
