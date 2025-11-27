package com.revature.revtickets.controller;

import com.revature.revtickets.repository.MovieRepository;
import com.revature.revtickets.repository.UserRepository;
import com.revature.revtickets.repository.BookingRepository;
import com.revature.revtickets.response.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/dashboard")
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin(origins = "*")
public class AdminDashboardController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDashboardStats() {
        try {
            Map<String, Object> stats = new HashMap<>();
            
            // Count active users
            stats.put("totalUsers", userRepository.count());
            
            // Count active movies
            stats.put("totalMovies", movieRepository.findByIsActiveTrue().size());
            
            // Count bookings
            long totalBookings = bookingRepository.count();
            stats.put("totalBookings", totalBookings);
            
            // Calculate total revenue from CONFIRMED bookings only - sum of total_amount column
            double totalRevenue = bookingRepository.findAll().stream()
                .filter(booking -> booking.getBookingStatus() == com.revature.revtickets.entity.Booking.BookingStatus.CONFIRMED)
                .mapToDouble(booking -> booking.getTotalAmount() != null ? booking.getTotalAmount().doubleValue() : 0.0)
                .sum();
            stats.put("totalRevenue", Math.round(totalRevenue * 100.0) / 100.0); // Round to 2 decimal places
            
            // Bookings by status
            Map<String, Long> bookingsByStatus = new HashMap<>();
            bookingsByStatus.put("confirmed", bookingRepository.countByBookingStatus(com.revature.revtickets.entity.Booking.BookingStatus.CONFIRMED));
            bookingsByStatus.put("pending", bookingRepository.countByBookingStatus(com.revature.revtickets.entity.Booking.BookingStatus.PENDING));
            bookingsByStatus.put("cancelled", bookingRepository.countByBookingStatus(com.revature.revtickets.entity.Booking.BookingStatus.CANCELLED));
            stats.put("bookingsByStatus", bookingsByStatus);
            
            return ResponseEntity.ok(new ApiResponse<>(true, "Stats fetched successfully", stats));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Failed to fetch stats: " + e.getMessage(), null));
        }
    }
}
