package com.revature.revtickets.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "movies", indexes = {
    @Index(name = "idx_title", columnList = "title"),
    @Index(name = "idx_genre", columnList = "genre"),
    @Index(name = "idx_release_date", columnList = "release_date")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "movie_id")
    private Long movieId;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    private String genre;

    private String language;

    @Column(name = "parental_rating", length = 10)
    private String parentalRating;

    @Column(name = "release_date")
    private LocalDate releaseDate;

    @Column(columnDefinition = "TEXT")
    private String cast;

    @Column(columnDefinition = "TEXT")
    private String crew;

    @Column(name = "trailer_url", length = 500)
    private String trailerUrl;

    @Column(name = "display_image_url", length = 500)
    private String displayImageUrl;

    @Column(name = "banner_image_url", length = 500)
    private String bannerImageUrl;

    @Column(precision = 2, scale = 1)
    private BigDecimal rating = BigDecimal.ZERO;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
