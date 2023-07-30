package io.upschool.repository;
import io.upschool.entity.Route;
import org.springframework.data.jpa.repository.JpaRepository;


public interface RouteRepository extends JpaRepository<Route, Long> {
    // Custom query methods can be added if needed
}