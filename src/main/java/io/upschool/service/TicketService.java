package io.upschool.service;

import io.upschool.dto.*;
import io.upschool.entity.*;
import io.upschool.repository.FlightRepository;
import io.upschool.repository.RouteRepository;
import io.upschool.repository.TicketRepository;
import org.jetbrains.annotations.NotNull;
import io.upschool.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TicketService {
    private final TicketRepository ticketRepository;
    private final FlightService flightService;




    private TicketDTO convertToDTO(@NotNull Ticket ticket) {
        return TicketDTO.builder()
                .id(ticket.getId())
                .flightId(ticket.getFlight().getId()) // flightId alanını doldurduk
                .passengerName(ticket.getPassengerName())
                .seatNumber(ticket.getSeatNumber())
                .build();
    }

    private Ticket convertToEntity(@NotNull TicketDTO ticketDTO) {
        return Ticket.builder()
                .id(ticketDTO.getId())
                .passengerName(ticketDTO.getPassengerName())
                .seatNumber(ticketDTO.getSeatNumber())
                .build();
    }

    private FlightDTO convertToDTO(@NotNull Flight flight) {
        return FlightDTO.builder()
                .id(flight.getId())
                .airline(convertToDTO(flight.getAirline()))
                .route(convertToDTO(flight.getRoute()))
                .departureDate(flight.getDepartureDate())
                .price(flight.getPrice())
                .build();
    }

    private AirportDTO convertToDTO(@NotNull Airport airport) {
        return AirportDTO.builder()
                .id(airport.getId())
                .name(airport.getName())
                .country(airport.getCountry())
                .code(airport.getCode())
                .build();
    }

    private RouteDTO convertToDTO(@NotNull Route route) {
        return RouteDTO.builder()
                .id(route.getId())
                .departureAirport(convertToDTO(route.getDepartureAirport()))
                .arrivalAirport(convertToDTO(route.getArrivalAirport()))
                .distance(route.getDistance())
                .build();
    }

    private Flight convertToEntity(@NotNull FlightDTO flightDTO) {
        return Flight.builder()
                .id(flightDTO.getId())
                .departureDate(flightDTO.getDepartureDate())
                .price(flightDTO.getPrice())
                .build();
    }

    private CompanyDTO convertToDTO(@NotNull Company company) {
        return CompanyDTO.builder()
                .id(company.getId())
                .name(company.getName())
                .build();
    }

    private Company convertToEntity(@NotNull CompanyDTO companyDTO) {
        return Company.builder()
                .id(companyDTO.getId())
                .name(companyDTO.getName())
                .build();
    }

    private Route convertToEntity(@NotNull RouteDTO routeDTO) {
        return Route.builder()
                .id(routeDTO.getId())
                .departureAirport(convertToEntity(routeDTO.getDepartureAirport()))
                .arrivalAirport(convertToEntity(routeDTO.getArrivalAirport()))
                .distance((int) routeDTO.getDistance())
                .build();
    }

    private Airport convertToEntity(AirportDTO departureAirportDTO) {
        return Airport.builder()
                .id(departureAirportDTO.getId())
                .name(departureAirportDTO.getName())
                .country(departureAirportDTO.getCountry())
                .code(departureAirportDTO.getCode())
                .build();
    }

    public List<TicketDTO> getAllTickets() {
        List<Ticket> tickets = ticketRepository.findAll();
        return tickets.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public TicketDTO getTicketById(Long id) {
        return ticketRepository.findById(Math.toIntExact(id))
                .map(this::convertToDTO)
                .orElse(null);
    }

    public TicketDTO saveTicket(TicketDTO ticketDTO) {
        FlightDTO flightDTO = flightService.getFlightById(ticketDTO.getFlightId());
        if (flightDTO == null) {
            throw new IllegalArgumentException("Flight not found with id: " + ticketDTO.getFlightId());
        }

        Ticket ticket = convertToEntity(ticketDTO);
        ticket.getFlight().setAirline(convertToEntity(flightDTO.getAirline()));
        ticket.getFlight().setRoute(convertToEntity(flightDTO.getRoute()));
        ticket = ticketRepository.save(ticket);
        return convertToDTO(ticket);
    }

    public void deleteTicket(Long id) {
        ticketRepository.deleteById(Math.toIntExact(id));
    }

}
