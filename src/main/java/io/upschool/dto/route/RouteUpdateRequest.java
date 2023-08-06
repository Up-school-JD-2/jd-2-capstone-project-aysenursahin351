package io.upschool.dto.route;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RouteUpdateRequest {
    private Long id;
    private Long departureAirportId;
    private Long arrivalAirportId;
    private double distance;
}
