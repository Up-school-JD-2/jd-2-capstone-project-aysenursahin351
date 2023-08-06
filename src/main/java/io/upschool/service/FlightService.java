package io.upschool.service;

import io.upschool.dto.flight.FlightSaveRequest;
import io.upschool.dto.flight.FlightSaveResponse;
import io.upschool.dto.ticket.TicketSaveRequest;
import io.upschool.dto.ticket.TicketSaveResponse;
import io.upschool.dto.BaseResponse;
import io.upschool.entity.Flight;
import io.upschool.repository.FlightRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class FlightService {
    private final FlightRepository flightRepository;

    public BaseResponse<FlightSaveResponse> saveFlight(FlightSaveRequest flightRequest) {
        Flight flight = convertToEntity(flightRequest);
        flight = flightRepository.save(flight);
        FlightSaveResponse flightResponse = convertToResponse(flight);
        return BaseResponse.<FlightSaveResponse>builder()
                .status(200)
                .isSuccess(true)
                .data(flightResponse)
                .build();
    }
    public Flight convertToEntityFromResponse(Long flightId) {
        FlightSaveResponse flightResponse = getFlightById(flightId).getData();
        if (flightResponse == null) {
            throw new IllegalArgumentException("Flight not found with id: " + flightId);
        }
        return convertResponseToEntity(flightResponse);
    }
    public List<FlightSaveResponse> getAllFlights() {
        List<Flight> flights = flightRepository.findAll();
        return flights.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    private FlightSaveResponse convertToResponse(Flight flight) {
        return FlightSaveResponse.builder()
                .id(flight.getId())
                .companyId(flight.getCompanyId())
                .routeId(flight.getRouteId())
                .departureDate(flight.getDepartureDate())
                .price(flight.getPrice())
                .build();
    }
    public void deleteFlight(Long id) {
        flightRepository.deleteById(id);
    }

    public BaseResponse<FlightSaveResponse> getFlightById(Long id) {
        Flight flight = flightRepository.findById(id).orElse(null);
        if (flight != null) {
            FlightSaveResponse flightResponse = convertToResponse(flight);
            return BaseResponse.<FlightSaveResponse>builder()
                    .status(200)
                    .isSuccess(true)
                    .data(flightResponse)
                    .build();
        } else {
            return BaseResponse.<FlightSaveResponse>builder()
                    .status(404)
                    .isSuccess(false)
                    .error("Flight not found")
                    .build();
        }
    }


    public Flight convertToEntity(FlightSaveRequest flightRequest) {
        return Flight.builder()
                .companyId(flightRequest.getCompanyId())
                .routeId(flightRequest.getRouteId())
                .departureDate(flightRequest.getDepartureDate())
                .price(flightRequest.getPrice())
                .build();
    }

    public Flight convertResponseToEntity(FlightSaveResponse flightResponse) {
        return Flight.builder()
                .companyId(flightResponse.getCompanyId())
                .routeId(flightResponse.getRouteId())
                .departureDate(flightResponse.getDepartureDate())
                .price(flightResponse.getPrice())
                .build();
    }

    public Flight convertToEntityFromResponse(Long flightId, FlightService flightService) {
        FlightSaveResponse flightResponse = flightService.getFlightById(flightId).getData();
        if (flightResponse == null) {
            throw new IllegalArgumentException("Flight not found with id: " + flightId);
        }
        return convertResponseToEntity(flightResponse);
    }



}
