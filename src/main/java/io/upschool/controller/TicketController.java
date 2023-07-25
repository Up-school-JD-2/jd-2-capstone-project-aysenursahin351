package io.upschool.controller;

import io.upschool.entity.Ticket;
import io.upschool.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TicketController {
    @Autowired
    private TicketService service;

    @PostMapping("/addTicket")
    public Ticket addticket(@RequestBody Ticket ticket ){
        return service.saveTicket(ticket);
    }
    @PostMapping("/addTickets")
    public List<Ticket> addTickets(@RequestBody List<Ticket> tickets){
        return (List<Ticket>) service.saveTickets(tickets);//??
    }
    @GetMapping("/tickets")
    public List<Ticket> findAllProducts(){
        return service.getTickets();
    }
    @GetMapping("/ticket/{id}")
    public Ticket findTicketById(@PathVariable int id){
        return service.getTicketById(id);
    }
    @GetMapping("/ticketByName/{name}")
    public Ticket findTicketByName(@PathVariable String name){
        return service.getTicketByName(name);
    }
    @PutMapping("/update")
    public Ticket updateTicket(@RequestBody Ticket ticket){
        return service.updateTicket(ticket);
    }
    @DeleteMapping("/delete/{id}")
    public String deleteTicket(@PathVariable int id){
        return  service.deleteTicket(id);
    }
}
