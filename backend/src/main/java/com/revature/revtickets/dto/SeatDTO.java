package com.revature.revtickets.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeatDTO {
    private Long seatId;
    private String rowLabel;
    private Integer seatNumber;
    private String seatType;
    private BigDecimal price;
    private Boolean isAvailable;
    private Boolean isBlocked;
}
