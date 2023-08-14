package io.upschool.dto.flight;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FlightSaveRequest {
    private Long companyId;
    private String name;
    private Long routeId;
    private Date departureDate;
    private double price;
    private int totalSeats; // Eklendi

}
