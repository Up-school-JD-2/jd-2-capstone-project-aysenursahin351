package io.upschool.service;

import io.upschool.dto.BaseResponse;
import io.upschool.dto.airport.AirportSaveRequest;
import io.upschool.dto.airport.AirportSaveResponse;
import io.upschool.dto.airport.AirportUpdateRequest;
import io.upschool.entity.Airport;
import io.upschool.entity.Flight;
import io.upschool.entity.Route;
import io.upschool.exception.ResourceAlreadyDeletedException;
import io.upschool.exception.ResourceNotFoundException;
import io.upschool.repository.AirportRepository;
import io.upschool.repository.FlightRepository;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Lazy
public class AirportService {
    private final AirportRepository airportRepository;

    private FlightService flightService;
    private TicketService ticketService;
    @Autowired
    public AirportService(AirportRepository airportRepository, @Lazy FlightService flightService, @Lazy TicketService  ticketService) {
        this.airportRepository = airportRepository;
        this.flightService = flightService;
        this.ticketService = ticketService;
    }

    public BaseResponse<AirportSaveResponse> saveAirport(AirportSaveRequest airportRequest) {

        Airport airport = convertToEntity(airportRequest);

        airport = airportRepository.save(airport);
        AirportSaveResponse airportResponse = convertToResponse(airport);
        return BaseResponse.<AirportSaveResponse>builder()
                .status(200)
                .isSuccess(true)
                .data(airportResponse)
                .build();
    }
    public boolean areAirportsValid(long departureAirportId, long arrivalAirportId) {
        Optional<Airport> departureAirport = airportRepository.findById(departureAirportId);
        Optional<Airport> arrivalAirport = airportRepository.findById(arrivalAirportId);

        // Departure Airport ve Arrival Airport var mı kontrolü
        if (departureAirport.isEmpty() || arrivalAirport.isEmpty()) {
            return false;
        }

        // Status kontrolü
        if (departureAirport.get().getStatus() != 1 || arrivalAirport.get().getStatus() != 1) {
            return false;
        }

        return true;
    }

    public List<AirportSaveResponse> searchAirportsByName(String name) {
        List<Airport> airports = airportRepository.flexibleSearch(name);
        return airports.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<AirportSaveResponse> getAllAirports() {
        List<Airport> airports = airportRepository.findAll();
        return airports.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    public BaseResponse<AirportSaveResponse> deleteAirport(Long id) throws ResourceAlreadyDeletedException {
        Optional<Airport> optionalAirport = airportRepository.findById(id);

        if (optionalAirport.isPresent()) {
            Airport airport = optionalAirport.get();

            if (airport.getStatus() == 0) {
                throw new ResourceAlreadyDeletedException("Airport with id " + id + " is already deleted.");
            }
            if (this.flightService != null) {
                // Havalimanına bağlı uçuşları kontrol et ve sil
                List<Flight> flightsToDelete = airport.getDepartureRoutes().stream()
                        .flatMap(route -> route.getFlights().stream())
                        .collect(Collectors.toList());
                for (Flight flight : flightsToDelete) {

                    ticketService.batchUpdateTickets(flight.getTickets()); // Ticketları güncelle
                    flightService.updateFlightStatusToDeleted(flight);
                }
            }
            for (Route route : airport.getDepartureRoutes()) {
                route.setStatus(0);
                if (!route.getFlights().isEmpty()) {
                    for (Flight flight : route.getFlights()) {
                        if (flight != null) {
                            flight.setStatus(0);
                        }
                    }
                }        }

            for (Route route : airport.getArrivalRoutes()) {
                route.setStatus(0);
                if (!route.getFlights().isEmpty()) {
                    for (Flight flight : route.getFlights()) {
                        if (flight != null) {
                            flight.setStatus(0);
                        }
                    }
                }
            }

            // Havalimanını sil
            airport.setStatus(0); // Set the status to "deleted"
            airportRepository.save(airport);

            AirportSaveResponse response = convertToResponse(airport);

            return BaseResponse.<AirportSaveResponse>builder()
                    .status(200)
                    .isSuccess(true)
                    .data(response)
                    .build();
        } else {
            return BaseResponse.<AirportSaveResponse>builder()
                    .status(404)
                    .isSuccess(false)
                    .error("Airport not found with id: " + id)
                    .build();
        }
    }

    public Airport getAirportById4Repo(Long id) {
        return airportRepository.findById(id)
                .orElse(null); // Varsa uçuşu, yoksa null döndürür.
    }
    public AirportSaveResponse getAirportById(Long id) {
        Airport airport = airportRepository.findById(id).orElse(null);
        if (airport != null) {
            return convertToResponse(airport);
        } else {
            return null;
        }
    }

    public AirportSaveResponse convertToResponse(@NotNull Airport airport) {
        return AirportSaveResponse.builder()
                .id(airport.getId())
                .name(airport.getName())
                .country(airport.getCountry())
                .code(airport.getCode())
               .status(airport.getStatus())
                .build();
    }

    public Airport convertToEntity(AirportSaveRequest airportRequest) {
        return Airport.builder()
                .name(airportRequest.getName())
                .country(airportRequest.getCountry())
                .code(airportRequest.getCode())
               .status(1)
                .build();
    }

    public BaseResponse<AirportSaveResponse> updateAirport(Long id, @NotNull AirportUpdateRequest updateRequest) {
        Airport existingAirport = airportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Airport not found with id: " + id));

        existingAirport.setName(updateRequest.getName());
        existingAirport.setCountry(updateRequest.getCountry());
        existingAirport.setCode(updateRequest.getCode());

        Airport updatedAirport = airportRepository.save(existingAirport);

        AirportSaveResponse response = convertToResponse(updatedAirport);

        return BaseResponse.<AirportSaveResponse>builder()
                .status(200)
                .isSuccess(true)
                .data(response)
                .build();
    }
}
