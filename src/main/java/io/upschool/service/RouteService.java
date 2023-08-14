package io.upschool.service;

import io.upschool.dto.airport.AirportSaveResponse;
import io.upschool.dto.route.RouteSaveRequest;
import io.upschool.dto.route.RouteSaveResponse;
import io.upschool.dto.BaseResponse;
import io.upschool.dto.route.RouteUpdateRequest;
import io.upschool.entity.Airport;
import io.upschool.entity.Flight;
import io.upschool.entity.Route;
import io.upschool.exception.ResourceNotFoundException;
import io.upschool.repository.RouteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Lazy

public class RouteService {
    private final RouteRepository routeRepository;
    private final AirportService airportService;
    private final FlightService flightService;
    private final  TicketService ticketService;

    @Autowired
    public RouteService(RouteRepository routeRepository, AirportService airportService, @Lazy FlightService flightService, @Lazy TicketService ticketService) {
        this.routeRepository = routeRepository;
        this.airportService = airportService;
        this.flightService = flightService;
        this.ticketService = ticketService;
    }
    public BaseResponse<RouteSaveResponse> getRouteById(Long id) {
        Route route = routeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Route not found with id : " + id));
        return BaseResponse.<RouteSaveResponse>builder()
                .status(200)
                .isSuccess(true)
                .data(convertToResponse(route))
                .build();
    }
    public Route getRouteById4Repo(Long id) {
        return routeRepository.findById(id)
                .orElse(null); // Varsa uçuşu, yoksa null döndürür.
    }
    public boolean areRouteValid(long routeId) {
        Optional<Route> routeValid = routeRepository.findById(routeId);

        if (routeValid.isEmpty() ) {
            return false;
        }

        // Status kontrolü
        if (routeValid.get().getStatus() != 1 ) {
            return false;
        }

        return true;
    }
    public List<RouteSaveResponse> searchRoutesByName(String name) {
        List<Route> routes = routeRepository.flexibleSearch(name);
        return routes.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public BaseResponse<RouteSaveResponse> saveRoute(RouteSaveRequest routeRequest) {
        long arrivalAirportId = routeRequest.getArrivalAirportId();
        long departureAirportId = routeRequest.getDepartureAirportId();

        if (airportService.areAirportsValid(arrivalAirportId, departureAirportId)) {
            Route route = convertToEntity(routeRequest);
            route = routeRepository.save(route);

            return BaseResponse.<RouteSaveResponse>builder()
                    .status(200)
                    .isSuccess(true)
                    .data(convertToResponse(route))
                    .build();
        } else {
            return BaseResponse.<RouteSaveResponse>builder()
                    .status(400)
                    .isSuccess(false)
                    .error("No valid airports found for the given route.")
                    .build();
        }
    }
    public BaseResponse<RouteSaveResponse> updateRoute(RouteUpdateRequest routeUpdateRequest) {
        Long routeId = routeUpdateRequest.getId();
        Route routeToUpdate = routeRepository.findById(routeId)
                .orElseThrow(() -> new ResourceNotFoundException("Route not found with id: " + routeId));

        Airport departureAirport = convertToEntity(routeUpdateRequest.getDepartureAirportId());
        Airport arrivalAirport = convertToEntity(routeUpdateRequest.getArrivalAirportId());

        routeToUpdate.setDepartureAirport(departureAirport);
        routeToUpdate.setArrivalAirport(arrivalAirport);
        routeToUpdate.setDistance((int) routeUpdateRequest.getDistance());

        // Save the updated route
        Route updatedRoute = routeRepository.save(routeToUpdate);

        return BaseResponse.<RouteSaveResponse>builder()
                .status(200)
                .isSuccess(true)
                .data(convertToResponse(updatedRoute))
                .build();
    }
public BaseResponse<Void> softDeleteRoute(Long id) {
    try {
        Route route = routeRepository.findById(id).orElse(null);

        if (route != null) {
            // Soft delete the route
            route.setStatus(0);
            routeRepository.save(route);

            // Set flight statuses to "0"
            List<Flight> flights = route.getFlights();
            ticketService.batchUpdateFlightsAndTickets(flights);

            return BaseResponse.<Void>builder()
                    .status(200)
                    .isSuccess(true)
                    .data(null)
                    .build();
        } else {
            return BaseResponse.<Void>builder()
                    .status(404)
                    .isSuccess(false)
                    .error("Route not found")
                    .data(null)
                    .build();
        }
    } catch (Exception e) {
        return BaseResponse.<Void>builder()
                .status(500)
                .isSuccess(false)
                .error("Route deletion failed")
                .data(null)
                .build();
    }
}

    private RouteSaveResponse convertToResponse(Route route) {
        return RouteSaveResponse.builder()
                .id(route.getId())
                .departureAirport(convertToResponse(route.getDepartureAirport()))
                .arrivalAirport(convertToResponse(route.getArrivalAirport()))
                .distance(route.getDistance())
                .status(route.getStatus())
                .build();
    }
    public Route convertToEntityR(RouteSaveResponse routeResponse) {
        return Route.builder()
                .id(routeResponse.getId())
                .departureAirport(convertToEntity(routeResponse.getDepartureAirport()))
                .arrivalAirport(convertToEntity(routeResponse.getArrivalAirport()))
                .distance((int) routeResponse.getDistance())
                .status(routeResponse.getStatus())
                .build();
    }
    public void updateRouteFlights(Long routeId, List<Flight> flights) {
        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new ResourceNotFoundException("Route not found with id: " + routeId));

        if (route.getFlights() == null) {
            route.setFlights(new ArrayList<>()); // Yeni bir liste oluşturarak atanıyor
        }

        route.getFlights().addAll(flights);

        routeRepository.save(route);
    }

    private Airport convertToEntity(Long airportId) {
        AirportSaveResponse airportResponse = airportService.getAirportById(airportId);
        if (airportResponse != null) {
            return convertToEntity(airportResponse);
        } else {
            throw new IllegalArgumentException("Airport not found with id: " + airportId);
        }
    }


    public BaseResponse<List<RouteSaveResponse>> getAllRoutes() {
        List<Route> routes = routeRepository.findAll();
        List<RouteSaveResponse> routeResponses = routes.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return BaseResponse.<List<RouteSaveResponse>>builder()
                .status(200)
                .isSuccess(true)
                .data(routeResponses)
                .build();
    }
    private Airport convertToEntity(AirportSaveResponse airportResponse) {
        if (airportResponse == null) {
            return null;
        }

        Airport airport = new Airport();
        airport.setId(airportResponse.getId());
        airport.setName(airportResponse.getName());
        airport.setCountry(airportResponse.getCountry());
        airport.setCode(airportResponse.getCode());
        // status

        return airport;
    }

    public Route convertToEntity(RouteSaveRequest routeRequest) {
        Airport departureAirport = convertToEntity(routeRequest.getDepartureAirportId());
        Airport arrivalAirport = convertToEntity(routeRequest.getArrivalAirportId());

        // Set "code" fields of airports
        departureAirport.setCode(routeRequest.getCode());
        arrivalAirport.setCode(routeRequest.getCode());

        return Route.builder()
                .departureAirport(departureAirport)
                .arrivalAirport(arrivalAirport)
                .distance((int) routeRequest.getDistance())
                .status(1)
                .build();
    }


    private AirportSaveResponse convertToResponse(Airport airport) {
        if (airport == null||airport.getStatus()==0) {
            return null;
        }

        return AirportSaveResponse.builder()
                .id(airport.getId())
                .name(airport.getName())
                .country(airport.getCountry())
                .code(airport.getCode())
                .status(airport.getStatus())
                .build();
    }
}
