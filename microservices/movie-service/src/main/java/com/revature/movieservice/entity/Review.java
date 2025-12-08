package com.revature.movieservice.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "reviews")
@Data
public class Review {
    @Id
    private String id;
    
    private Long movieId;
    private Long userId;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt = LocalDateTime.now();
}
