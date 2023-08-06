package io.upschool.dto.route;

import io.upschool.dto.airport.AirportSaveResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RouteSaveResponse {
    private Long id;
    private AirportSaveResponse departureAirport;
    private AirportSaveResponse arrivalAirport;
    private double distance;



}
