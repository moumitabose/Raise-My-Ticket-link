package com.example.support_ticket_system.controller;


import com.example.support_ticket_system.model.Agent;
import com.example.support_ticket_system.model.Ticket;
import com.example.support_ticket_system.service.TicketService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/agents")
public class AgentController {


    private final TicketService ticketService;

    public AgentController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<Agent>> getAllAgents() {
        return ResponseEntity.ok(ticketService.getAllAgents());
    }


    @PostMapping("/create")
    public ResponseEntity<Map<String, String>> addAgent(@RequestBody Agent agent) {
        ticketService.addAgent(agent);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Agent added successfully");
        return ResponseEntity.ok(response);
    }

    @PutMapping("/auto-assign")
    public ResponseEntity<String> autoAssignTicket(
            @RequestParam String email,
            @RequestParam String issueType,
            @RequestParam String category,
            @RequestParam String region) {
        try {
            ticketService.autoAssignTicket(email, issueType, category, region);
            return ResponseEntity.ok("Ticket auto-assigned to an agent successfully");
        } catch (RuntimeException ex) {
            return ResponseEntity.status(400).body(ex.getMessage());
        }
    }

    @GetMapping("/{agent}")
    public ResponseEntity<List<Ticket>> getTicketsByAgentId(@PathVariable String agent) {
        System.out.println(agent);
        List<Ticket> tickets = ticketService.getTicketsByAgentId(agent);
        System.out.println(tickets.size());
        return ResponseEntity.ok(tickets);
    }
}
