package id.ac.ui.cs.advprog.eshop.service;

import id.ac.ui.cs.advprog.eshop.model.Order;
import id.ac.ui.cs.advprog.eshop.model.Payment;
import id.ac.ui.cs.advprog.eshop.repository.OrderRepository;
import id.ac.ui.cs.advprog.eshop.repository.PaymentRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl {
  @Autowired
  private PaymentRepository paymentRepository;

  @Autowired
  private OrderRepository orderRepository;

  public Payment addPayment(Order order, String method, Map<String, String> paymentData) {
    return null;
  }

  public Payment setStatus(Payment payment, String status) {
    return null;
  }

  public Payment getPayment(String paymentId) {
    return null;
  }

  public List<Payment> getAllPayments() {
    return new ArrayList<>();
  }
}
