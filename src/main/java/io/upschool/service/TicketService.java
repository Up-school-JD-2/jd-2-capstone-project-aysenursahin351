package io.upschool.service;

import io.upschool.entity.Ticket;
import io.upschool.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TicketService {
    @Autowired
    private TicketRepository repository;

    public Ticket saveTicket(Ticket ticket){
       return repository.save(ticket) ;
    }
    public Ticket saveTickets(List<Ticket> tickets){
        return (Ticket) repository.saveAll(tickets);
    }

    public List<Ticket> getTickets(){
        return repository.findAll() ;
    }

    public Ticket getTicketById(int id){
        return repository.findById(id).orElse(null) ;
    }
    public Ticket getTicketByName(String name){
        return repository.findByName(name) ;
    }
    public String deleteTicket(int id){
        repository.deleteById(id);
        return "ticket canceled!!"+id  ;
    }
    public Ticket updateTicket(Ticket ticket){
        Ticket existingTicket=repository.findById(ticket.getId()).orElse(null);
        existingTicket.setName(ticket.getName());
        existingTicket.setQuantity(ticket.getQuantity());
        existingTicket.setPrice(ticket.getPrice());
        return repository.save(existingTicket);
    }
}
