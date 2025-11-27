package com.revature.revtickets.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "events", indexes = {
    @Index(name = "idx_title", columnList = "title"),
    @Index(name = "idx_category", columnList = "category"),
    @Index(name = "idx_event_date", columnList = "event_date")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Long eventId;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 100)
    private String category;

    @Column(name = "event_date", nullable = false)
    private LocalDate eventDate;

    @Column(name = "event_time")
    private LocalTime eventTime;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Column(name = "artist_or_team")
    private String artistOrTeam;

    private String language;

    @Column(name = "age_restriction", length = 50)
    private String ageRestriction;

    @Column(name = "display_image_url", length = 500)
    private String displayImageUrl;

    @Column(name = "banner_image_url", length = 500)
    private String bannerImageUrl;

    @Column(name = "trailer_url", length = 500)
    private String trailerUrl;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
