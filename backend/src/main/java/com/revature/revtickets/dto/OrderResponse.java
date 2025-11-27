package com.revature.revtickets.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private String orderId;
    private Long bookingId;
    private String amount;
    private String currency;
    private String key;
}
