package com.example.support_ticket_system.controller;


import com.example.support_ticket_system.service.SqsMessageProcessor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tickets")
public class SqsController {

    private final SqsMessageProcessor sqsMessageProcessor;

    public SqsController(SqsMessageProcessor sqsMessageProcessor) {
        this.sqsMessageProcessor = sqsMessageProcessor;
    }

    @GetMapping("/process")
    public ResponseEntity<String> processMessages() {
        sqsMessageProcessor.processMessages();
        return new ResponseEntity<>("Messages processed successfully!", HttpStatus.OK);
    }
}
