package io.upschool.service;

import io.upschool.dto.flight.FlightSaveResponse;
import io.upschool.dto.ticket.TicketSaveRequest;
import io.upschool.dto.ticket.TicketSaveResponse;
import io.upschool.dto.BaseResponse;
import io.upschool.dto.ticket.TicketUpdateRequest;
import io.upschool.entity.Flight;
import io.upschool.entity.Ticket;
import io.upschool.exception.ResourceAlreadyDeletedException;
import io.upschool.exception.ResourceNotFoundException;
import io.upschool.repository.TicketRepository;
import io.upschool.repository.FlightRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Repository
@Lazy
public class TicketService {
    private static final int PNR_LENGTH = 8;
    @Autowired
    public final TicketRepository ticketRepository;
    private final FlightService flightService;
    @Autowired
    public FlightRepository flightRepository;

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

    public List<Ticket> getTicketsByPnr(String pnr) {
        return ticketRepository.findByPnrStartingWith(pnr);
    }

    public List<Ticket> getTicketsByFlightId(Long flightId) {
        return ticketRepository.findByFlightId(flightId);
    }

    private String generatePNR() {
        // PNR numarasını rastgele harf ve rakamlarla oluşturma
        StringBuilder pnrBuilder = new StringBuilder();
        Random random = new Random();

        // PNR'de kullanılacak karakterler
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

        for (int i = 0; i < PNR_LENGTH; i++) {
            char randomChar = characters.charAt(random.nextInt(characters.length()));
            pnrBuilder.append(randomChar);
        }

        return pnrBuilder.toString();
    }
    public void updateTicketStatusToDeleted(Ticket ticket) {
        ticket.setStatus(0);
        ticketRepository.save(ticket);
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

        // PNR oluşturma
        String pnr = generatePNR();
        ticket.setPnr(pnr);
        ticket.setCreditCardNumber(normalizeAndMaskCreditCardNumber(ticket.getCreditCardNumber()));
        ticket.setFlight(flightService.convertToEntityFromResponse(ticketRequest.getFlightId(), flightService));
        ticket = ticketRepository.save(ticket);
        TicketSaveResponse ticketSaveResponse = convertToResponse(ticket);
        return BaseResponse.<TicketSaveResponse>builder()
                .status(200)
                .isSuccess(true)
                .data(ticketSaveResponse)
                .build();
    }

    private String normalizeAndMaskCreditCardNumber(String creditCardNumber) throws IllegalArgumentException {
        String digitsOnly = creditCardNumber.replaceAll("[^\\d]", "");
        if (digitsOnly.matches("\\d{4}16\\*{6}\\d{4}")) {
            return creditCardNumber;
        }
        if (digitsOnly.length() < 12) {
            throw new IllegalArgumentException("Invalid credit card number format");
        }
        String prefix = digitsOnly.substring(0, 6);
        String suffix = digitsOnly.substring(digitsOnly.length() - 2);

        String maskedDigits = "*".repeat(4);
        StringBuilder maskedNumberBuilder = new StringBuilder();
        maskedNumberBuilder.append(prefix).append(maskedDigits).append(suffix);
        return maskedNumberBuilder.toString();
    }


    public BaseResponse<TicketSaveResponse> purchaseTicket(TicketSaveRequest ticketRequest) {
        try {
            Optional<Flight> flightOptional = flightRepository.findById(ticketRequest.getFlightId());
            if (flightOptional.isEmpty()) {
                return BaseResponse.<TicketSaveResponse>builder()
                        .status(404)
                        .isSuccess(false)
                        .error("Flight not found with id: " + ticketRequest.getFlightId())
                        .build();
            }
            Flight flight = flightOptional.get();
            if (flight.getStatus() == 0) {
                return BaseResponse.<TicketSaveResponse>builder()
                        .status(400)
                        .isSuccess(false)
                        .error("Cannot purchase ticket for a cancelled flight.")
                        .build();
            }
            flight.setAvailableSeats(flight.getTotalSeats()-flight.getTicketsSold());
            if (flight.getTicketsSold()==flight.getTotalSeats() ) {
                return BaseResponse.<TicketSaveResponse>builder()
                        .status(400)
                        .isSuccess(false)
                        .error("No available seats for this flight.")
                        .build();
            }
            String pnr = generatePNR();
            Ticket ticket = Ticket.builder()
                    .flight(flight)
                    .passengerName(ticketRequest.getPassengerName())
                    .seatNumber(ticketRequest.getSeatNumber())
                    .creditCardNumber(normalizeAndMaskCreditCardNumber(ticketRequest.getCreditCardNumber()))
                    .pnr(pnr)
                    .isConfirmed(true)
                    .build();

            ticket = ticketRepository.save(ticket);
            List<Ticket> tickets=flight.getTickets();
            tickets.add(ticket);
            flight.setTickets(tickets);
            flight.setAvailableSeats(flight.getAvailableSeats() - 1);
            flight.setTicketsSold(flight.getTicketsSold()+1);
            flightRepository.save(flight);

            return BaseResponse.<TicketSaveResponse>builder()
                    .status(200)
                    .isSuccess(true)
                    .data(convertToResponse(ticket))
                    .build();

        } catch (Exception e) {
            return BaseResponse.<TicketSaveResponse>builder()
                    .status(500)
                    .isSuccess(false)
                    .error("An error occurred while processing the request.")
                    .build();
        }
    }
    public Ticket getTicketById4Repo(Long id) {
        return ticketRepository.findById(id)
                .orElse(null);
    }
    public  List<Ticket> getFlight4Repo(Flight flight) {
        return ticketRepository.findByFlight(flight);
    }
    private boolean flightAlreadyExists(Long flightId) {
        return flightRepository.existsById(flightId);
    }

