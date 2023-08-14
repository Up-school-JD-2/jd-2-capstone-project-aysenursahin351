package io.upschool.dto.route;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RouteSaveRequest {
    private Long departureAirportId;
    private Long arrivalAirportId;
    private double distance;
    private String code;

}
