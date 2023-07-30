package io.upschool.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RouteDTO {
    private Long id;
    private AirportDTO departureAirport;
    private AirportDTO arrivalAirport;
    private double distance;
}

