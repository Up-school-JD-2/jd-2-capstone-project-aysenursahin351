package io.upschool.service;

import io.upschool.dto.company.CompanySaveResponse;
import io.upschool.dto.flight.FlightSaveRequest;
import io.upschool.dto.flight.FlightSaveResponse;
import io.upschool.dto.flight.FlightUpdateRequest;
import io.upschool.dto.BaseResponse;
import io.upschool.dto.route.RouteSaveResponse;
import io.upschool.entity.Company;
import io.upschool.entity.Flight;
import io.upschool.entity.Route;
import io.upschool.entity.Ticket;
import io.upschool.exception.ResourceAlreadyDeletedException;
import io.upschool.exception.ResourceNotFoundException;
import io.upschool.repository.FlightRepository;
import io.upschool.repository.TicketRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor

public class FlightService {

    private final FlightRepository flightRepository;
    private final RouteService routeService;
    private TicketService ticketService;
    private final CompanyService companyService;

    @Autowired
    public FlightService(FlightRepository flightRepository, RouteService routeService,
                         @Lazy TicketService ticketService, CompanyService companyService) {
        this.flightRepository = flightRepository;
        this.routeService = routeService;
        this.ticketService = ticketService;
        this.companyService = companyService;
    }

    public BaseResponse<FlightSaveResponse> saveFlight(FlightSaveRequest flightRequest) {
        Flight flight = convertToEntity(flightRequest);
        flight.setAvailableSeats(flightRequest.getTotalSeats());

        if (routeService.areRouteValid(flightRequest.getRouteId())) {
             flight = convertToEntity(flightRequest);

            flight = flightRepository.save(flight);
            Route route;
            RouteSaveResponse routeR = routeService.getRouteById(flightRequest.getRouteId()).getData();
            if (routeR != null) {
                route = routeService.convertToEntityR(routeR);

                if (route.getFlights() == null) {
                    route.setFlights(new ArrayList<>()); // Eğer uçuş listesi null ise, yeni bir liste oluştur
                }

                List<Flight> flights = route.getFlights(); // Mevcut flight listesini al
                flights.add(flight); // Yeni flight'ı ekle

                routeService.updateRouteFlights(route.getId(), flights);
            }

            FlightSaveResponse flightResponse = convertToResponse(flight);
            return BaseResponse.<FlightSaveResponse>builder()
                    .status(200)
                    .isSuccess(true)
                    .data(flightResponse)
                    .build();
        }
       else{
           return BaseResponse.<FlightSaveResponse>builder()
                   .status(400)
                   .isSuccess(false)
                   .error("No valid routes found for the given flight.")
                   .build();
       }

    }

    public void updateFlightStatusToDeleted(Flight flight) {
        flight.setStatus(0);
        flightRepository.save(flight);
    }

    public void batchUpdateFlights(List<Flight> flightsToUpdate) {
        if (flightsToUpdate != null && !flightsToUpdate.isEmpty()) {
            List<Flight> updatedFlights = new ArrayList<>();
            for (Flight flight : flightsToUpdate) {
                flight.setStatus(0);
                updatedFlights.add(flight);
            }
            flightRepository.saveAll(updatedFlights);
        }
    }
    public Flight getFlightById4Repo(Long id) {
        return flightRepository.findById(id)
                .orElse(null); // Varsa uçuşu, yoksa null döndürür.
    }
    public Flight convertToEntityFromResponse(Long flightId) {
        FlightSaveResponse flightResponse = getFlightById(flightId).getData();
        if (flightResponse == null) {
            throw new IllegalArgumentException("Flight not found with id: " + flightId);
        }
        return convertResponseToEntity(flightResponse);
    }

