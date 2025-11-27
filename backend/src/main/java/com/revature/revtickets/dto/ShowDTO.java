package com.revature.revtickets.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShowDTO {
    private Long showId;
    private Long movieId;
    private String movieTitle;
    private Long eventId;
    private String eventTitle;
    private Long venueId;
    private String venueName;
    private String venueCity;
    private Long screenId;
    private Integer screenNumber;
    private LocalDate showDate;
    private LocalTime showTime;
    private BigDecimal basePrice;
    private Map<String, BigDecimal> pricingTiers;
    private Integer totalSeats;
    private Integer availableSeats;
}
