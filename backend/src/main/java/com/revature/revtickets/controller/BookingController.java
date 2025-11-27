package com.revature.revtickets.controller;

import com.revature.revtickets.dto.BookingRequest;
import com.revature.revtickets.dto.BookingResponse;
import com.revature.revtickets.entity.Booking;
import com.revature.revtickets.response.ApiResponse;
import com.revature.revtickets.service.BookingService;
import com.revature.revtickets.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "*")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private NotificationService notificationService;

    @PostMapping
    public ResponseEntity<ApiResponse<BookingResponse>> createBooking(
            @RequestBody BookingRequest request,
            Authentication authentication) {
        String userEmail = authentication.getName();
        BookingResponse response = bookingService.createBooking(request, userEmail);
        
        // Send notification (you'd get userId from the booking)
        // notificationService.sendNotification(userId, "Booking Created", "Your booking has been created successfully", "BOOKING");
        
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(new ApiResponse<>(true, "Booking created successfully", response));
    }

    @GetMapping("/my-bookings")
    public ResponseEntity<ApiResponse<List<Booking>>> getMyBookings(Authentication authentication) {
        String userEmail = authentication.getName();
        List<Booking> bookings = bookingService.getUserBookings(userEmail);
        return ResponseEntity.ok(new ApiResponse<>(true, "Bookings retrieved successfully", bookings));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Booking>> getBookingById(@PathVariable Long id) {
        Booking booking = bookingService.getBookingById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Booking retrieved successfully", booking));
    }

    @GetMapping("/reference/{reference}")
    public ResponseEntity<ApiResponse<Booking>> getBookingByReference(@PathVariable String reference) {
        Booking booking = bookingService.getBookingByReference(reference);
        return ResponseEntity.ok(new ApiResponse<>(true, "Booking retrieved successfully", booking));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> cancelBooking(
            @PathVariable Long id,
            Authentication authentication) {
        String userEmail = authentication.getName();
        bookingService.cancelBooking(id, userEmail);
        return ResponseEntity.ok(new ApiResponse<>(true, "Booking cancelled successfully", null));
    }
}
