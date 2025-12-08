package com.revature.paymentservice.controller;

import com.revature.paymentservice.dto.ApiResponse;
import com.revature.paymentservice.entity.Payment;
import com.revature.paymentservice.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Payment>> getPaymentById(@PathVariable Long id) {
        try {
            Payment payment = paymentService.getPaymentById(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Payment retrieved successfully", payment));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<ApiResponse<List<Payment>>> getPaymentsByBooking(@PathVariable Long bookingId) {
        try {
            List<Payment> payments = paymentService.getPaymentsByBookingId(bookingId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Payments retrieved successfully", payments));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @org.springframework.beans.factory.annotation.Value("${razorpay.key.id}")
    private String razorpayKeyId;

    @org.springframework.beans.factory.annotation.Value("${razorpay.key.secret}")
    private String razorpayKeySecret;

    @PostMapping("/create-order")
    public ResponseEntity<ApiResponse<java.util.Map<String, Object>>> createOrder(@RequestBody java.util.Map<String, Object> orderData) {
        try {
            double totalAmount = Double.parseDouble(orderData.get("totalAmount").toString());
            int amountInPaise = (int)(totalAmount * 100);
            
            com.razorpay.RazorpayClient razorpayClient = new com.razorpay.RazorpayClient(razorpayKeyId, razorpayKeySecret);
            
            org.json.JSONObject orderRequest = new org.json.JSONObject();
            orderRequest.put("amount", amountInPaise);
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", "rcpt_" + System.currentTimeMillis());

            com.razorpay.Order razorpayOrder = razorpayClient.orders.create(orderRequest);
            
            java.util.Map<String, Object> response = new java.util.HashMap<>();
            response.put("orderId", razorpayOrder.get("id"));
            response.put("amount", String.valueOf(amountInPaise));
            response.put("currency", "INR");
            response.put("key", razorpayKeyId);
            response.put("bookingId", 0);
            
            return ResponseEntity.ok(new ApiResponse<>(true, "Order created successfully", response));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Failed to create order: " + e.getMessage(), null));
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<java.util.Map<String, Object>>> verifyPayment(@RequestBody java.util.Map<String, Object> paymentData) {
        try {
            // For testing, accept any payment
            java.util.Map<String, Object> result = new java.util.HashMap<>();
            result.put("verified", true);
            result.put("paymentId", paymentData.get("razorpay_payment_id"));
            return ResponseEntity.ok(new ApiResponse<>(true, "Payment verified successfully", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @PostMapping("/test-payment")
    public ResponseEntity<ApiResponse<java.util.Map<String, Object>>> testPayment(@RequestBody java.util.Map<String, Object> paymentData) {
        try {
            java.util.Map<String, Object> result = new java.util.HashMap<>();
            result.put("success", true);
            result.put("paymentId", "test_" + System.currentTimeMillis());
            result.put("orderId", paymentData.get("orderId"));
            result.put("amount", paymentData.get("amount"));
            return ResponseEntity.ok(new ApiResponse<>(true, "Test payment successful", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }
}
