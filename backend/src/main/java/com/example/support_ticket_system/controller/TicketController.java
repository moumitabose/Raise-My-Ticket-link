package com.example.support_ticket_system.controller;

import com.example.support_ticket_system.model.Ticket;
import com.example.support_ticket_system.service.ComprehendService;
import com.example.support_ticket_system.service.TicketService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;
    private final ComprehendService comprehendService;

    public TicketController(TicketService ticketService, ComprehendService comprehendService) {
        this.ticketService = ticketService;
        this.comprehendService = comprehendService;

    }

    @PostMapping("/send")
    public ResponseEntity<Map<String, String>> createTicket(@RequestBody Ticket ticket) {
        // Call service to process the ticket
        ticketService.createTicket(ticket);

        // Create a response map with the success message
        Map<String, String> response = new HashMap<>();
        response.put("message", "Ticket created and processed successfully!");

        // Return the response as JSON with HTTP status CREATED (201)
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


//    @PostMapping("/createFromLex")
//    public void createTicketFromLex(@RequestParam String userInput) {
//        ticketService.createTicketFromLex(userInput);
//    }


    @GetMapping("/getAllTickets")
    public List<Ticket> getAllTickets() {
        return ticketService.getAllTickets();  // Calls the service method to fetch tickets
    }


    @PutMapping("/resolve")
    public ResponseEntity<Map<String, String>> markTicketAsResolved(@RequestBody Ticket ticket) {
        Map<String, String> response = new HashMap<>();
        try {
            // Call the service to mark the ticket as resolved
            ticketService.markTicketAsResolved(ticket.getEmail(), ticket.getIssueType());

            // Return a success response with a map
            response.put("message", "Ticket marked as resolved successfully.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // Return an error response with a map
            response.put("message", "Failed to resolve ticket: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/ticketsByEmaiId")
    public List<Ticket> getTicketsByEmail(@RequestBody Ticket ticket) {
        return ticketService.getTicketsByEmail(ticket.getEmail());
    }

    @GetMapping("/detectSentiment")
    public String detectSentiment(@RequestParam String text) {
        return comprehendService.detectSentiment(text);
    }

    // Endpoint to detect entities
    @GetMapping("/detectEntities")
    public String detectEntities(@RequestParam String text) {
        return comprehendService.detectEntities(text);
    }

    // Endpoint to detect key phrases
    @GetMapping("/detectKeyPhrases")
    public String detectKeyPhrases(@RequestParam String text) {
        return comprehendService.detectKeyPhrases(text);
    }

    // Endpoint to detect syntax
    @GetMapping("/detectSyntax")
    public String detectSyntax(@RequestParam String text) {
        return comprehendService.detectSyntax(text);
    }


}
