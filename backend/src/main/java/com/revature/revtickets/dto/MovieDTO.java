package com.revature.revtickets.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieDTO {
    private Long movieId;
    private String title;
    private String description;
    private Integer durationMinutes;
    private String genre;
    private String language;
    private String parentalRating;
    private LocalDate releaseDate;
    private String cast;
    private String crew;
    private String trailerUrl;
    private String displayImageUrl;
    private String bannerImageUrl;
    private BigDecimal rating;
}
