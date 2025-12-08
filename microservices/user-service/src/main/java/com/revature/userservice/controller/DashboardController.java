package com.revature.userservice.controller;

import com.revature.userservice.dto.ApiResponse;
import com.revature.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.ServiceInstance;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/admin/dashboard")
public class DashboardController {

    @Autowired
    private UserService userService;

    @Autowired
    private DiscoveryClient discoveryClient;

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDashboardStats() {
        try {
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalUsers", userService.getTotalUsers());
            System.out.println("Total users fetched: " + stats.get("totalUsers"));
            
            // Try to get movie count from movie service
            try {
                ResponseEntity<Map> movieResponse = restTemplate.getForEntity(
                    "http://localhost:8082/api/movies/admin/count", Map.class);
                    if (movieResponse.getBody() != null && movieResponse.getBody().get("data") != null) {
                        stats.put("totalMovies", movieResponse.getBody().get("data"));
                    } else {
                        stats.put("totalMovies", 0);
                    }

            } catch (Exception e) {
                stats.put("totalMovies", 0);
            }
            
            // Get booking stats from booking service
            try {
                ResponseEntity<Map> bookingResponse = restTemplate.getForEntity(
                    "http://localhost:8084/api/admin/bookings/stats", Map.class);
                    if (bookingResponse.getBody() != null && bookingResponse.getBody().get("data") != null) {
                        Map<String, Object> bookingStats = (Map<String, Object>) bookingResponse.getBody().get("data");
                        stats.put("totalBookings", bookingStats.get("totalBookings"));
                        stats.put("totalRevenue", bookingStats.get("totalRevenue"));
                        stats.put("cancelledPayments", bookingStats.get("cancelledPayments"));
                        stats.put("bookingsByStatus", bookingStats.get("bookingsByStatus"));
                    } else {
                        stats.put("totalBookings", 0);
                        stats.put("totalRevenue", 0);
                        Map<String, Integer> bookingsByStatus = new HashMap<>();
                        bookingsByStatus.put("confirmed", 0);
                        bookingsByStatus.put("pending", 0);
                        bookingsByStatus.put("cancelled", 0);
                        stats.put("bookingsByStatus", bookingsByStatus);
                    }

            } catch (Exception e) {
                System.err.println("Error fetching booking stats: " + e.getMessage());
                e.printStackTrace();
                stats.put("totalBookings", 0);
                stats.put("totalRevenue", 0);
                Map<String, Integer> bookingsByStatus = new HashMap<>();
                bookingsByStatus.put("confirmed", 0);
                bookingsByStatus.put("pending", 0);
                bookingsByStatus.put("cancelled", 0);
                stats.put("bookingsByStatus", bookingsByStatus);
            }
            
            // Get venue count from venue service
            try {
                ResponseEntity<Map> venueResponse = restTemplate.getForEntity(
                    "http://localhost:8083/api/venues/admin", Map.class);
                    if (venueResponse.getBody() != null && venueResponse.getBody().get("data") != null) {
                        List<?> venues = (List<?>) venueResponse.getBody().get("data");
                        stats.put("totalVenues", venues.size());
                    } else {
                        stats.put("totalVenues", 0);
                    }

            } catch (Exception e) {
                stats.put("totalVenues", 0);
            }
            
            return ResponseEntity.ok(new ApiResponse<>(true, "Dashboard stats fetched successfully", stats));
        } catch (Exception e) {
            System.err.println("Error in getDashboardStats: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(new ApiResponse<>(false, "Error fetching dashboard stats: " + e.getMessage(), null));
        }
    }
}