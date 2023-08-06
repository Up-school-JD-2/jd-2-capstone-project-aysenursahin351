package io.upschool.service;

import io.upschool.dto.airport.AirportSaveResponse;
import io.upschool.dto.route.RouteSaveRequest;
import io.upschool.dto.route.RouteSaveResponse;
import io.upschool.dto.BaseResponse;
import io.upschool.entity.Airport;
import io.upschool.entity.Route;
import io.upschool.exception.ResourceNotFoundException;
import io.upschool.repository.RouteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class RouteService {
    private final RouteRepository routeRepository;
    private final AirportService airportService;

    public BaseResponse<RouteSaveResponse> getRouteById(Long id) {
        Route route = routeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Route not found with id : " + id));
        return BaseResponse.<RouteSaveResponse>builder()
                .status(200)
                .isSuccess(true)
                .data(convertToResponse(route))
                .build();
    }

    public BaseResponse<RouteSaveResponse> saveRoute(RouteSaveRequest routeRequest) {
        Route route = convertToEntity(routeRequest);
        route = routeRepository.save(route);
        return BaseResponse.<RouteSaveResponse>builder()
                .status(200)
                .isSuccess(true)
                .data(convertToResponse(route))
                .build();
    }

    public BaseResponse<Void> deleteRoute(Long id) {
        try {
            // Route silme işlemleri
            routeRepository.deleteById(id);

            return BaseResponse.<Void>builder()
                    .status(200)
                    .isSuccess(true)
                    .data(null)
                    .build();
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
                .build();
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
        // Set other fields if needed

        return airport;
    }

    private Route convertToEntity(RouteSaveRequest routeRequest) {
        Airport departureAirport = convertToEntity(routeRequest.getDepartureAirportId());
        Airport arrivalAirport = convertToEntity(routeRequest.getArrivalAirportId());

        // Set "code" fields of airports
        departureAirport.setCode(routeRequest.getCode());
        arrivalAirport.setCode(routeRequest.getCode());

        return Route.builder()
                .departureAirport(departureAirport)
                .arrivalAirport(arrivalAirport)
                .distance((int) routeRequest.getDistance())
                .build();
    }


    private AirportSaveResponse convertToResponse(Airport airport) {
        if (airport == null) {
            return null;
        }

        return AirportSaveResponse.builder()
                .id(airport.getId())
                .name(airport.getName())
                .country(airport.getCountry())
                .code(airport.getCode())
                .build();
    }
}
