package com.revature.revtickets.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
}
