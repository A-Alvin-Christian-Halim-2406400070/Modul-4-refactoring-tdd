package id.ac.ui.cs.advprog.eshop.service;

import id.ac.ui.cs.advprog.eshop.enums.OrderStatus;
import id.ac.ui.cs.advprog.eshop.enums.PaymentStatus;
import id.ac.ui.cs.advprog.eshop.model.Order;
import id.ac.ui.cs.advprog.eshop.model.Payment;
import id.ac.ui.cs.advprog.eshop.repository.OrderRepository;
import id.ac.ui.cs.advprog.eshop.repository.PaymentRepositoryInterface;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl {
  private final PaymentRepositoryInterface paymentRepository;
  private final OrderRepository orderRepository;

  @Autowired
  public PaymentServiceImpl(PaymentRepositoryInterface paymentRepository,
      OrderRepository orderRepository) {
    this.paymentRepository = paymentRepository;
    this.orderRepository = orderRepository;
  }

  public Payment addPayment(Order order, String method, Map<String, String> paymentData) {
    if (order == null) {
      throw new IllegalArgumentException();
    }

    Payment payment = new Payment(order.getId(), method, paymentData);
    if ("VOUCHER_CODE".equals(method)) {
      String voucherCode = paymentData.get("voucherCode");
      if (isValidVoucherCode(voucherCode)) {
        payment.setStatus(PaymentStatus.SUCCESS.getValue());
      } else {
        payment.setStatus(PaymentStatus.REJECTED.getValue());
      }
    }

    paymentRepository.add(payment);
    return payment;
  }

  public Payment setStatus(Payment payment, String status) {
    payment.setStatus(status);
    Order order = orderRepository.findById(payment.getId());
    if (order != null && PaymentStatus.SUCCESS.getValue().equals(status)) {
      order.setStatus(OrderStatus.SUCCESS.getValue());
    } else if (order != null && PaymentStatus.REJECTED.getValue().equals(status)) {
      order.setStatus(OrderStatus.FAILED.getValue());
    }

    paymentRepository.add(payment);
    return payment;
  }

  public Payment getPayment(String paymentId) {
    return paymentRepository.findById(paymentId);
  }

  public List<Payment> getAllPayments() {
    return paymentRepository.findAll();
  }

  private boolean isValidVoucherCode(String voucherCode) {
    if (voucherCode == null || voucherCode.length() != 16 || !voucherCode.startsWith("ESHOP")) {
      return false;
    }

    long numericCount = voucherCode.chars().filter(Character::isDigit).count();
    return numericCount == 8;
  }
}
