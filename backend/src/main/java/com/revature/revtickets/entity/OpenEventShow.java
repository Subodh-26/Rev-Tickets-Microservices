package com.revature.revtickets.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "open_event_shows", indexes = {
    @Index(name = "idx_event_date", columnList = "event_id, show_date"),
    @Index(name = "idx_active", columnList = "is_active")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OpenEventShow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "open_show_id")
    private Long openShowId;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(name = "show_date", nullable = false)
    private LocalDate showDate;

    @Column(name = "show_time", nullable = false)
    private LocalTime showTime;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "pricing_zones", columnDefinition = "JSON", nullable = false)
    private List<Map<String, Object>> pricingZones;

    @Column(name = "total_capacity", nullable = false)
    private Integer totalCapacity;

    @Column(name = "available_capacity", nullable = false)
    private Integer availableCapacity;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
