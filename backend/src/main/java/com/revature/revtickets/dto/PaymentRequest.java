package com.revature.revtickets.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {
    private Long showId;
    private Long openShowId;
    private Boolean isOpenEvent;
    private List<String> seatNumbers; // For regular shows
    private List<ZoneBookingDTO> zoneBookings; // For open events
    private BigDecimal totalAmount;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ZoneBookingDTO {
        private String zoneName;
        private Integer quantity;
        private BigDecimal pricePerTicket;
    }
}
