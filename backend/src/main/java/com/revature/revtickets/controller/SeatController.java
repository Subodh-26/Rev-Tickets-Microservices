package com.revature.revtickets.controller;

import com.revature.revtickets.dto.SeatDTO;
import com.revature.revtickets.entity.Seat;
import com.revature.revtickets.response.ApiResponse;
import com.revature.revtickets.service.SeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seats")
@CrossOrigin(origins = "*")
public class SeatController {

    @Autowired
    private SeatService seatService;

    @GetMapping("/show/{showId}")
    public ResponseEntity<ApiResponse<List<SeatDTO>>> getSeatsByShow(@PathVariable Long showId) {
        List<SeatDTO> seats = seatService.getSeatsByShowId(showId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Seats retrieved successfully", seats));
    }

    @GetMapping("/available/show/{showId}")
    public ResponseEntity<ApiResponse<List<Seat>>> getAvailableSeats(@PathVariable Long showId) {
        List<Seat> seats = seatService.getAvailableSeatsByShowId(showId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Available seats retrieved", seats));
    }
}
