package id.ac.ui.cs.advprog.eshop.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import id.ac.ui.cs.advprog.eshop.model.Payment;
import id.ac.ui.cs.advprog.eshop.service.PaymentService;
import java.lang.reflect.Field;
import java.util.HashMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

@ExtendWith(MockitoExtension.class)
class PaymentControllerTest {

  @Mock
  private PaymentService paymentService;

  @Mock
  private Model model;

  private PaymentController controller;

  @BeforeEach
  void setUp() throws Exception {
    controller = new PaymentController();
    Field field = PaymentController.class.getDeclaredField("paymentService");
    field.setAccessible(true);
    field.set(controller, paymentService);
  }

  @Test
  void paymentAdminSetStatusWhenPaymentNotFoundReturnsResultWithNullPayment() {
    String paymentId = "missing-payment";
    when(paymentService.getPayment(paymentId)).thenReturn(null);

    String view = controller.paymentAdminSetStatusPost(paymentId, "SUCCESS", model);

    assertEquals("PaymentAdminSetStatusResult", view);
    verify(paymentService).getPayment(paymentId);
    verify(paymentService, never()).setStatus(any(Payment.class), anyString());
    verify(model).addAttribute("payment", null);
  }

  @Test
  void paymentAdminSetStatusWhenPaymentFoundCallsSetStatus() {
    String paymentId = "payment-id";
    Payment payment = new Payment(paymentId, "BANK_TRANSFER", new HashMap<>(java.util.Map.of("bankName", "BCA")));
    when(paymentService.getPayment(paymentId)).thenReturn(payment);
    when(paymentService.setStatus(payment, "SUCCESS")).thenReturn(payment);

    String view = controller.paymentAdminSetStatusPost(paymentId, "SUCCESS", model);

    assertEquals("PaymentAdminSetStatusResult", view);
    verify(paymentService).setStatus(payment, "SUCCESS");
    verify(model).addAttribute("payment", payment);
  }
}
