package id.ac.ui.cs.advprog.eshop.service.payment;

import java.util.Map;

public interface BankTransferPaymentDataValidator {
  boolean isValid(Map<String, String> paymentData);
}
