package com.revature.bookingservice.service;

import com.revature.bookingservice.entity.Seat;
import com.revature.bookingservice.repository.SeatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SeatService {

    @Autowired
    private SeatRepository seatRepository;

    public List<Seat> getSeatsByShowId(Long showId) {
        return seatRepository.findByShowIdOrderByRowLabelAscSeatNumberAsc(showId);
    }

    public List<Seat> getAvailableSeatsByShowId(Long showId) {
        return seatRepository.findByShowIdAndIsAvailableTrue(showId);
    }
    
    public void generateSeatsForShow(Long showId, int totalSeats, java.math.BigDecimal basePrice) {
        int seatsPerRow = 10;
        int rows = (int) Math.ceil((double) totalSeats / seatsPerRow);
        
        for (int row = 0; row < rows; row++) {
            char rowLabel = (char) ('A' + row);
            int seatsInThisRow = Math.min(seatsPerRow, totalSeats - (row * seatsPerRow));
            
            for (int seatNum = 1; seatNum <= seatsInThisRow; seatNum++) {
                Seat seat = new Seat();
                seat.setShowId(showId);
                seat.setRowLabel(String.valueOf(rowLabel));
                seat.setSeatNumber(seatNum);
                seat.setPrice(basePrice);
                seat.setIsAvailable(true);
                seatRepository.save(seat);
            }
        }
    }
}
