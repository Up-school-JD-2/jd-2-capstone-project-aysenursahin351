package io.upschool.dto.flight;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlightSaveResponse {
    private Long id;
    private Long companyId;
    private Long routeId;
    private Date departureDate;
    private double price;




}
