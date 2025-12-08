package com.revature.venueservice.dto;

import java.util.Map;

public class ScreenDTO {
    private Long id;
    private Integer screenNumber;
    private String screenType;
    private String soundSystem;
    private Map<String, Object> seatLayout;
    private Integer totalSeats;

    // Constructors
    public ScreenDTO() {}

    public ScreenDTO(Long id, Integer screenNumber, String screenType, String soundSystem, Map<String, Object> seatLayout, Integer totalSeats) {
        this.id = id;
        this.screenNumber = screenNumber;
        this.screenType = screenType;
        this.soundSystem = soundSystem;
        this.seatLayout = seatLayout;
        this.totalSeats = totalSeats;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getScreenNumber() { return screenNumber; }
    public void setScreenNumber(Integer screenNumber) { this.screenNumber = screenNumber; }

    public String getScreenType() { return screenType; }
    public void setScreenType(String screenType) { this.screenType = screenType; }

    public String getSoundSystem() { return soundSystem; }
    public void setSoundSystem(String soundSystem) { this.soundSystem = soundSystem; }

    public Map<String, Object> getSeatLayout() { return seatLayout; }
    public void setSeatLayout(Map<String, Object> seatLayout) { this.seatLayout = seatLayout; }

    public Integer getTotalSeats() { return totalSeats; }
    public void setTotalSeats(Integer totalSeats) { this.totalSeats = totalSeats; }

    // Builder pattern
    public static ScreenDTO.Builder builder() {
        return new ScreenDTO.Builder();
    }

    public static class Builder {
        private Long id;
        private Integer screenNumber;
        private String screenType;
        private String soundSystem;
        private Map<String, Object> seatLayout;
        private Integer totalSeats;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder screenNumber(Integer screenNumber) { this.screenNumber = screenNumber; return this; }
        public Builder screenType(String screenType) { this.screenType = screenType; return this; }
        public Builder soundSystem(String soundSystem) { this.soundSystem = soundSystem; return this; }
        public Builder seatLayout(Map<String, Object> seatLayout) { this.seatLayout = seatLayout; return this; }
        public Builder totalSeats(Integer totalSeats) { this.totalSeats = totalSeats; return this; }

        public ScreenDTO build() {
            return new ScreenDTO(id, screenNumber, screenType, soundSystem, seatLayout, totalSeats);
        }
    }
}