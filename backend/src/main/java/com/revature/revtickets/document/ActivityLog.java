package com.revature.revtickets.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;

@Document(collection = "activity_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivityLog {

    @Id
    private String id;
    
    private Long userId;
    private String userEmail;
    private String action; // LOGIN, BOOKING_CREATED, PAYMENT_SUCCESS, etc.
    private String description;
    
    private Map<String, Object> metadata;
    
    private String ipAddress;
    private String userAgent;
    
    private LocalDateTime timestamp = LocalDateTime.now();
}
