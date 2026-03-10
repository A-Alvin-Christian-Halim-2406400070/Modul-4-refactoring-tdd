package id.ac.ui.cs.advprog.eshop.controller;

import id.ac.ui.cs.advprog.eshop.model.Order;
import id.ac.ui.cs.advprog.eshop.model.Payment;
import id.ac.ui.cs.advprog.eshop.model.Product;
import id.ac.ui.cs.advprog.eshop.service.OrderService;
import id.ac.ui.cs.advprog.eshop.service.PaymentService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/order")
public class OrderController {
  private static final String VIEW_CREATE_ORDER = "CreateOrder";
  private static final String VIEW_ORDER_HISTORY = "OrderHistory";
  private static final String VIEW_ORDER_LIST = "OrderList";
  private static final String VIEW_ORDER_PAY = "OrderPay";
  private static final String VIEW_ORDER_PAY_RESULT = "OrderPayResult";

  @Autowired
  private OrderService orderService;

  @Autowired
  private PaymentService paymentService;

  @GetMapping("/create")
  public String createOrderPage() {
    return VIEW_CREATE_ORDER;
  }

  @PostMapping("/create")
  public String createOrderPost(@RequestParam("author") String author, Model model) {
    if (author == null || author.trim().isEmpty()) {
      model.addAttribute("errorMessage", "Author name is required");
      return VIEW_CREATE_ORDER;
    }

    Product product = new Product();
    product.setProductName("Auto-generated Item");
    product.setProductQuantity(1);

    List<Product> products = new ArrayList<>();
    products.add(product);

    Order order = new Order(
        UUID.randomUUID().toString(),
        products,
        System.currentTimeMillis(),
        author.trim()
    );

    Order createdOrder = orderService.createOrder(order);
    if (createdOrder == null) {
      model.addAttribute("errorMessage", "Failed to create order");
      return VIEW_CREATE_ORDER;
    }
    return VIEW_CREATE_ORDER;
  }

  @GetMapping("/history")
  public String orderHistoryPage() {
    return VIEW_ORDER_HISTORY;
  }

  @PostMapping("/history")
  public String orderHistoryPost(@RequestParam("author") String author, Model model) {
    String normalizedAuthor = author == null ? "" : author.trim();
    List<Order> orders = orderService.findAllByAuthor(normalizedAuthor);
    model.addAttribute("orders", orders);
    return VIEW_ORDER_LIST;
  }

  @GetMapping("/pay/{orderId}")
  public String payOrderPage(@PathVariable String orderId, Model model) {
    Order order = orderService.findById(orderId);
    model.addAttribute("order", order);
    model.addAttribute("orderId", orderId);
    return VIEW_ORDER_PAY;
  }

  @PostMapping("/pay/{orderId}")
  public String payOrderPost(@PathVariable String orderId,
      @RequestParam("method") String method,
      @RequestParam(value = "bankName", required = false) String bankName,
      @RequestParam(value = "referenceCode", required = false) String referenceCode,
      @RequestParam(value = "voucherCode", required = false) String voucherCode,
      Model model) {
    Order order = orderService.findById(orderId);
    if (order == null) {
      model.addAttribute("paymentId", orderId);
      return VIEW_ORDER_PAY_RESULT;
    }

    Map<String, String> paymentData = new HashMap<>();
    if ("BANK_TRANSFER".equals(method)) {
      paymentData.put("bankName", bankName);
      paymentData.put("referenceCode", referenceCode);
    } else if ("VOUCHER_CODE".equals(method)) {
      paymentData.put("voucherCode", voucherCode);
    }

    Payment payment = paymentService.addPayment(order, method, paymentData);
    model.addAttribute("payment", payment);
    model.addAttribute("paymentId", payment.getId());
    return VIEW_ORDER_PAY_RESULT;
  }
}
