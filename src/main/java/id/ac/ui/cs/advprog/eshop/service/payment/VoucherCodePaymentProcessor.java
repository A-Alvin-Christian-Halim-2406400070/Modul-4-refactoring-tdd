package id.ac.ui.cs.advprog.eshop.service.payment;

import id.ac.ui.cs.advprog.eshop.enums.PaymentMethod;
import id.ac.ui.cs.advprog.eshop.enums.PaymentStatus;
import id.ac.ui.cs.advprog.eshop.model.Payment;
import org.springframework.stereotype.Component;

@Component
public class VoucherCodePaymentProcessor implements PaymentSubFeatureProcessor {
  private static final String VOUCHER_KEY = "voucherCode";
  private static final String PREFIX = "ESHOP";
  private static final int TOTAL_LENGTH = 16;
  private static final long NUMERIC_COUNT = 8;

  @Override
  public PaymentMethod getPaymentMethod() {
    return PaymentMethod.VOUCHER_CODE;
  }

  @Override
  public void process(Payment payment) {
    String voucherCode = payment.getPaymentData().get(VOUCHER_KEY);
    if (isValidVoucherCode(voucherCode)) {
      payment.setStatus(PaymentStatus.SUCCESS.getValue());
    } else {
      payment.setStatus(PaymentStatus.REJECTED.getValue());
    }
  }

  private boolean isValidVoucherCode(String voucherCode) {
    if (voucherCode == null || voucherCode.length() != TOTAL_LENGTH || !voucherCode.startsWith(PREFIX)) {
      return false;
    }

    long numericCount = voucherCode.chars().filter(Character::isDigit).count();
    return numericCount == NUMERIC_COUNT;
  }
}
