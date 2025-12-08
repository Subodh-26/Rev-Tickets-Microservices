package com.revature.movieservice.controller;

import com.revature.movieservice.dto.ApiResponse;
import com.revature.movieservice.entity.Review;
import com.revature.movieservice.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewRepository reviewRepository;

    @GetMapping("/movie/{movieId}")
    public ResponseEntity<ApiResponse<List<Review>>> getMovieReviews(@PathVariable Long movieId) {
        List<Review> reviews = reviewRepository.findByMovieIdOrderByCreatedAtDesc(movieId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Reviews fetched", reviews));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Review>> createReview(@RequestBody Map<String, Object> data) {
        Review review = new Review();
        review.setMovieId(Long.parseLong(data.get("movieId").toString()));
        review.setUserId(Long.parseLong(data.get("userId").toString()));
        review.setRating(Integer.parseInt(data.get("rating").toString()));
        if (data.get("comment") != null) {
            review.setComment(data.get("comment").toString());
        }
        review.setCreatedAt(java.time.LocalDateTime.now());
        Review saved = reviewRepository.save(review);
        return ResponseEntity.ok(new ApiResponse<>(true, "Review submitted", saved));
    }
}
