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
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "seat_layouts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeatLayout {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "layout_id")
    private Long layoutId;

    @Column(name = "layout_name", nullable = false)
    private String layoutName;

    @Column(name = "total_rows", nullable = false)
    private Integer totalRows;

    @Column(name = "total_columns", nullable = false)
    private Integer totalColumns;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "seat_configuration", nullable = false, columnDefinition = "JSON")
    private List<Map<String, Object>> seatConfiguration;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
