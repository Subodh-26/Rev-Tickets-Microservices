package com.revature.movieservice.repository;

import com.revature.movieservice.entity.Review;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface ReviewRepository extends MongoRepository<Review, String> {
    List<Review> findByMovieIdOrderByCreatedAtDesc(Long movieId);
}
