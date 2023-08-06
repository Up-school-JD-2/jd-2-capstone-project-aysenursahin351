package io.upschool.dto.flight;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class FlightUpdateRequest {
    private Long id;
    private Long companyId;
    private Long routeId;
    private Date departureDate;
    private double price;
}
