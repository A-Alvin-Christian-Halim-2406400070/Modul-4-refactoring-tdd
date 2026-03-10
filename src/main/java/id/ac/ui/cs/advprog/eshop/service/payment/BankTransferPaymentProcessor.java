package id.ac.ui.cs.advprog.eshop.service.payment;

import id.ac.ui.cs.advprog.eshop.enums.PaymentMethod;
import id.ac.ui.cs.advprog.eshop.enums.PaymentStatus;
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
    String bankName = payment.getPaymentData().get("bankName");
    String referenceCode = payment.getPaymentData().get("referenceCode");

    if (isNullOrEmpty(bankName) || isNullOrEmpty(referenceCode)) {
      payment.setStatus(PaymentStatus.REJECTED.getValue());
    } else {
      payment.setStatus(PaymentStatus.SUCCESS.getValue());
    }
  }

  private boolean isNullOrEmpty(String value) {
    return value == null || value.isEmpty();
  }
}
