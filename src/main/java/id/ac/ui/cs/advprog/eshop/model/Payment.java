package id.ac.ui.cs.advprog.eshop.model;

import java.util.Arrays;
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
    this.id = id;
    this.method = method;
    this.status = "PENDING";

    if (paymentData.isEmpty()) {
      throw new IllegalArgumentException();
    } else {
      this.paymentData = paymentData;
    }
  }

  public Payment(String id, String method, Map<String, String> paymentData, String status) {
    this(id, method, paymentData);

    String[] statusList = {"PENDING", "FAILED", "SUCCESS", "CANCELLED"};
    if (Arrays.stream(statusList).noneMatch(item -> item.equals(status))) {
      throw new IllegalArgumentException();
    } else {
      this.status = status;
    }
  }

  public void setStatus(String status) {
    String[] statusList = {"PENDING", "FAILED", "SUCCESS", "CANCELLED"};
    if (Arrays.stream(statusList).noneMatch(item -> item.equals(status))) {
      throw new IllegalArgumentException();
    } else {
      this.status = status;
    }
  }
}
