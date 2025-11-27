package com.revature.revtickets.service;

// import com.revature.revtickets.dto.PaymentIntentRequest;
// import com.revature.revtickets.dto.PaymentIntentResponse;
import com.revature.revtickets.entity.Booking;
import com.revature.revtickets.entity.Payment;
import com.revature.revtickets.exception.BadRequestException;
import com.revature.revtickets.exception.ResourceNotFoundException;
import com.revature.revtickets.repository.BookingRepository;
import com.revature.revtickets.repository.PaymentRepository;
// import com.stripe.Stripe;
// import com.stripe.exception.StripeException;
// import com.stripe.model.PaymentIntent;
// import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private BookingService bookingService;

    // @Value("${stripe.api.key}")
    // private String stripeApiKey;

    // Stripe methods commented out - using Razorpay in PaymentController instead
    /*
    public PaymentIntentResponse createPaymentIntent(PaymentIntentRequest request) throws StripeException {
        Stripe.apiKey = stripeApiKey;

        Booking booking = bookingRepository.findById(request.getBookingId())
            .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        // Convert amount to cents (Stripe uses smallest currency unit)
        long amountInCents = booking.getTotalAmount().multiply(java.math.BigDecimal.valueOf(100)).longValue();

        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
            .setAmount(amountInCents)
            .setCurrency("usd")
            .putMetadata("bookingId", booking.getBookingId().toString())
            .putMetadata("bookingReference", booking.getBookingReference())
            .build();

        PaymentIntent intent = PaymentIntent.create(params);

        PaymentIntentResponse response = new PaymentIntentResponse();
        response.setClientSecret(intent.getClientSecret());
        response.setPaymentIntentId(intent.getId());

        return response;
    }

    public Payment confirmPayment(String paymentIntentId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        // Create payment record
        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setPaymentMethod(Payment.PaymentMethod.CARD);
        payment.setTransactionId(paymentIntentId);
        payment.setAmount(booking.getTotalAmount());
        payment.setPaymentStatus(Payment.PaymentStatus.SUCCESS);

        payment = paymentRepository.save(payment);

        // Confirm booking
        bookingService.confirmBooking(bookingId);

        return payment;
    }
    */

    public Payment createPayment(Payment payment) {
        return paymentRepository.save(payment);
    }

    public Payment getPaymentById(Long id) {
        return paymentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + id));
    }

    public List<Payment> getPaymentsByBookingId(Long bookingId) {
        return paymentRepository.findByBookingBookingId(bookingId);
    }

    public Payment getPaymentByTransactionId(String transactionId) {
        return paymentRepository.findByTransactionId(transactionId)
            .orElseThrow(() -> new ResourceNotFoundException("Payment not found with transaction id: " + transactionId));
    }

    public void processRefund(Long paymentId) {
        Payment payment = getPaymentById(paymentId);
        
        if (payment.getPaymentStatus() != Payment.PaymentStatus.SUCCESS) {
            throw new BadRequestException("Payment is not in completed status");
        }

        payment.setPaymentStatus(Payment.PaymentStatus.REFUNDED);
        paymentRepository.save(payment);
    }
}
