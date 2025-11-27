package com.revature.revtickets.repository;

import com.revature.revtickets.entity.OpenEventShow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface OpenEventShowRepository extends JpaRepository<OpenEventShow, Long> {
    
    List<OpenEventShow> findByEventEventIdAndIsActiveTrue(Long eventId);
    
    List<OpenEventShow> findByEventEventIdAndShowDateAndIsActiveTrue(Long eventId, LocalDate showDate);
    
    @Query("SELECT DISTINCT o.showDate FROM OpenEventShow o WHERE o.event.eventId = :eventId AND o.isActive = true AND o.showDate >= CURRENT_DATE ORDER BY o.showDate")
    List<LocalDate> findDistinctShowDatesByEventEventIdAndIsActiveTrue(Long eventId);
}
