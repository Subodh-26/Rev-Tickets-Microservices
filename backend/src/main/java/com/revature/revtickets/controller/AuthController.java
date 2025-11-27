package com.revature.revtickets.controller;

import com.revature.revtickets.dto.ApiResponse;
import com.revature.revtickets.dto.AuthResponse;
import com.revature.revtickets.dto.LoginRequest;
import com.revature.revtickets.dto.RegisterRequest;
import com.revature.revtickets.entity.User;
import com.revature.revtickets.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest request) {
        try {
            AuthResponse authData = authService.registerUser(request);
            
            // Create a wrapper response matching frontend expectations
            java.util.Map<String, Object> user = new java.util.HashMap<>();
            user.put("id", authData.getUserId());
            user.put("email", authData.getEmail());
            user.put("name", authData.getFullName());
            user.put("role", authData.getRole());
            
            java.util.Map<String, Object> data = new java.util.HashMap<>();
            data.put("token", authData.getToken());
            data.put("user", user);
            
            java.util.Map<String, Object> response = new java.util.HashMap<>();
            response.put("success", true);
            response.put("message", "Registration successful");
            response.put("data", data);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody LoginRequest request) {
        try {
            AuthResponse authData = authService.loginUser(request);
            
            // Create a wrapper response matching frontend expectations
            java.util.Map<String, Object> user = new java.util.HashMap<>();
            user.put("id", authData.getUserId());
            user.put("email", authData.getEmail());
            user.put("name", authData.getFullName());
            user.put("role", authData.getRole());
            
            java.util.Map<String, Object> data = new java.util.HashMap<>();
            data.put("token", authData.getToken());
            data.put("user", user);
            
            java.util.Map<String, Object> response = new java.util.HashMap<>();
            response.put("success", true);
            response.put("message", "Login successful");
            response.put("data", data);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            java.util.Map<String, Object> errorResponse = new java.util.HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Invalid email or password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getCurrentUser() {
        try {
            User user = authService.getCurrentUser();
            user.setPassword(null); // Don't send password
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse(false, e.getMessage()));
        }
    }
}
