package io.upschool.controller;

import io.upschool.dto.ticket.TicketSaveRequest;
import io.upschool.dto.ticket.TicketSaveResponse;
import io.upschool.dto.ticket.TicketUpdateRequest;
import io.upschool.entity.Ticket;
import io.upschool.exception.ResourceAlreadyDeletedException;
import io.upschool.exception.ResourceNotFoundException;
import io.upschool.service.TicketService;
import io.upschool.dto.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {
    private final TicketService ticketService;

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<TicketSaveResponse>> getTicketById(@PathVariable Long id) {
        BaseResponse<TicketSaveResponse> ticketSaveResponse = ticketService.getTicketById(id);
        if (ticketSaveResponse.isSuccess()) {
            return ResponseEntity.ok(ticketSaveResponse);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ticketSaveResponse);
        }
    }
    @PostMapping("/purchase")
    public ResponseEntity<BaseResponse<TicketSaveResponse>> purchaseTicket(@RequestBody TicketSaveRequest ticketRequest) {
        BaseResponse<TicketSaveResponse> response = ticketService.purchaseTicket(ticketRequest);
        if (response.isSuccess()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
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

    @GetMapping("/pnr/{pnr}")
    public BaseResponse<List<TicketSaveResponse>> getTicketsByPnr(@PathVariable String pnr) {
        List<TicketSaveResponse> ticketResponses = ticketService.getTicketsByPnr(pnr)
                .stream()
                .map(ticketService::convertToResponse)
                .collect(Collectors.toList());

        if (!ticketResponses.isEmpty()) {
            return BaseResponse.<List<TicketSaveResponse>>builder()
                    .status(200)
                    .isSuccess(true)
                    .data(ticketResponses)
                    .build();
        } else {
            return BaseResponse.<List<TicketSaveResponse>>builder()
                    .status(404)
                    .isSuccess(false)
                    .error("No tickets found with PNR starting with: " + pnr)
                    .build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<TicketSaveResponse>> updateTicket(@PathVariable Long id,
                                                                         @RequestBody TicketUpdateRequest ticketUpdateRequest) {
        BaseResponse<TicketSaveResponse> updatedTicketResponse = ticketService.updateTicket(id, ticketUpdateRequest);
        if (updatedTicketResponse.isSuccess()) {
            return ResponseEntity.ok(updatedTicketResponse);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(updatedTicketResponse);
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> deleteTicket(@PathVariable Long id) {
        try {
            ticketService.softDeleteTicket(id);
            return ResponseEntity.ok(BaseResponse.<Void>builder()
                    .status(HttpStatus.OK.value())
                    .isSuccess(true)
                    .data(null)
                    .build());
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (ResourceAlreadyDeletedException e) {
            var response = BaseResponse.<Void>builder()
                    .status(HttpStatus.BAD_REQUEST.value())
                    .error(e.getMessage())
                    .isSuccess(false)
                    .data(null)
                    .build();
            return ResponseEntity.badRequest().body(response);
        }
    }
    @PutMapping("/cancel")
    public ResponseEntity<BaseResponse<TicketSaveResponse>> cancelTicketByPnr(@RequestParam String pnr) {
        BaseResponse<TicketSaveResponse> response = ticketService.cancelTicketByPnr(pnr);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
