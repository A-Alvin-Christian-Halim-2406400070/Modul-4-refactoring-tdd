package id.ac.ui.cs.advprog.eshop.model;

import id.ac.ui.cs.advprog.eshop.enums.PaymentMethod;
import id.ac.ui.cs.advprog.eshop.enums.PaymentStatus;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Payment {
  String id;
  String method;
  Map<String, String> paymentData;
  String status;

  public Payment(String id, String method, Map<String, String> paymentData) {
    if (!PaymentMethod.contains(method)) {
      throw new IllegalArgumentException();
    }

    this.id = id;
    this.method = method;
    this.status = PaymentStatus.PENDING.getValue();

    if (paymentData.isEmpty()) {
      throw new IllegalArgumentException();
    } else {
      this.paymentData = paymentData;
    }
  }

  public Payment(String id, String method, Map<String, String> paymentData, String status) {
    this(id, method, paymentData);
    this.setStatus(status);
  }

  public void setStatus(String status) {
    if (PaymentStatus.contains(status)) {
      this.status = status;
    } else {
      throw new IllegalArgumentException();
    }
  }
}
