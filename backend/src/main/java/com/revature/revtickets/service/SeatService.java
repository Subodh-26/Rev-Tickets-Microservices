package com.revature.revtickets.service;

import com.revature.revtickets.dto.SeatDTO;
import com.revature.revtickets.entity.Seat;
import com.revature.revtickets.entity.Show;
import com.revature.revtickets.exception.ResourceNotFoundException;
import com.revature.revtickets.repository.SeatRepository;
import com.revature.revtickets.repository.ShowRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class SeatService {

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private ShowRepository showRepository;

    public List<SeatDTO> getSeatsByShowId(Long showId) {
        List<Seat> seats = seatRepository.findByShowShowIdOrderByRowLabelAscSeatNumberAsc(showId);
        return seats.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    public List<Seat> getAvailableSeatsByShowId(Long showId) {
        return seatRepository.findByShowShowIdAndIsAvailableTrue(showId);
    }

    public Seat getSeatById(Long id) {
        return seatRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Seat not found with id: " + id));
    }

    public List<Seat> generateSeatsForShow(Long showId, int totalRows, int seatsPerRow) {
        Show show = showRepository.findById(showId)
            .orElseThrow(() -> new ResourceNotFoundException("Show not found with id: " + showId));

        // Delete existing seats for this show first
        List<Seat> existingSeats = seatRepository.findByShowShowId(showId);
        if (!existingSeats.isEmpty()) {
            seatRepository.deleteAll(existingSeats);
            System.out.println("Deleted " + existingSeats.size() + " existing seats for show " + showId);
        }

        // Get disabled seats from screen layout
        Set<String> disabledSeats = new HashSet<>();
        try {
            Map<String, Object> seatLayout = show.getScreen().getSeatLayout();
            if (seatLayout != null && seatLayout.containsKey("disabledSeats")) {
                Object disabledObj = seatLayout.get("disabledSeats");
                if (disabledObj instanceof List) {
                    List<?> disabledList = (List<?>) disabledObj;
                    for (Object seatLabel : disabledList) {
                        disabledSeats.add(seatLabel.toString());
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error reading disabled seats: " + e.getMessage());
        }

        List<Seat> seats = new ArrayList<>();
        
        for (int i = 1; i <= totalRows; i++) {
            String rowLabel = String.valueOf((char) ('A' + i - 1)); // A, B, C, etc.
            int rowSeatNumber = 1; // Sequential number for this row only

            for (int position = 1; position <= seatsPerRow; position++) {
                String seatLabel = rowLabel + position;
                
                Seat seat = new Seat();
                seat.setShow(show);
                seat.setRowLabel(rowLabel);
                seat.setSeatType(Seat.SeatType.REGULAR);
                seat.setPrice(show.getBasePrice());
                
                // Check if this position is blocked/disabled
                if (disabledSeats.contains(seatLabel)) {
                    // Create blocked seat marker with negative position number
                    seat.setSeatNumber(-position); // Negative position for blocked
                    seat.setIsBlocked(true);
                    seat.setIsAvailable(false);
                } else {
                    // Create actual bookable seat with row-based sequential number
                    seat.setSeatNumber(rowSeatNumber);
                    seat.setIsBlocked(false);
                    seat.setIsAvailable(true);
                    rowSeatNumber++; // Increment for each bookable seat in this row
                }
                
                seats.add(seat);
            }
        }

        return seatRepository.saveAll(seats);
    }

    public Seat updateSeat(Long id, SeatDTO dto) {
        Seat seat = getSeatById(id);
        seat.setSeatType(Seat.SeatType.valueOf(dto.getSeatType()));
        seat.setPrice(dto.getPrice());
        return seatRepository.save(seat);
    }

    public void deleteSeat(Long id) {
        seatRepository.deleteById(id);
    }

    private SeatDTO convertToDTO(Seat seat) {
        SeatDTO dto = new SeatDTO();
        dto.setSeatId(seat.getSeatId());
        dto.setRowLabel(seat.getRowLabel());
        dto.setSeatNumber(seat.getSeatNumber());
        dto.setSeatType(seat.getSeatType().name());
        dto.setPrice(seat.getPrice());
        dto.setIsAvailable(seat.getIsAvailable());
        dto.setIsBlocked(seat.getIsBlocked());
        return dto;
    }
}
