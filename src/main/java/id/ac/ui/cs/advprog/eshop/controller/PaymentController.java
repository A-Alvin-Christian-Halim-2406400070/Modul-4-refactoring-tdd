package id.ac.ui.cs.advprog.eshop.controller;

import id.ac.ui.cs.advprog.eshop.model.Payment;
import id.ac.ui.cs.advprog.eshop.service.PaymentService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/payment")
public class PaymentController {
  private static final String VIEW_PAYMENT_DETAIL = "PaymentDetail";
  private static final String VIEW_PAYMENT_ADMIN_LIST = "PaymentAdminList";
  private static final String VIEW_PAYMENT_ADMIN_DETAIL = "PaymentAdminDetail";
  private static final String VIEW_PAYMENT_ADMIN_SET_STATUS_RESULT = "PaymentAdminSetStatusResult";

  @Autowired
  private PaymentService paymentService;

  @GetMapping("/detail")
  public String paymentDetailPage() {
    return VIEW_PAYMENT_DETAIL;
  }

  @GetMapping("/detail/{paymentId}")
  public String paymentDetailByIdPage(@PathVariable String paymentId, Model model) {
    Payment payment = paymentService.getPayment(paymentId);
    model.addAttribute("payment", payment);
    model.addAttribute("paymentId", paymentId);
    return VIEW_PAYMENT_DETAIL;
  }

  @GetMapping("/admin/list")
  public String paymentAdminListPage(Model model) {
    List<Payment> payments = paymentService.getAllPayments();
    model.addAttribute("payments", payments);
    return VIEW_PAYMENT_ADMIN_LIST;
  }

  @GetMapping("/admin/detail/{paymentId}")
  public String paymentAdminDetailPage(@PathVariable String paymentId, Model model) {
    Payment payment = paymentService.getPayment(paymentId);
    model.addAttribute("payment", payment);
    return VIEW_PAYMENT_ADMIN_DETAIL;
  }

  @PostMapping("/admin/set-status/{paymentId}")
  public String paymentAdminSetStatusPost(@PathVariable String paymentId,
      @RequestParam("status") String status,
      Model model) {
    Payment payment = paymentService.getPayment(paymentId);
    if (payment != null) {
      payment = paymentService.setStatus(payment, status);
    }

    model.addAttribute("payment", payment);
    return VIEW_PAYMENT_ADMIN_SET_STATUS_RESULT;
  }
}
