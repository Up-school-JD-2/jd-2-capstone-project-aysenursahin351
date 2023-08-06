package io.upschool.service;

import io.upschool.dto.flight.FlightSaveResponse;
import io.upschool.dto.ticket.TicketSaveRequest;
import io.upschool.dto.ticket.TicketSaveResponse;
import io.upschool.dto.BaseResponse;
import io.upschool.entity.Ticket;
import io.upschool.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class TicketService {
    private final TicketRepository ticketRepository;
    private final FlightService flightService;

    public BaseResponse<TicketSaveResponse> getTicketById(Long id) {
        TicketSaveResponse TicketSaveResponse = ticketRepository.findById(id)
                .map(this::convertToResponse)
                .orElse(null);

        if (TicketSaveResponse != null) {
            return BaseResponse.<TicketSaveResponse>builder()
                    .status(200)
                    .isSuccess(true)
                    .data(TicketSaveResponse)
                    .build();
        } else {
            return BaseResponse.<TicketSaveResponse>builder()
                    .status(404)
                    .isSuccess(false)
                    .error("Ticket not found with id: " + id)
                    .build();
        }
    }

    public BaseResponse<TicketSaveResponse> saveTicket(TicketSaveRequest ticketRequest) {
        FlightSaveResponse flightResponse = flightService.getFlightById(ticketRequest.getFlightId()).getData();
        if (flightResponse == null) {
            return BaseResponse.<TicketSaveResponse>builder()
                    .status(404)
                    .isSuccess(false)
                    .error("Flight not found with id: " + ticketRequest.getFlightId())
                    .build();
        }

        Ticket ticket = convertToEntity(ticketRequest);
        ticket.setFlight(flightService.convertToEntityFromResponse(ticketRequest.getFlightId(), flightService));
        ticket = ticketRepository.save(ticket);
        TicketSaveResponse ticketSaveResponse = convertToResponse(ticket);
        return BaseResponse.<TicketSaveResponse>builder()
                .status(200)
                .isSuccess(true)
                .data(ticketSaveResponse)
                .build();
    }


    public void deleteTicket(Long id) {
        ticketRepository.deleteById(id);
    }

    private TicketSaveResponse convertToResponse(Ticket ticket) {
        return TicketSaveResponse.builder()
                .id(ticket.getId())
                .flightId(ticket.getFlight().getId())
                .passengerName(ticket.getPassengerName())
                .seatNumber(ticket.getSeatNumber())
                .build();
    }

    private Ticket convertToEntity(TicketSaveRequest ticketRequest) {
        return Ticket.builder()
                .passengerName(ticketRequest.getPassengerName())
                .seatNumber(ticketRequest.getSeatNumber())
                .build();
    }
}
