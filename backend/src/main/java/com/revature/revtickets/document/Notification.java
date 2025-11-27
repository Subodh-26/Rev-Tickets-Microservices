package com.revature.revtickets.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    private String id;
    
    private Long userId;
    private String type; // BOOKING_CONFIRMATION, BOOKING_CANCELLATION, OFFER, REMINDER
    private String title;
    private String message;
    
    private Boolean isRead = false;
    private LocalDateTime createdAt = LocalDateTime.now();
    
    // Optional metadata
    private Long bookingId;
    private Long showId;
}
