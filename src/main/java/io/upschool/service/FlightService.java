package io.upschool.service;
import io.upschool.dto.AirportDTO;
import io.upschool.dto.FlightDTO;
import io.upschool.dto.RouteDTO;
import io.upschool.entity.Airport;
import io.upschool.entity.Company;
import io.upschool.entity.Flight;
import io.upschool.entity.Route;
import io.upschool.repository.FlightRepository;
import io.upschool.repository.RouteRepository;
import org.jetbrains.annotations.NotNull;
import io.upschool.dto.CompanyDTO;
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
public class FlightService {
    private final FlightRepository flightRepository;

    public List<FlightDTO> getAllFlights() {
        List<Flight> flights = flightRepository.findAll();
        return flights.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public FlightDTO getFlightById(Long id) {
        return flightRepository.findById(id)
                .map(this::convertToDTO)
                .orElse(null);
    }

    public FlightDTO saveFlight(FlightDTO flightDTO) {
        Flight flight = convertToEntity(flightDTO);
        flight = flightRepository.save(flight);
        return convertToDTO(flight);
    }

    public void deleteFlight(Long id) {
        flightRepository.deleteById(id);
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

    private Flight convertToEntity(@NotNull FlightDTO flightDTO) {
        return Flight.builder()
                .id(flightDTO.getId())
                .airline(convertToEntity(flightDTO.getAirline()))
                .route(convertToEntity(flightDTO.getRoute()))
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

    private RouteDTO convertToDTO(@NotNull Route route) {
        return RouteDTO.builder()
                .id(route.getId())
                .departureAirport(convertToDTO(route.getDepartureAirport()))
                .arrivalAirport(convertToDTO(route.getArrivalAirport()))
                .distance(route.getDistance())
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

    private AirportDTO convertToDTO(@NotNull Airport airport) {
        return AirportDTO.builder()
                .id(airport.getId())
                .name(airport.getName())
                .country(airport.getCountry())
                .code(airport.getCode())
                .build();
    }

    private Airport convertToEntity(@NotNull AirportDTO airportDTO) {
        return Airport.builder()
                .id(airportDTO.getId())
                .name(airportDTO.getName())
                .country(airportDTO.getCountry())
                .code(airportDTO.getCode())
                .build();
    }
}
