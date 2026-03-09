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
    paymentRepository.add(payment);
    return payment;
  }

  public Payment setStatus(Payment payment, String status) {
    if ("REJECTED".equals(status)) {
      payment.setStatus(PaymentStatus.FAILED.getValue());
      Order order = orderRepository.findById(payment.getId());
      if (order != null) {
        order.setStatus(OrderStatus.FAILED.getValue());
      }
    } else {
      payment.setStatus(status);
      if (PaymentStatus.SUCCESS.getValue().equals(status)) {
        Order order = orderRepository.findById(payment.getId());
        if (order != null) {
          order.setStatus(OrderStatus.SUCCESS.getValue());
        }
      }
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
}
