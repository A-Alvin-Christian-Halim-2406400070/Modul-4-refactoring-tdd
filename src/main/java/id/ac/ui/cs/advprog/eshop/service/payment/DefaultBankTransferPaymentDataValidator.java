package id.ac.ui.cs.advprog.eshop.service.payment;

import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class DefaultBankTransferPaymentDataValidator implements BankTransferPaymentDataValidator {
  private static final String BANK_NAME_KEY = "bankName";
  private static final String REFERENCE_CODE_KEY = "referenceCode";

  @Override
  public boolean isValid(Map<String, String> paymentData) {
    String bankName = paymentData.get(BANK_NAME_KEY);
    String referenceCode = paymentData.get(REFERENCE_CODE_KEY);
    return !isNullOrEmpty(bankName) && !isNullOrEmpty(referenceCode);
  }

  private boolean isNullOrEmpty(String value) {
    return value == null || value.isEmpty();
  }
}
