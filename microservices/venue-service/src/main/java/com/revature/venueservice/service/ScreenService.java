package com.revature.venueservice.service;

import com.revature.venueservice.dto.ScreenDTO;
import com.revature.venueservice.entity.Screen;
import com.revature.venueservice.repository.ScreenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ScreenService {

    @Autowired
    private ScreenRepository screenRepository;

    public List<Screen> getScreensByVenueId(Long venueId) {
        return screenRepository.findByVenueIdAndIsActiveTrue(venueId);
    }

    public List<ScreenDTO> convertToScreenDTOs(List<Screen> screens) {
        return screens.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public ScreenDTO convertToDTO(Screen screen) {
        return ScreenDTO.builder()
            .id(screen.getScreenId())
            .screenNumber(screen.getScreenNumber())
            .screenType(screen.getScreenType())
            .soundSystem(screen.getSoundSystem())
            .seatLayout(screen.getSeatLayout())
            .totalSeats(screen.getTotalSeats())
            .build();
    }

    public Screen convertFromDTO(ScreenDTO screenDTO, Long venueId) {
        Screen screen = new Screen();
        screen.setVenueId(venueId);
        screen.setScreenNumber(screenDTO.getScreenNumber());
        screen.setScreenType(screenDTO.getScreenType());
        screen.setSoundSystem(screenDTO.getSoundSystem());
        screen.setSeatLayout(screenDTO.getSeatLayout());
        screen.setTotalSeats(screenDTO.getTotalSeats());
        screen.setIsActive(true);
        return screen;
    }

    public void deleteScreensByVenueId(Long venueId) {
        screenRepository.deleteByVenueId(venueId);
    }

    public List<Screen> saveScreens(List<Screen> screens) {
        return screenRepository.saveAll(screens);
    }
}