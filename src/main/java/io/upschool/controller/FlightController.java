package io.upschool.controller;

import io.upschool.dto.flight.FlightSaveRequest;
import io.upschool.dto.flight.FlightSaveResponse;
import io.upschool.dto.BaseResponse;
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
    @PostMapping
    public ResponseEntity<BaseResponse<FlightSaveResponse>> saveFlight(@RequestBody FlightSaveRequest flightRequest) {
        BaseResponse<FlightSaveResponse> savedFlight = flightService.saveFlight(flightRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedFlight);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFlight(@PathVariable Long id) {
        flightService.deleteFlight(id);
        return ResponseEntity.noContent().build();
    }
}
