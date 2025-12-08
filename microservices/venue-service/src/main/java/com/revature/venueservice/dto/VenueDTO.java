package com.revature.venueservice.dto;

import java.util.List;
import java.util.Map;

public class VenueDTO {
    private Long id;
    private String name;
    private String address;
    private String city;
    private String state;
    private String pincode;
    private Integer totalScreens;
    private Map<String, Object> facilities;
    private List<ScreenDTO> screens;

    // Constructors
    public VenueDTO() {}

    public VenueDTO(Long id, String name, String address, String city, String state, String pincode, Integer totalScreens, Map<String, Object> facilities, List<ScreenDTO> screens) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.city = city;
        this.state = state;
        this.pincode = pincode;
        this.totalScreens = totalScreens;
        this.facilities = facilities;
        this.screens = screens;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getPincode() { return pincode; }
    public void setPincode(String pincode) { this.pincode = pincode; }

    public Integer getTotalScreens() { return totalScreens; }
    public void setTotalScreens(Integer totalScreens) { this.totalScreens = totalScreens; }

    public Map<String, Object> getFacilities() { return facilities; }
    public void setFacilities(Map<String, Object> facilities) { this.facilities = facilities; }

    public List<ScreenDTO> getScreens() { return screens; }
    public void setScreens(List<ScreenDTO> screens) { this.screens = screens; }

    // Builder pattern
    public static VenueDTO.Builder builder() {
        return new VenueDTO.Builder();
    }

    public static class Builder {
        private Long id;
        private String name;
        private String address;
        private String city;
        private String state;
        private String pincode;
        private Integer totalScreens;
        private Map<String, Object> facilities;
        private List<ScreenDTO> screens;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder address(String address) { this.address = address; return this; }
        public Builder city(String city) { this.city = city; return this; }
        public Builder state(String state) { this.state = state; return this; }
        public Builder pincode(String pincode) { this.pincode = pincode; return this; }
        public Builder totalScreens(Integer totalScreens) { this.totalScreens = totalScreens; return this; }
        public Builder facilities(Map<String, Object> facilities) { this.facilities = facilities; return this; }
        public Builder screens(List<ScreenDTO> screens) { this.screens = screens; return this; }

        public VenueDTO build() {
            return new VenueDTO(id, name, address, city, state, pincode, totalScreens, facilities, screens);
        }
    }
}