package id.ac.ui.cs.advprog.eshop.service;

import id.ac.ui.cs.advprog.eshop.enums.OrderStatus;
import id.ac.ui.cs.advprog.eshop.enums.PaymentMethod;
import id.ac.ui.cs.advprog.eshop.enums.PaymentStatus;
import id.ac.ui.cs.advprog.eshop.model.Order;
import id.ac.ui.cs.advprog.eshop.model.Payment;
import id.ac.ui.cs.advprog.eshop.repository.OrderRepository;
import id.ac.ui.cs.advprog.eshop.repository.PaymentRepositoryInterface;
import id.ac.ui.cs.advprog.eshop.service.payment.BankTransferPaymentProcessor;
import id.ac.ui.cs.advprog.eshop.service.payment.PaymentSubFeatureProcessor;
import id.ac.ui.cs.advprog.eshop.service.payment.VoucherCodePaymentProcessor;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl implements PaymentService {
  private final PaymentRepositoryInterface paymentRepository;
  private final OrderRepository orderRepository;
  private final Map<PaymentMethod, PaymentSubFeatureProcessor> paymentSubFeatureProcessors;

  @Autowired
  public PaymentServiceImpl(PaymentRepositoryInterface paymentRepository,
      OrderRepository orderRepository,
      List<PaymentSubFeatureProcessor> paymentSubFeatureProcessors) {
    this.paymentRepository = paymentRepository;
    this.orderRepository = orderRepository;
    this.paymentSubFeatureProcessors = new EnumMap<>(PaymentMethod.class);

    registerDefaultSubFeatureProcessors();
    if (paymentSubFeatureProcessors != null) {
      for (PaymentSubFeatureProcessor paymentSubFeatureProcessor : paymentSubFeatureProcessors) {
        this.paymentSubFeatureProcessors.put(
            paymentSubFeatureProcessor.getPaymentMethod(),
            paymentSubFeatureProcessor
        );
      }
    }
  }

  @Override
  public Payment addPayment(Order order, String method, Map<String, String> paymentData) {
    if (order == null) {
      throw new IllegalArgumentException();
    }

    PaymentMethod paymentMethod = PaymentMethod.fromValue(method);
    Payment payment = new Payment(order.getId(), paymentMethod.getValue(), paymentData);

    PaymentSubFeatureProcessor paymentSubFeatureProcessor = paymentSubFeatureProcessors.get(paymentMethod);
    if (paymentSubFeatureProcessor != null) {
      paymentSubFeatureProcessor.process(payment);
    } else {
      throw new IllegalArgumentException();
    }

    paymentRepository.add(payment);
    return payment;
  }

  @Override
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

  @Override
  public Payment getPayment(String paymentId) {
    return paymentRepository.findById(paymentId);
  }

  @Override
  public List<Payment> getAllPayments() {
    return paymentRepository.findAll();
  }

  private void registerDefaultSubFeatureProcessors() {
    paymentSubFeatureProcessors.put(PaymentMethod.BANK_TRANSFER, new BankTransferPaymentProcessor());
    paymentSubFeatureProcessors.put(PaymentMethod.VOUCHER_CODE, new VoucherCodePaymentProcessor());
  }
}
