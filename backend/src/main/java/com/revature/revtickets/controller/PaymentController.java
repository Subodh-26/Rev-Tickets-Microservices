package com.revature.revtickets.controller;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import com.revature.revtickets.dto.OrderResponse;
import com.revature.revtickets.dto.PaymentRequest;
import com.revature.revtickets.dto.PaymentVerifyRequest;
import com.revature.revtickets.entity.Booking;
import com.revature.revtickets.entity.OpenEventShow;
import com.revature.revtickets.entity.Show;
import com.revature.revtickets.entity.User;
import com.revature.revtickets.repository.BookingRepository;
import com.revature.revtickets.repository.BookingSeatRepository;
import com.revature.revtickets.repository.OpenEventShowRepository;
import com.revature.revtickets.repository.ShowRepository;
import com.revature.revtickets.repository.UserRepository;
import com.revature.revtickets.response.ApiResponse;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*")
public class PaymentController {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ShowRepository showRepository;

    @Autowired
    private OpenEventShowRepository openEventShowRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingSeatRepository bookingSeatRepository;

    @Autowired
    private com.revature.revtickets.repository.SeatRepository seatRepository;

    @Value("${razorpay.key.id}")
    private String razorpayKeyId;

    @Value("${razorpay.key.secret}")
    private String razorpayKeySecret;

