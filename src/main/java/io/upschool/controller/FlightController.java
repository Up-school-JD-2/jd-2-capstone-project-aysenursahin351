package io.upschool.controller;

import io.upschool.dto.flight.FlightSaveRequest;
import io.upschool.dto.flight.FlightSaveResponse;
import io.upschool.dto.BaseResponse;
import io.upschool.dto.flight.FlightUpdateRequest;
import io.upschool.exception.ResourceAlreadyDeletedException;
import io.upschool.service.FlightService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/flights")
@RequiredArgsConstructor
public class FlightController {
    private final FlightService flightService;

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<FlightSaveResponse>> getFlightById(@PathVariable Long id) {
        BaseResponse<FlightSaveResponse> FlightSaveResponse = flightService.getFlightById(id);
        if (FlightSaveResponse.isSuccess()) {
            return ResponseEntity.ok(FlightSaveResponse);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping
    public ResponseEntity<BaseResponse<List<FlightSaveResponse>>> getAllFlights() {
        List<FlightSaveResponse> flights = flightService.getAllFlights();
        return ResponseEntity.ok(BaseResponse.<List<FlightSaveResponse>>builder()
                .status(200)
                .isSuccess(true)
                .data(flights)
                .build());
    }
    @GetMapping("/search")
    public ResponseEntity<List<FlightSaveResponse>> searchCompanies(@RequestParam String keyword) {
        List<FlightSaveResponse> searchResults = flightService.searchCompanyByName(keyword);
        return ResponseEntity.ok(searchResults);
    }

    @PostMapping
    public ResponseEntity<BaseResponse<FlightSaveResponse>> saveFlight(@RequestBody FlightSaveRequest flightRequest) {
        BaseResponse<FlightSaveResponse> savedFlight = flightService.saveFlight(flightRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedFlight);
    }
    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<FlightSaveResponse>> updateFlight(
            @PathVariable Long id,
            @RequestBody FlightUpdateRequest flightRequest
    ) {
        BaseResponse<FlightSaveResponse> updatedFlight = flightService.updateFlight(id, flightRequest);
        if (updatedFlight.isSuccess()) {
            return ResponseEntity.ok(updatedFlight);
        } else {
            return ResponseEntity.status(updatedFlight.getStatus()).body(updatedFlight);
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> deleteFlight(@PathVariable Long id) throws ResourceAlreadyDeletedException {
        BaseResponse<Void> response = flightService.deleteFlight(id);
        return ResponseEntity.status(response.getStatus()).body(response);
    }


}