    public void deleteRepo(Flight flight) {

        flightRepository.delete(flight);
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
                .companyId(flight.getCompany().getId())
                .routeId(flight.getRoute().getId())
                .departureDate(flight.getDepartureDate())
                .price(flight.getPrice())
                .status(flight.getStatus())
                .totalSeats(flight.getTotalSeats())
                .ticketsSold(flight.getTicketsSold())
                .name(flight.getName())
                .build();
    }

public BaseResponse<Void> deleteFlight(Long id) throws ResourceAlreadyDeletedException {
    Optional<Flight> optionalFlight = flightRepository.findById(id);

    if (optionalFlight.isPresent()) {
        Flight flight = optionalFlight.get();

        if (flight.getStatus() == 0) {
            return BaseResponse.<Void>builder()
                    .status(HttpStatus.NOT_FOUND.value())
                    .isSuccess(false)
                    .error("Flight not found")
                    .build();
        }

        // Set the status to "deleted" and save
        flight.setStatus(0);
        flightRepository.save(flight);
        // Uçuşa bağlı biletleri kontrol et ve sil
        if (ticketService.getTicketsByFlightId(flight.getId())!=null) {
            for (Ticket ticket : flight.getTickets()) {
                ticketService.updateTicketStatusToDeleted(ticket);
            }
        }


        return BaseResponse.<Void>builder()
                .status(200)
                .isSuccess(true)
                .build();

    } else {
        // Flight not found, return appropriate response
        return BaseResponse.<Void>builder()
                .status(HttpStatus.NOT_FOUND.value())
                .isSuccess(false)
                .error("Flight not found")
                .build();    }
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
    public BaseResponse<FlightSaveResponse> updateFlight(Long id, FlightUpdateRequest flightRequest) {
        Optional<Flight> optionalFlight = flightRepository.findById(id);

        if (optionalFlight.isPresent()) {
            Flight flight = optionalFlight.get();
            Optional<Route> optionalRoute = Optional.ofNullable(routeService.getRouteById4Repo(flightRequest.getRouteId()));
            if (optionalRoute.isPresent()) {
                Route route = optionalRoute.get();
                flight.setRoute(route);
            } else {
                throw new ResourceNotFoundException("Route not found with id: " + flightRequest.getRouteId());
            }

            // Update flight properties based on flightRequest
            flight.setName(flightRequest.getName());
            flight.setTotalSeats(flightRequest.getTotalSeats());
            flight.setDepartureDate(flightRequest.getDepartureDate());
            flight.setPrice(flightRequest.getPrice());
            flight = flightRepository.save(flight);
            FlightSaveResponse flightResponse = convertToResponse(flight);

            return BaseResponse.<FlightSaveResponse>builder()
                    .status(HttpStatus.OK.value())
                    .isSuccess(true)
                    .data(flightResponse)
                    .build();
        } else {
            return BaseResponse.<FlightSaveResponse>builder()
                    .status(HttpStatus.NOT_FOUND.value())
                    .isSuccess(false)
                    .error("Flight not found with id: " + id)
                    .build();
        }
    }

    public List<FlightSaveResponse> searchCompanyByName(String name) {
        List<Flight> companies = flightRepository.flexibleSearch(name);
        return companies.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public Flight convertToEntity(FlightSaveRequest flightRequest) {
        Company company = companyService.getCompanyById4Repo(flightRequest.getCompanyId());
        if (company == null) {
            throw new EntityNotFoundException("Company not found with id: " + flightRequest.getCompanyId());
        }
        Route route = routeService.getRouteById4Repo(flightRequest.getRouteId());

        return Flight.builder()
                .company(company)
                .route(route)
                .departureDate(flightRequest.getDepartureDate())
                .price(flightRequest.getPrice())
                .status(1)
                .name(flightRequest.getName())
                .totalSeats(flightRequest.getTotalSeats())
                .availableSeats(flightRequest.getTotalSeats())  //deneme
                .build();
    }
    public Flight convertToEntityU(FlightUpdateRequest flightRequest) {
        // Veritabanından routeId ile Route nesnesini çekme işlemi
        Route route = routeService.getRouteById4Repo(flightRequest.getRouteId());
        if (route == null) {
            throw new EntityNotFoundException("Route not found with id: " + flightRequest.getRouteId());
        }
        return Flight.builder()
                .route(route) // Doğru şekilde Route nesnesini atıyoruz
                .departureDate(flightRequest.getDepartureDate())
                .price(flightRequest.getPrice())
                .status(1)
                .name(flightRequest.getName())
                .totalSeats(flightRequest.getTotalSeats())
                .build();
    }

    public Flight convertResponseToEntity(FlightSaveResponse flightResponse) {
        // Veritabanından companyId ile Company nesnesini çekme işlemi
        Company company = companyService.getCompanyById4Repo(flightResponse.getCompanyId());
        if (company == null) {
            throw new EntityNotFoundException("Company not found with id: " + flightResponse.getCompanyId());
        }
        Route route = routeService.getRouteById4Repo(flightResponse.getRouteId());
        if (route == null) {
            throw new EntityNotFoundException("Route not found with id: " + flightResponse.getRouteId());
        }
        return Flight.builder()
                .company(company) // Doğru şekilde Company nesnesini atıyoruz
                .route(route)
                .departureDate(flightResponse.getDepartureDate())
                .price(flightResponse.getPrice())
                .status(1)
                .totalSeats(flightResponse.getTotalSeats())
                .name(flightResponse.getName())
                .ticketsSold(flightResponse.getTicketsSold())
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
