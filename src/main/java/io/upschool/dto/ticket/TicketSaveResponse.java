package io.upschool.dto.ticket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TicketSaveResponse {
    private Long id;
    private Long flightId;
    private String passengerName;
    private String seatNumber;

    // Add getters and setters if necessary
}
