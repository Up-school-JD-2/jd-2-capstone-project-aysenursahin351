package io.upschool.controller;

import io.upschool.dto.airport.AirportSaveResponse;
import io.upschool.dto.route.RouteSaveRequest;
import io.upschool.dto.route.RouteSaveResponse;
import io.upschool.dto.BaseResponse;
import io.upschool.dto.route.RouteUpdateRequest;
import io.upschool.service.RouteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/routes")
@RequiredArgsConstructor
public class RouteController {
    private final RouteService routeService;
    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<RouteSaveResponse>> getRouteById(@PathVariable Long id) {
        BaseResponse<RouteSaveResponse> routeResponse = routeService.getRouteById(id);
        if (routeResponse.isSuccess()) {
            return ResponseEntity.ok(routeResponse);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("/search/routes")
    public ResponseEntity<List<RouteSaveResponse>> searchRoutes(@RequestParam String keyword) {
        List<RouteSaveResponse> searchResults = routeService.searchRoutesByName(keyword);
        return ResponseEntity.ok(searchResults);
    }
    @GetMapping
    public ResponseEntity<BaseResponse<List<RouteSaveResponse>>> getAllRoutes() {
        BaseResponse<List<RouteSaveResponse>> routeResponse = routeService.getAllRoutes();
        return ResponseEntity.ok(routeResponse);
    }
    @PostMapping
    public ResponseEntity<BaseResponse<RouteSaveResponse>> saveRoute(@RequestBody RouteSaveRequest routeRequest) {
        BaseResponse<RouteSaveResponse> savedRoute = routeService.saveRoute(routeRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedRoute);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> deleteRoute(@PathVariable Long id) {
        BaseResponse<Void> response = routeService.softDeleteRoute(id);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<RouteSaveResponse>> updateRoute(@PathVariable Long id, @RequestBody RouteUpdateRequest routeUpdateRequest) {
        routeUpdateRequest.setId(id);
        BaseResponse<RouteSaveResponse> response = routeService.updateRoute(routeUpdateRequest);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

}
