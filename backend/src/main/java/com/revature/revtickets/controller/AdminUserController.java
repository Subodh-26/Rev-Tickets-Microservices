package com.revature.revtickets.controller;

import com.revature.revtickets.entity.User;
import com.revature.revtickets.repository.UserRepository;
import com.revature.revtickets.response.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin(origins = "*")
public class AdminUserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<List<User>>> getAllUsers() {
        try {
            List<User> users = userRepository.findAll();
            return ResponseEntity.ok(new ApiResponse<>(true, "Users fetched successfully", users));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Failed to fetch users: " + e.getMessage(), null));
        }
    }
}
