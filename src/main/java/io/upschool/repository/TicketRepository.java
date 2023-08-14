package io.upschool.repository;

import io.upschool.entity.Flight;
import io.upschool.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByPnrStartingWith(String pnr);
    List<Ticket> findByFlight(Flight flight);

    List<Ticket> findByFlightId(Long flightId);
}
