package com.revature.revtickets.service;

import com.revature.revtickets.dto.AuthResponse;
import com.revature.revtickets.dto.LoginRequest;
import com.revature.revtickets.dto.RegisterRequest;
import com.revature.revtickets.entity.User;
import com.revature.revtickets.repository.UserRepository;
import com.revature.revtickets.security.CustomUserDetails;
import com.revature.revtickets.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    public AuthResponse registerUser(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email is already registered");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setRole(User.UserRole.CUSTOMER);
        user.setIsActive(true);

        User savedUser = userRepository.save(user);

        String token = tokenProvider.generateTokenFromEmail(savedUser.getEmail());

        return new AuthResponse(
            token,
            savedUser.getUserId(),
            savedUser.getEmail(),
            savedUser.getFullName(),
            savedUser.getRole().name()
        );
    }

    public AuthResponse loginUser(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = tokenProvider.generateToken(authentication);
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        return new AuthResponse(
            token,
            userDetails.getUserId(),
            userDetails.getEmail(),
            userDetails.getFullName(),
            userDetails.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "")
        );
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            return userRepository.findByEmail(userDetails.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));
        }
        throw new RuntimeException("Not authenticated");
    }
}
