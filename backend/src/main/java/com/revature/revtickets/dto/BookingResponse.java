package com.revature.revtickets.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {
    private Long bookingId;
    private String bookingReference;
    private String showTitle;
    private String venueName;
    private String showDateTime;
    private List<SeatInfo> seats;
    private BigDecimal totalAmount;
    private String qrCodeBase64;
    private String status;
    private LocalDateTime bookingDate;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SeatInfo {
        private String rowLabel;
        private Integer seatNumber;
        private String seatType;
        private BigDecimal price;
    }
}
