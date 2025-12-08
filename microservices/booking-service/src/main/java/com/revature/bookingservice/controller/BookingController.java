package com.revature.bookingservice.controller;

import com.revature.bookingservice.dto.ApiResponse;
import com.revature.bookingservice.entity.Booking;
import com.revature.bookingservice.entity.Show;
import com.revature.bookingservice.service.BookingService;
import com.revature.bookingservice.service.ShowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class BookingController {

    @Autowired
    private BookingService bookingService;
    
    @Autowired
    private ShowService showService;

    @GetMapping("/bookings")
    public ResponseEntity<ApiResponse<List<java.util.Map<String, Object>>>> getUserBookings(@RequestParam Long userId) {
        try {
            List<java.util.Map<String, Object>> bookings = bookingService.getUserBookings(userId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Bookings retrieved successfully", bookings));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @GetMapping("/admin/bookings")
    public ResponseEntity<ApiResponse<List<java.util.Map<String, Object>>>> getAllBookings() {
        try {
            List<java.util.Map<String, Object>> bookings = bookingService.getAllBookings();
            return ResponseEntity.ok(new ApiResponse<>(true, "Bookings fetched successfully", bookings));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @GetMapping("/admin/shows")
    public ResponseEntity<ApiResponse<List<java.util.Map<String, Object>>>> getAllShows() {
        try {
            List<java.util.Map<String, Object>> shows = showService.getAllShowsWithDetails();
            return ResponseEntity.ok(new ApiResponse<>(true, "Shows fetched successfully", shows));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @PostMapping("/admin/shows")
    public ResponseEntity<ApiResponse<Show>> createShow(@RequestBody Map<String, Object> showData) {
        try {
            Show show = showService.createShowFromData(showData);
            return ResponseEntity.ok(new ApiResponse<>(true, "Show created successfully", show));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @GetMapping("/bookings/my-bookings")
    public ResponseEntity<ApiResponse<List<java.util.Map<String, Object>>>> getMyBookings(@RequestParam Long userId) {
        try {
            List<java.util.Map<String, Object>> bookings = bookingService.getUserBookings(userId);
            return ResponseEntity.ok(new ApiResponse<>(true, "My bookings retrieved successfully", bookings));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @GetMapping("/shows")
    public ResponseEntity<ApiResponse<List<Show>>> getActiveShows() {
        try {
            List<Show> shows = showService.getAllActiveShows();
            return ResponseEntity.ok(new ApiResponse<>(true, "Active shows retrieved successfully", shows));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @GetMapping("/shows/{id}")
    public ResponseEntity<ApiResponse<Show>> getShowById(@PathVariable Long id) {
        try {
            Show show = showService.getShowById(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Show retrieved successfully", show));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @GetMapping("/shows/movie/{movieId}/dates")
    public ResponseEntity<ApiResponse<List<String>>> getShowDatesByMovie(@PathVariable Long movieId) {
        try {
            List<String> dates = showService.getShowDatesByMovieId(movieId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Show dates retrieved successfully", dates));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @GetMapping("/shows/movie/{movieId}")
    public ResponseEntity<ApiResponse<List<Show>>> getShowsByMovieAndDate(@PathVariable Long movieId, @RequestParam String date) {
        try {
            List<Show> shows = showService.getShowsByMovieIdAndDate(movieId, date);
            return ResponseEntity.ok(new ApiResponse<>(true, "Shows retrieved successfully", shows));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @GetMapping("/shows/event/{eventId}/dates")
    public ResponseEntity<ApiResponse<List<String>>> getShowDatesByEvent(@PathVariable Long eventId) {
        try {
            List<String> dates = showService.getShowDatesByEventId(eventId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Show dates retrieved successfully", dates));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @GetMapping("/shows/event/{eventId}")
    public ResponseEntity<ApiResponse<List<Show>>> getShowsByEventAndDate(@PathVariable Long eventId, @RequestParam String date) {
        try {
            List<Show> shows = showService.getShowsByEventIdAndDate(eventId, date);
            return ResponseEntity.ok(new ApiResponse<>(true, "Shows retrieved successfully", shows));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @PutMapping("/shows/{id}/soft-delete")
    public ResponseEntity<ApiResponse<Void>> deleteShow(@PathVariable Long id) {
        try {
            showService.deleteShow(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Show deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @PutMapping("/shows/{id}/activate")
    public ResponseEntity<ApiResponse<Show>> activateShow(@PathVariable Long id) {
        try {
            Show show = showService.activateShow(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Show activated successfully", show));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @PostMapping("/admin/shows/{id}/generate-seats")
    public ResponseEntity<ApiResponse<Void>> generateSeats(@PathVariable Long id) {
        try {
            showService.generateSeatsForShow(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Seats generated successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @PostMapping("/bookings")
    public ResponseEntity<ApiResponse<Booking>> createBooking(@RequestBody Map<String, Object> bookingData) {
        try {
            Booking booking = bookingService.createBooking(bookingData);
            return ResponseEntity.ok(new ApiResponse<>(true, "Booking created successfully", booking));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @GetMapping("/admin/bookings/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getBookingStats() {
        try {
            Map<String, Object> stats = bookingService.getBookingStats();
            return ResponseEntity.ok(new ApiResponse<>(true, "Booking stats retrieved successfully", stats));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }
}
