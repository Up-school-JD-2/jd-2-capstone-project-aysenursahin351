package io.upschool.controller;

import io.upschool.dto.ticket.TicketSaveRequest;
import io.upschool.dto.ticket.TicketSaveResponse;
import io.upschool.service.TicketService;
import io.upschool.dto.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {
    private final TicketService ticketService;

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<TicketSaveResponse>> getTicketById(@PathVariable Long id) {
        BaseResponse<TicketSaveResponse> TicketSaveResponse = ticketService.getTicketById(id);
        if (TicketSaveResponse.isSuccess()) {
            return ResponseEntity.ok(TicketSaveResponse);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(TicketSaveResponse);
        }
    }

    @PostMapping
    public ResponseEntity<BaseResponse<TicketSaveResponse>> saveTicket(@RequestBody TicketSaveRequest ticketRequest) {
        BaseResponse<TicketSaveResponse> savedTicket = ticketService.saveTicket(ticketRequest);
        if (savedTicket.isSuccess()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(savedTicket);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(savedTicket);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTicket(@PathVariable Long id) {
        ticketService.deleteTicket(id);
        return ResponseEntity.noContent().build();
    }
}
