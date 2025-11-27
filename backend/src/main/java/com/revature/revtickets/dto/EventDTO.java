package com.revature.revtickets.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventDTO {
    private Long eventId;
    private String title;
    private String description;
    private String category;
    private LocalDate eventDate;
    private LocalTime eventTime;
    private Integer durationMinutes;
    private String artistOrTeam;
    private String language;
    private String ageRestriction;
    private String displayImageUrl;
    private String bannerImageUrl;
    private String trailerUrl;
}
