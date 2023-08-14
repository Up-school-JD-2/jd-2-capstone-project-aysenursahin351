package io.upschool.repository;
import io.upschool.entity.Airport;
import io.upschool.entity.Company;
import io.upschool.entity.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> {
    @Query("SELECT c FROM Flight c WHERE c.name LIKE %:keyword%")
    List<Flight> flexibleSearch(@Param("keyword") String keyword);
}

