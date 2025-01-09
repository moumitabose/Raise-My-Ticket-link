package com.example.support_ticket_system.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class SqsScheduledProcessor {
    private final SqsMessageProcessor sqsMessageProcessor;

    public SqsScheduledProcessor(SqsMessageProcessor sqsMessageProcessor) {
        this.sqsMessageProcessor = sqsMessageProcessor;
    }

    @Scheduled(fixedRate = 300000) // Run every 30 seconds
    public void scheduleMessageProcessing() {
        System.out.println("Running scheduled SQS message processing...");
        sqsMessageProcessor.processMessages();
    }

}
