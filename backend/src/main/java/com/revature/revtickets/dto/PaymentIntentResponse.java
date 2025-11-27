package com.revature.revtickets.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentIntentResponse {
    private String clientSecret;
    private String paymentIntentId;
}
