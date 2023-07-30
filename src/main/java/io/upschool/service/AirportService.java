package io.upschool.service;

import io.upschool.repository.AirportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
public class AirportService {
    private final AirportRepository airportRepository;

    public List<AirportDTO> getAllAirports() {
        List<Airport> airports = airportRepository.findAll();
        return airports.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public AirportDTO getAirportById(Long id) {
        return airportRepository.findById(id)
                .map(this::convertToDTO)
                .orElse(null);
    }

    public AirportDTO saveAirport(AirportDTO airportDTO) {
        Airport airport = convertToEntity(airportDTO);
        airport = airportRepository.save(airport);
        return convertToDTO(airport);
    }

    public void deleteAirport(Long id) {
        airportRepository.deleteById(id);
    }

    private AirportDTO convertToDTO(Airport airport) {
        return AirportDTO.builder()
                .id(airport.getId())
                .name(airport.getName())
                .country(airport.getCountry())
                .code(airport.getCode())
                .build();
    }

    private Airport convertToEntity(AirportDTO airportDTO) {
        return Airport.builder()
                .id(airportDTO.getId())
                .name(airportDTO.getName())
                .country(airportDTO.getCountry())
                .code(airportDTO.getCode())
                .build();
    }
}
