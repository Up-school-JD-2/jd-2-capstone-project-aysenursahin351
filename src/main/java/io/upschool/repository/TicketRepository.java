package io.upschool.repository;

import io.upschool.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository <Ticket,Integer> {

    Ticket findByName(String name);
}
