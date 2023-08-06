package io.upschool.service;

import io.upschool.dto.BaseResponse;
import io.upschool.dto.airport.AirportSaveRequest;
import io.upschool.dto.airport.AirportSaveResponse;
import io.upschool.entity.Airport;
import io.upschool.repository.AirportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AirportService {
    private final AirportRepository airportRepository;

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
    public List<AirportSaveResponse> getAllAirports() {
        List<Airport> airports = airportRepository.findAll();
        return airports.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }


    public void deleteAirport(Long id) {
        airportRepository.deleteById(id);
    }

    public AirportSaveResponse getAirportById(Long id) {
        Airport airport = airportRepository.findById(id).orElse(null);
        if (airport != null) {
            return convertToResponse(airport);
        } else {
            return null;
        }
    }




    public AirportSaveResponse convertToResponse(Airport airport) {
        return AirportSaveResponse.builder()
                .id(airport.getId())
                .name(airport.getName())
                .country(airport.getCountry())
                .build();
    }

    public Airport convertToEntity(AirportSaveRequest airportRequest) {
        return Airport.builder()
                .name(airportRequest.getName())
                .country(airportRequest.getCountry())
                .code(airportRequest.getCode())
                .build();
    }
}
