package io.upschool.repository;
import io.upschool.entity.Flight;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FlightRepository extends JpaRepository<Flight, Long> {
    // Custom query methods can be added if needed
}

