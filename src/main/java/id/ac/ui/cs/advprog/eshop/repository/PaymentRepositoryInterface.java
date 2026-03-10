package id.ac.ui.cs.advprog.eshop.repository;

import id.ac.ui.cs.advprog.eshop.model.Payment;
import java.util.List;

public interface PaymentRepositoryInterface {
  Payment add(Payment payment);
  Payment findById(String id);
  List<Payment> findAll();
}
