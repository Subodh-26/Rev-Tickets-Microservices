package com.revature.revtickets.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "cart",
    uniqueConstraints = @UniqueConstraint(name = "unique_cart_seat",
        columnNames = {"user_id", "seat_id"}),
    indexes = {
        @Index(name = "idx_user_show", columnList = "user_id, show_id"),
        @Index(name = "idx_expires", columnList = "expires_at")
    })
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_id")
    private Long cartId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "show_id", nullable = false)
    private Show show;

    @ManyToOne
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;

    @CreationTimestamp
    @Column(name = "added_at", updatable = false)
    private LocalDateTime addedAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;
}
