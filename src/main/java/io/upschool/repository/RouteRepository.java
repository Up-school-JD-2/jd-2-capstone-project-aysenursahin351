package io.upschool.repository;
import io.upschool.entity.Airport;
import io.upschool.entity.Flight;
import io.upschool.entity.Route;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {
    @Query("SELECT a FROM Route a WHERE a.departureAirport LIKE %:keyword% OR a.arrivalAirport LIKE %:keyword%")
    List<Route> flexibleSearch(@Param("keyword") String keyword);
    @Modifying
    @Query("UPDATE Route r SET r.flights = :flights WHERE r.id = :routeId")
    void updateRouteFlights(@Param("routeId") Long routeId, @Param("flights") List<Flight> flights);

}