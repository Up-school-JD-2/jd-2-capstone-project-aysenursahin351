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
    private String pnr;
    private Long flightId;
    private String passengerName;
    private String seatNumber;
    private String maskedCreditCardNumber;
    private int cancellation; // Eklendi
    private int status; // Eklendi
    private boolean isConfirmed; // Eklendi

}
