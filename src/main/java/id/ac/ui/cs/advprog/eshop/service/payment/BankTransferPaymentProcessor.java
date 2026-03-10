package id.ac.ui.cs.advprog.eshop.service.payment;

import id.ac.ui.cs.advprog.eshop.enums.PaymentMethod;
import id.ac.ui.cs.advprog.eshop.enums.PaymentStatus;
import id.ac.ui.cs.advprog.eshop.model.Payment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BankTransferPaymentProcessor implements PaymentSubFeatureProcessor {
  private final BankTransferPaymentDataValidator bankTransferPaymentDataValidator;

  public BankTransferPaymentProcessor() {
    this(new DefaultBankTransferPaymentDataValidator());
  }

  @Autowired
  public BankTransferPaymentProcessor(BankTransferPaymentDataValidator bankTransferPaymentDataValidator) {
    this.bankTransferPaymentDataValidator = bankTransferPaymentDataValidator;
  }

  @Override
  public PaymentMethod getPaymentMethod() {
    return PaymentMethod.BANK_TRANSFER;
  }

  @Override
  public void process(Payment payment) {
    if (!bankTransferPaymentDataValidator.isValid(payment.getPaymentData())) {
      payment.setStatus(PaymentStatus.REJECTED.getValue());
    } else {
      payment.setStatus(PaymentStatus.SUCCESS.getValue());
    }
  }
}
