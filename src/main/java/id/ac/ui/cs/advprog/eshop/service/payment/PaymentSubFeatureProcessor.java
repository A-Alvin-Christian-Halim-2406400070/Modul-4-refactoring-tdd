package id.ac.ui.cs.advprog.eshop.service.payment;

import id.ac.ui.cs.advprog.eshop.enums.PaymentMethod;
import id.ac.ui.cs.advprog.eshop.model.Payment;

public interface PaymentSubFeatureProcessor {
  PaymentMethod getPaymentMethod();
  void process(Payment payment);
}
