package io.upschool.repository;

import io.upschool.entity.Airport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public interface AirportRepository extends JpaRepository<Airport, Long> {
    @Query("SELECT a FROM Airport a WHERE a.name LIKE %:keyword% OR a.country LIKE %:keyword% OR a.code LIKE %:keyword%")
    List<Airport> flexibleSearch(@Param("keyword") String keyword);
}
