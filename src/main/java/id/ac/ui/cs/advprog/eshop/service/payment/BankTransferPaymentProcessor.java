package id.ac.ui.cs.advprog.eshop.service.payment;

import id.ac.ui.cs.advprog.eshop.enums.PaymentMethod;
import id.ac.ui.cs.advprog.eshop.model.Payment;
import org.springframework.stereotype.Component;

@Component
public class BankTransferPaymentProcessor implements PaymentSubFeatureProcessor {

  @Override
  public PaymentMethod getPaymentMethod() {
    return PaymentMethod.BANK_TRANSFER;
  }

  @Override
  public void process(Payment payment) {
    // Keep default PENDING status for bank transfer.
  }
}
