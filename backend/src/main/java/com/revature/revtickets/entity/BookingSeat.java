package com.revature.revtickets.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "booking_seats",
    uniqueConstraints = @UniqueConstraint(name = "unique_booking_seat",
        columnNames = {"booking_id", "seat_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingSeat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_seat_id")
    private Long bookingSeatId;

    @ManyToOne
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @ManyToOne
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;

    @Column(name = "seat_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal seatPrice;
}
