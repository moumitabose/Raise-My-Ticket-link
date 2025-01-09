package com.example.support_ticket_system.service;

import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

@Service
public class SqsMessageProcessor {

    private final SqsClient sqsClient;
    private final String sqsQueueUrl = "https://sqs.us-east-2.amazonaws.com/412381741682/SupportTicketQueue";

    public SqsMessageProcessor(SqsClient sqsClient) {
        this.sqsClient = sqsClient;
    }


    public void processMessages() {
        ReceiveMessageRequest receiveRequest = ReceiveMessageRequest.builder()
                .queueUrl(sqsQueueUrl)
                .maxNumberOfMessages(10) // Batch processing up to 10 messages
                .build();

        sqsClient.receiveMessage(receiveRequest).messages().forEach(message -> {
            System.out.println("Processing message: " + message.body());

            // Delete the message after processing
            DeleteMessageRequest deleteRequest = DeleteMessageRequest.builder()
                    .queueUrl(sqsQueueUrl)
                    .receiptHandle(message.receiptHandle())
                    .build();
            sqsClient.deleteMessage(deleteRequest);
        });
    }

}
