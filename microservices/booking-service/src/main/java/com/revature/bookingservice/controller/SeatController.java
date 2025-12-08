package com.revature.bookingservice.controller;

import com.revature.bookingservice.dto.ApiResponse;
import com.revature.bookingservice.entity.Seat;
import com.revature.bookingservice.service.SeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seats")
public class SeatController {

    @Autowired
    private SeatService seatService;

    @GetMapping("/show/{showId}")
    public ResponseEntity<ApiResponse<List<Seat>>> getSeatsByShow(@PathVariable Long showId) {
        try {
            List<Seat> seats = seatService.getSeatsByShowId(showId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Seats retrieved successfully", seats));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @GetMapping("/available/show/{showId}")
    public ResponseEntity<ApiResponse<List<Seat>>> getAvailableSeats(@PathVariable Long showId) {
        try {
            List<Seat> seats = seatService.getAvailableSeatsByShowId(showId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Available seats retrieved", seats));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }
}
