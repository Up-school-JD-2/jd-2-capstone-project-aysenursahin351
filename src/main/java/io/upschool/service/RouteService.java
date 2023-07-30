package io.upschool.service;

import io.upschool.dto.AirportDTO;
import io.upschool.dto.RouteDTO;
import io.upschool.entity.Airport;
import io.upschool.entity.Route;
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
public class RouteService {
    private final RouteRepository routeRepository;

    public List<RouteDTO> getAllRoutes() {
        List<Route> routes = routeRepository.findAll();
        return routes.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public RouteDTO getRouteById(Long id) {
        return routeRepository.findById(id)
                .map(this::convertToDTO)
                .orElse(null);
    }

    public RouteDTO saveRoute(RouteDTO routeDTO) {
        Route route = convertToEntity(routeDTO);
        route = routeRepository.save(route);
        return convertToDTO(route);
    }

    public void deleteRoute(Long id) {
        routeRepository.deleteById(id);
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
