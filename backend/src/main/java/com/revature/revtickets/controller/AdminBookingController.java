package com.revature.revtickets.controller;

import com.revature.revtickets.entity.Booking;
import com.revature.revtickets.repository.BookingRepository;
import com.revature.revtickets.response.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Sort;

import java.util.List;

@RestController
@RequestMapping("/api/admin/bookings")
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin(origins = "*")
public class AdminBookingController {

    @Autowired
    private BookingRepository bookingRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Booking>>> getAllBookings() {
        try {
            // Fetch all bookings sorted by booking date descending (newest first)
            List<Booking> bookings = bookingRepository.findAll(Sort.by(Sort.Direction.DESC, "bookingDate"));
            System.out.println("Admin: Fetched " + bookings.size() + " bookings");
            return ResponseEntity.ok(new ApiResponse<>(true, "Bookings fetched successfully", bookings));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Failed to fetch bookings: " + e.getMessage(), null));
        }
    }
}
