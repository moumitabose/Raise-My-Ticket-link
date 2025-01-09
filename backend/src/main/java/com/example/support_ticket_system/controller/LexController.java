package com.example.support_ticket_system.controller;

import com.example.support_ticket_system.service.LexService;
import com.example.support_ticket_system.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class LexController {


    private final LexService lexService;
    private final TicketService ticketService;

    @Autowired
    public LexController(LexService lexService,TicketService ticketService) {
        this.lexService = lexService;
        this.ticketService = ticketService;
    }

    @GetMapping("/askLex")
    public String askLex(@RequestParam String userInput) {
        return lexService.processUserInput(userInput);
    }

    @PostMapping("/createFromLex")
    public void createTicketFromLex(@RequestBody Map<String, String> requestBody) {
        // Extract user input and session ID from request body
        String userInput = requestBody.get("userInput");
        String sessionId = requestBody.get("sessionId");

        // Validate the inputs
        if (userInput == null || userInput.isEmpty()) {
            throw new IllegalArgumentException("User input is required.");
        }
        if (sessionId == null || sessionId.isEmpty()) {
            throw new IllegalArgumentException("Session ID is required.");
        }

        // Call the service method with both the user input and session ID
        ticketService.createTicketFromLex(userInput, sessionId);
    }




}
