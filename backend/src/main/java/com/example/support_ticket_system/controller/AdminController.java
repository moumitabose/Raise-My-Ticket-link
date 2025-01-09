package com.example.support_ticket_system.controller;

import com.example.support_ticket_system.model.Ticket;
import com.example.support_ticket_system.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final TicketService ticketService;

    @Autowired
    public AdminController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    // Endpoint to fetch all tickets
    @GetMapping("/tickets")
    public List<Ticket> getAllTickets() {
        return ticketService.getAllTickets();
    }

    // Endpoint to assign a ticket to a specific agent
    @PostMapping("/tickets/{email}/{issueType}/assign")
    public String assignTicketToAgent(@PathVariable String email,
                                      @PathVariable String issueType,
                                      @RequestParam String agent) {
        ticketService.assignTicketToAgent(email, issueType, agent);
        return "Ticket assigned to agent " + agent;
    }

    // Endpoint to change the status of a ticket
    @PatchMapping("/tickets/{email}/{issueType}/status")
    public String changeTicketStatus(@PathVariable String email,
                                     @PathVariable String issueType,
                                     @RequestParam String status) {
        ticketService.changeTicketStatus(email, issueType, status);
        return "Ticket status changed to " + status;
    }

    // Endpoint to mark a ticket as resolved
    @PatchMapping("/tickets/{email}/{issueType}/resolve")
    public String resolveTicket(@PathVariable String email,
                                @PathVariable String issueType) {
        ticketService.changeTicketStatus(email, issueType, "Resolved");
        return "Ticket marked as resolved";
    }
}
