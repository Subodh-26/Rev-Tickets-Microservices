package com.revature.revtickets.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "seats", 
    uniqueConstraints = @UniqueConstraint(name = "unique_seat_per_show", 
        columnNames = {"show_id", "row_label", "seat_number"}),
    indexes = {
        @Index(name = "idx_show_availability", columnList = "show_id, is_available")
    })
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seat_id")
    private Long seatId;

    @ManyToOne
    @JoinColumn(name = "show_id", nullable = false)
    private Show show;

    @Column(name = "row_label", nullable = false, length = 5)
    private String rowLabel;

    @Column(name = "seat_number", nullable = false)
    private Integer seatNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "seat_type", nullable = false)
    private SeatType seatType = SeatType.REGULAR;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "is_available")
    private Boolean isAvailable = true;

    @Column(name = "is_blocked")
    private Boolean isBlocked = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public enum SeatType {
        PREMIUM, REGULAR, ECONOMY, RECLINER, VIP
    }
}
