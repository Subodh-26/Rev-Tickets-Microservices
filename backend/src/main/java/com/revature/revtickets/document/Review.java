package com.revature.revtickets.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "reviews")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Review {

    @Id
    private String id;
    
    private Long userId;
    private String userName;
    private Long movieId;
    private Long eventId;
    private String reviewType; // MOVIE or EVENT
    
    private Double rating;
    private String comment;
    
    private List<String> likedByUserIds = new ArrayList<>();
    private Integer likeCount = 0;
    
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();
}
