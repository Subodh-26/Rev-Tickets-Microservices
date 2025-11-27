package com.revature.revtickets.controller;

import com.revature.revtickets.entity.OpenEventShow;
import com.revature.revtickets.response.ApiResponse;
import com.revature.revtickets.service.OpenEventShowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/open-event-shows")
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin(origins = "*")
public class AdminOpenEventShowController {

    @Autowired
    private OpenEventShowService openEventShowService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OpenEventShow>> getOpenEventShow(@PathVariable Long id) {
        try {
            OpenEventShow show = openEventShowService.getShowById(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Open event show fetched successfully", show));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Failed to fetch open event show: " + e.getMessage(), null));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteOpenEventShow(@PathVariable Long id) {
        try {
            openEventShowService.deleteShow(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Open event show deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Failed to delete open event show: " + e.getMessage(), null));
        }
    }
}
