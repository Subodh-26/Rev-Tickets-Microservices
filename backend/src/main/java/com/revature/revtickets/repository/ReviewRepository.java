package com.revature.revtickets.repository;

import com.revature.revtickets.document.Review;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends MongoRepository<Review, String> {
    List<Review> findByMovieId(Long movieId);
    List<Review> findByMovieIdOrderByCreatedAtDesc(Long movieId);
    List<Review> findByEventId(Long eventId);
    List<Review> findByEventIdOrderByCreatedAtDesc(Long eventId);
    List<Review> findByUserId(Long userId);
    List<Review> findByUserIdOrderByCreatedAtDesc(Long userId);
}