    @PostMapping("/create-order")
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(
            @RequestBody PaymentRequest request,
            Authentication authentication) {
        try {
            // Get current user
            String email = authentication.getName();
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Create booking
            Booking booking = new Booking();
            booking.setUser(user);
            booking.setTotalAmount(request.getTotalAmount());
            booking.setBookingReference(generateBookingReference());
            booking.setBookingStatus(Booking.BookingStatus.PENDING);
            booking.setPaymentStatus("PENDING");

            if (request.getIsOpenEvent() != null && request.getIsOpenEvent()) {
                // Open event booking
                OpenEventShow openShow = openEventShowRepository.findById(request.getOpenShowId())
                        .orElseThrow(() -> new RuntimeException("Open event show not found"));
                booking.setOpenEventShow(openShow);
                booking.setTotalSeats(request.getZoneBookings().stream()
                        .mapToInt(PaymentRequest.ZoneBookingDTO::getQuantity).sum());
                
                // Convert zone bookings
                List<Booking.ZoneBooking> zoneBookings = request.getZoneBookings().stream()
                        .map(dto -> new Booking.ZoneBooking(
                                dto.getZoneName(),
                                dto.getQuantity(),
                                dto.getPricePerTicket()
                        ))
                        .collect(Collectors.toList());
                booking.setZoneBookings(zoneBookings);
            } else {
                // Regular show booking
                Show show = showRepository.findById(request.getShowId())
                        .orElseThrow(() -> new RuntimeException("Show not found"));
                booking.setShow(show);
                booking.setTotalSeats(request.getSeatNumbers().size());
            }

            // Save booking first
            booking = bookingRepository.save(booking);

            // For regular shows, create BookingSeat entries
            if (!(request.getIsOpenEvent() != null && request.getIsOpenEvent())) {
                // request.getSeatNumbers() contains values like "A2", "B10"
                for (String seatLabel : request.getSeatNumbers()) {
                    if (seatLabel == null || seatLabel.trim().isEmpty()) continue;
                    // parse row (letters) and number (digits)
                    String rowLabel = seatLabel.replaceAll("\\d", "").trim();
                    String numberPart = seatLabel.replaceAll("\\D", "").trim();
                    if (rowLabel.isEmpty() || numberPart.isEmpty()) {
                        throw new RuntimeException("Invalid seat label: " + seatLabel);
                    }
                    Integer seatNumber = Integer.parseInt(numberPart);
                    // find seat entity
                    com.revature.revtickets.entity.Seat seat = seatRepository.findByShowShowIdAndRowLabelAndSeatNumber(booking.getShow().getShowId(), rowLabel, seatNumber);
                    if (seat == null) {
                        throw new RuntimeException("Seat not found: " + seatLabel);
                    }

                    // create booking seat
                    com.revature.revtickets.entity.BookingSeat bs = new com.revature.revtickets.entity.BookingSeat();
                    bs.setBooking(booking);
                    bs.setSeat(seat);
                    bs.setSeatPrice(seat.getPrice());
                    bookingSeatRepository.save(bs);

                    // mark seat unavailable (reserve)
                    seat.setIsAvailable(false);
                    seatRepository.save(seat);
                }
            }

            // Create Razorpay order
            RazorpayClient razorpayClient = new RazorpayClient(razorpayKeyId, razorpayKeySecret);
            
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", request.getTotalAmount().multiply(new BigDecimal("100")).intValue()); // Amount in paise
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", booking.getBookingReference());

            Order order = razorpayClient.orders.create(orderRequest);
            
            // Update booking with order ID
            booking.setRazorpayOrderId(order.get("id"));
            bookingRepository.save(booking);

            // Return response
            OrderResponse response = new OrderResponse(
                    order.get("id"),
                    booking.getBookingId(),
                    order.get("amount").toString(),
                    order.get("currency"),
                    razorpayKeyId
            );

            return ResponseEntity.ok(new ApiResponse<>(true, "Order created successfully", response));

        } catch (RazorpayException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Failed to create order: " + e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Error: " + e.getMessage(), null));
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<Booking>> verifyPayment(@RequestBody PaymentVerifyRequest request) {
        try {
            // Verify signature
            JSONObject options = new JSONObject();
            options.put("razorpay_order_id", request.getRazorpayOrderId());
            options.put("razorpay_payment_id", request.getRazorpayPaymentId());
            options.put("razorpay_signature", request.getRazorpaySignature());

            boolean isValidSignature = Utils.verifyPaymentSignature(options, razorpayKeySecret);

            if (!isValidSignature) {
                return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Invalid payment signature", null));
            }

            // Update booking
            Booking booking = bookingRepository.findById(request.getBookingId())
                    .orElseThrow(() -> new RuntimeException("Booking not found"));

            booking.setRazorpayPaymentId(request.getRazorpayPaymentId());
            booking.setRazorpaySignature(request.getRazorpaySignature());
            booking.setPaymentStatus("PAID");
            booking.setBookingStatus(Booking.BookingStatus.CONFIRMED);

            // Update show capacity
            if (booking.getShow() != null) {
                Show show = booking.getShow();
                show.setAvailableSeats(show.getAvailableSeats() - booking.getTotalSeats());
                showRepository.save(show);
            } else if (booking.getOpenEventShow() != null) {
                OpenEventShow openShow = booking.getOpenEventShow();
                int totalTickets = booking.getZoneBookings().stream()
                        .mapToInt(Booking.ZoneBooking::getQuantity).sum();
                openShow.setAvailableCapacity(openShow.getAvailableCapacity() - totalTickets);
                openEventShowRepository.save(openShow);
            }

            booking = bookingRepository.save(booking);

            return ResponseEntity.ok(new ApiResponse<>(true, "Payment verified successfully", booking));

        } catch (RazorpayException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Payment verification failed: " + e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Error: " + e.getMessage(), null));
        }
    }

    @PostMapping("/cancel/{bookingId}")
    public ResponseEntity<ApiResponse<Booking>> cancelPayment(@PathVariable Long bookingId) {
        try {
            Booking booking = bookingRepository.findById(bookingId)
                    .orElseThrow(() -> new RuntimeException("Booking not found"));
            
            // Only cancel if payment is still pending
            if ("PENDING".equals(booking.getPaymentStatus())) {
                booking.setPaymentStatus("FAILED");
                booking.setBookingStatus(Booking.BookingStatus.CANCELLED);
                booking = bookingRepository.save(booking);
                return ResponseEntity.ok(new ApiResponse<>(true, "Payment cancelled successfully", booking));
            } else {
                return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Cannot cancel payment. Current status: " + booking.getPaymentStatus(), null));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Error: " + e.getMessage(), null));
        }
    }

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<ApiResponse<Booking>> getBooking(@PathVariable Long bookingId) {
        try {
            Booking booking = bookingRepository.findById(bookingId)
                    .orElseThrow(() -> new RuntimeException("Booking not found"));
            return ResponseEntity.ok(new ApiResponse<>(true, "Booking fetched successfully", booking));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Error: " + e.getMessage(), null));
        }
    }

    private String generateBookingReference() {
        return "BK" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }
}
