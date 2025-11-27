package com.revature.revtickets.service;

import com.revature.revtickets.entity.OpenEventShow;
import com.revature.revtickets.repository.OpenEventShowRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class OpenEventShowService {

    @Autowired
    private OpenEventShowRepository openEventShowRepository;
    
    public List<OpenEventShow> getAllShows() {
        return openEventShowRepository.findAll();
    }

    public List<OpenEventShow> getShowsByEventId(Long eventId) {
        return openEventShowRepository.findByEventEventIdAndIsActiveTrue(eventId);
    }

    public List<OpenEventShow> getShowsByEventIdAndDate(Long eventId, LocalDate date) {
        return openEventShowRepository.findByEventEventIdAndShowDateAndIsActiveTrue(eventId, date);
    }

    public List<LocalDate> getAvailableDatesForEvent(Long eventId) {
        List<LocalDate> allDates = openEventShowRepository.findDistinctShowDatesByEventEventIdAndIsActiveTrue(eventId);
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();
        
        // Filter dates that have at least one available show
        return allDates.stream()
            .filter(date -> {
                List<OpenEventShow> showsOnDate = openEventShowRepository.findByEventEventIdAndShowDateAndIsActiveTrue(eventId, date);
                // If date is today, filter out shows that have already started
                if (date.equals(today)) {
                    return showsOnDate.stream().anyMatch(show -> show.getShowTime().isAfter(now));
                }
                // For future dates, return true if there are any shows
                return !showsOnDate.isEmpty();
            })
            .collect(java.util.stream.Collectors.toList());
    }

    public OpenEventShow getShowById(Long id) {
        return openEventShowRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Open event show not found with id: " + id));
    }

    public OpenEventShow createShow(OpenEventShow show) {
        return openEventShowRepository.save(show);
    }

    public OpenEventShow updateShow(Long id, OpenEventShow showDetails) {
        OpenEventShow show = getShowById(id);
        show.setShowDate(showDetails.getShowDate());
        show.setShowTime(showDetails.getShowTime());
        show.setPricingZones(showDetails.getPricingZones());
        show.setTotalCapacity(showDetails.getTotalCapacity());
        show.setAvailableCapacity(showDetails.getAvailableCapacity());
        show.setIsActive(showDetails.getIsActive());
        return openEventShowRepository.save(show);
    }

    public void deleteShow(Long id) {
        OpenEventShow show = getShowById(id);
        show.setIsActive(false);
        openEventShowRepository.save(show);
    }

    public OpenEventShow softDeleteShow(Long id) {
        OpenEventShow show = getShowById(id);
        show.setIsActive(false);
        return openEventShowRepository.save(show);
    }

    public OpenEventShow activateShow(Long id) {
        OpenEventShow show = getShowById(id);
        show.setIsActive(true);
        return openEventShowRepository.save(show);
    }
}