    public BaseResponse<TicketSaveResponse> updateTicket(Long id, TicketUpdateRequest ticketUpdateRequest) {
        Ticket ticket = ticketRepository.findById(id).orElse(null);
        if (ticket == null||ticket.getStatus()==0) {
            return BaseResponse.<TicketSaveResponse>builder()
                    .status(404)
                    .isSuccess(false)
                    .error("Ticket not found with id: " + id)
                    .build();
        }
        if (ticketUpdateRequest.getPassengerName() != null) {
            ticket.setPassengerName(ticketUpdateRequest.getPassengerName());
        }

        if (ticketUpdateRequest.getSeatNumber() != null) {
            ticket.setSeatNumber(ticketUpdateRequest.getSeatNumber());
        }
        if (ticketUpdateRequest.getCancellation() !=0) {
            ticket.setCancellation(1);
            ticket.setStatus(0);
        }
        if (ticketUpdateRequest.getMaskedCreditCardNumber() != null) {
            String creditCardNumber = unmaskCreditCardNumber(ticketUpdateRequest.getMaskedCreditCardNumber());
            ticket.setCreditCardNumber(creditCardNumber);
        }
        ticket = ticketRepository.save(ticket);
        TicketSaveResponse ticketSaveResponse = convertToResponse(ticket);
        return BaseResponse.<TicketSaveResponse>builder()
                .status(200)
                .isSuccess(true)
                .data(ticketSaveResponse)
                .build();
    }

    private String unmaskCreditCardNumber(String maskedCreditCardNumber) {
        return maskedCreditCardNumber.replaceAll("\\*", "");
    }

    public void softDeleteTicket(Long id) throws ResourceAlreadyDeletedException {
        Ticket ticket = ticketRepository.findById(id).orElse(null);
        if (ticket == null) {
            throw new ResourceNotFoundException("Ticket not found with id: " + id);
        }

        if (ticket.getStatus() == 0) {
            throw new ResourceAlreadyDeletedException("Ticket with id " + id + " is already deleted.");
        }

        ticket.setStatus(0);
        ticketRepository.save(ticket);
    }

    public TicketSaveResponse convertToResponse(Ticket ticket) {
        return TicketSaveResponse.builder()
                .id(ticket.getId())
                .flightId(ticket.getFlight().getId())
                .passengerName(ticket.getPassengerName())
                .seatNumber(ticket.getSeatNumber())
                .maskedCreditCardNumber(ticket.getCreditCardNumber())
                .pnr(ticket.getPnr())
                .status(ticket.getStatus())
                .cancellation(ticket.getCancellation())
                .isConfirmed(ticket.isConfirmed())
                .build();
    }


    private Ticket convertToEntity(TicketSaveRequest ticketRequest) {
        return Ticket.builder()
                .passengerName(ticketRequest.getPassengerName())
                .seatNumber(ticketRequest.getSeatNumber())
                .build();
    }
public void batchUpdateFlightsAndTickets(List<Flight> flightsToUpdate) {
    if (flightsToUpdate != null && !flightsToUpdate.isEmpty()) {
        List<Ticket> allTicketsToUpdate = new ArrayList<>();
        for (Flight flight : flightsToUpdate) {
            List<Ticket> tickets = flight.getTickets();
            for (Ticket ticket : tickets) {
                ticket.setStatus(0);
                allTicketsToUpdate.add(ticket);
            }
        }
        batchUpdateTickets(allTicketsToUpdate);
        for (Flight flight : flightsToUpdate) {
            flight.setStatus(0);
        }
        flightRepository.saveAll(flightsToUpdate);
    }
}
    public void batchUpdateTickets(List<Ticket> ticketsToUpdate) {
        if (ticketsToUpdate != null && !ticketsToUpdate.isEmpty()) {
            List<Ticket> updatedTickets = new ArrayList<>();
            for (Ticket ticket : ticketsToUpdate) {
                ticket.setStatus(0);
                updatedTickets.add(ticket);
            }
            ticketRepository.saveAll(updatedTickets);
        }
    }

    public BaseResponse<TicketSaveResponse> cancelTicketByPnr(String pnr) {
        List<Ticket> ticketsToCancel = ticketRepository.findByPnrStartingWith(pnr);

        if (ticketsToCancel.isEmpty()) {
            return BaseResponse.<TicketSaveResponse>builder()
                    .status(404)
                    .isSuccess(false)
                    .error("No tickets found with PNR: " + pnr)
                    .build();
        }

        for (Ticket ticket : ticketsToCancel) {
            if (ticket.getStatus() == 1) { // Sadece aktif biletleri iptal et
                ticket.setCancellation(1);
                ticket.setStatus(0);
                ticketRepository.save(ticket);
            }
        }

        return BaseResponse.<TicketSaveResponse>builder()
                .status(200)
                .isSuccess(true)
                .data(null)
                .build();
    }

}
