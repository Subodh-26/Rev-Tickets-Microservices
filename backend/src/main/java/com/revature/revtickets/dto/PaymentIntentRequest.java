package com.revature.revtickets.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentIntentRequest {
    private Long bookingId;
    private BigDecimal amount;
    private String currency = "INR";
}
