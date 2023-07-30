package io.upschool.dto;

import io.upschool.entity.Ticket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TicketDTO {
    private Long id;
    private Long flightId;
    private FlightDTO flight;
    private String passengerName;
    private String seatNumber;
}