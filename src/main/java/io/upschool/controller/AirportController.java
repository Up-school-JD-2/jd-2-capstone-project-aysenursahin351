package io.upschool.controller;

import io.upschool.dto.airport.AirportSaveRequest;
import io.upschool.dto.airport.AirportSaveResponse;
import io.upschool.dto.BaseResponse;
import io.upschool.dto.airport.AirportUpdateRequest;
import io.upschool.exception.ResourceAlreadyDeletedException;
import io.upschool.exception.ResourceNotFoundException;
import io.upschool.service.AirportService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/airports")
@RequiredArgsConstructor
public class AirportController {
    private final AirportService airportService;

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<AirportSaveResponse>> getAirportById(@PathVariable Long id) {
        AirportSaveResponse airportResponse = airportService.getAirportById(id);
        if (airportResponse != null) {
            BaseResponse<AirportSaveResponse> response = BaseResponse.<AirportSaveResponse>builder()
                    .status(200)
                    .isSuccess(true)
                    .data(airportResponse)
                    .build();
            return ResponseEntity.ok(response);
        } else {
            BaseResponse<AirportSaveResponse> response = BaseResponse.<AirportSaveResponse>builder()
                    .status(404)
                    .isSuccess(false)
                    .error("Airport not found with id: " + id)
                    .build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @GetMapping
    public ResponseEntity<BaseResponse<List<AirportSaveResponse>>> getAllAirports() {
        List<AirportSaveResponse> airports = airportService.getAllAirports();
        return ResponseEntity.ok(BaseResponse.<List<AirportSaveResponse>>builder()
                .status(200)
                .isSuccess(true)
                .data(airports)
                .build());
    }
    @PostMapping
    public ResponseEntity<BaseResponse<AirportSaveResponse>> saveAirport(@RequestBody AirportSaveRequest airportRequest) {
        BaseResponse<AirportSaveResponse> savedAirport = airportService.saveAirport(airportRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedAirport);
    }
    @GetMapping("/search/airports")
    public ResponseEntity<List<AirportSaveResponse>> searchAirports(@RequestParam String keyword) {
        List<AirportSaveResponse> searchResults = airportService.searchAirportsByName(keyword);
        return ResponseEntity.ok(searchResults);
    }
    @PutMapping("/update/{id}")
    public ResponseEntity<BaseResponse<AirportSaveResponse>> updateAirport(
            @PathVariable Long id,
            @RequestBody AirportUpdateRequest updateRequest) {

        BaseResponse<AirportSaveResponse> response = airportService.updateAirport(id, updateRequest);

        return ResponseEntity.status(response.getStatus()).body(response);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<AirportSaveResponse>> deleteAirport(@PathVariable Long id) throws ResourceAlreadyDeletedException {
        BaseResponse<AirportSaveResponse> response = airportService.deleteAirport(id);
        return ResponseEntity.status(response.getStatus()).body(response);
    }


}
