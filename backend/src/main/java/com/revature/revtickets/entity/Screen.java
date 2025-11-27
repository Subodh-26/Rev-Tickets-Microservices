package com.revature.revtickets.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "screens", indexes = {
    @Index(name = "idx_venue_screen", columnList = "venue_id, screen_number")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Screen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "screen_id")
    private Long screenId;

    @ManyToOne
    @JoinColumn(name = "venue_id", nullable = false)
    private Venue venue;

    @Column(name = "screen_number", nullable = false)
    private Integer screenNumber;

    @Column(name = "screen_type", length = 50)
    private String screenType; // 2D, 3D, IMAX, 4DX

    @Column(name = "sound_system", length = 100)
    private String soundSystem; // Dolby Atmos, DTS, Standard

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "seat_layout", columnDefinition = "JSON", nullable = false)
    private Map<String, Object> seatLayout;

    @Column(name = "total_seats")
    private Integer totalSeats;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
