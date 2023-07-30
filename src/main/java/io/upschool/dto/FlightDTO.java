package io.upschool.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class FlightDTO {
    private Long id;
    private CompanyDTO airline;
    private RouteDTO route;
    private Date departureDate;
    private double price;
}
