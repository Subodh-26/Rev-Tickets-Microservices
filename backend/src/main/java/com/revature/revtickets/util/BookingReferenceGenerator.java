package com.revature.revtickets.util;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

@Component
public class BookingReferenceGenerator {

    private static final String PREFIX = "BK";
    private static final Random random = new Random();

    public String generateBookingReference() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String datePart = LocalDateTime.now().format(formatter);
        
        String randomPart = String.format("%06d", random.nextInt(1000000));
        
        return PREFIX + datePart + randomPart;
    }
}
