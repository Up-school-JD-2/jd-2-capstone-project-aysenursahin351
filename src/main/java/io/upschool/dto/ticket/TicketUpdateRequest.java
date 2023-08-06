package io.upschool.dto.ticket;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TicketUpdateRequest {
    private Long id;
    private Long flightId;
    private String passengerName;
    private String seatNumber;
}